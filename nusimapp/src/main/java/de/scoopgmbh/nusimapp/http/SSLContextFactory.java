/*
 * nusim-loader
 *
 * (c) 2020 Deutsche Telekom AG.
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package de.scoopgmbh.nusimapp.http;

import de.scoopgmbh.nusimapp.PathUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SSLContextFactory {
    private static final Logger logger = LoggerFactory.getLogger(SSLContextFactory.class);
    private static final String EPHEMERAL_KEYSTORE_PASSWORD = "we don't care";

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        Security.insertProviderAt(new BouncyCastleJsseProvider(), 1);

        for (Provider provider : Security.getProviders()) {
            logger.debug("activated Security Provider {} ({})", provider.getName(), provider.getClass());
            provider.getServices().stream().sorted(Comparator.comparing(s -> s.getType() + s.getAlgorithm())).forEach(s -> logger.debug("{} provides {} {}", provider.getName(), s.getType(), s.getAlgorithm()));
        }
    }

    public SSLContextFactory() {
    }

    public SslContext createServerContext(final SSLServerConfig sslConfig) throws IOException {
        final Path certDir = PathUtil.resolvePath(sslConfig.getCertDir());

        // Create SSL socket factory
        SslContext ctx = SslContextBuilder.forServer(Files.newInputStream(certDir.resolve("server.crt")), Files.newInputStream(certDir.resolve("server.key")))
                .ciphers(sslConfig.getSupportedCipherSuites())
                .protocols(sslConfig.getSupportedProtocols().toArray(new String[0]))
                .build();
        logger.info("successfully created SSLContext");
        return ctx;
    }

    public SslContext createClientContext(final SSLClientConfig sslConfig, final String clientName) throws IOException {
        final Path certDir = PathUtil.resolvePath(sslConfig.getCertDir());

        // Create SSL socket factory
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().trustManager(Files.newInputStream(certDir.resolve("trusted.crt")));
        Path clientCert = certDir.resolve("client.crt");
        Path clientKey = certDir.resolve("client.key");
        if (Files.exists(clientCert) && Files.exists(clientKey)) {
            sslContextBuilder = sslContextBuilder.keyManager(Files.newInputStream(clientCert), Files.newInputStream(clientKey));
            logger.debug("{} configured with client certificate {} and key {}", clientName, clientCert, clientKey);
        } else {
            logger.debug("{} configured without client certificate or key", clientName);
        }

        SslContext ctx = sslContextBuilder.build();
        logger.info("successfully created SSLContext for {}", clientName);
        return ctx;
    }

    private static PrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        LinkedList<String> keyAlgorithms = new LinkedList<>(Security.getAlgorithms("KeyFactory"));
        // ensure EC and RSA are first in list, if available
        if (keyAlgorithms.remove("RSA")) keyAlgorithms.add(0, "RSA");
        if (keyAlgorithms.remove("EC")) keyAlgorithms.add(0, "EC");

        InvalidKeySpecException lastEx = null;
        for (String keyAlgorithm : keyAlgorithms) {
            try {
                return KeyFactory.getInstance(keyAlgorithm).generatePrivate(spec);
            } catch (InvalidKeySpecException e) {
                lastEx = e;
            }
        }
        if (lastEx != null) throw lastEx;
        else throw new InvalidKeySpecException("no matching algorithm found for key");
    }

    private static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        final CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }

    private static PrivateKey createPrivateKey(File privateKeyPem) throws Exception {
        final BufferedReader r = new BufferedReader(new FileReader(privateKeyPem));
        String s = r.readLine();
        if (s == null || !s.contains("BEGIN") || !s.contains("PRIVATE KEY")) {
            r.close();
            throw new IllegalArgumentException("No PRIVATE KEY found");
        }
        final StringBuilder b = new StringBuilder();
        s = "";
        while (s != null) {
            if (s.contains("END") && s.contains("PRIVATE KEY")) {
                break;
            }
            b.append(s);
            s = r.readLine();
        }
        r.close();
        final String hexString = b.toString();
        final byte[] bytes = DatatypeConverter.parseBase64Binary(hexString);
        return generatePrivateKeyFromDER(bytes);
    }

    private static X509Certificate[] createCertificates(File certificatePem) throws Exception {
        final List<X509Certificate> result = new ArrayList<>();
        final BufferedReader r = new BufferedReader(new FileReader(certificatePem));
        String s = r.readLine();
        if (s == null || !s.contains("BEGIN CERTIFICATE")) {
            r.close();
            throw new IllegalArgumentException("No CERTIFICATE found");
        }
        StringBuilder b = new StringBuilder();
        while (s != null) {
            if (s.contains("END CERTIFICATE")) {
                String hexString = b.toString();
                final byte[] bytes = DatatypeConverter.parseBase64Binary(hexString);
                X509Certificate cert = generateCertificateFromDER(bytes);
                result.add(cert);
                b = new StringBuilder();
            } else {
                if (!s.startsWith("----")) {
                    b.append(s);
                }
            }
            s = r.readLine();
        }
        r.close();

        return result.toArray(new X509Certificate[0]);
    }

    /**
     * Create a KeyStore from standard PEM files
     */
    public static KeyStore createKeyStore(File privateKeyPem, File certificatePem, final String password) throws Exception {
        final X509Certificate[] cert = createCertificates(certificatePem);
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        // Import private key
        final PrivateKey key = createPrivateKey(privateKeyPem);
        keystore.setKeyEntry(privateKeyPem.getName(), key, password == null ? "abc".toCharArray() : password.toCharArray(), cert);
        return keystore;
    }

    public static KeyStore createTrustStore(File trustedPem) throws Exception {
        final X509Certificate[] certs = createCertificates(trustedPem);
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        // Import private key
        for (X509Certificate cert : certs) {
            keystore.setCertificateEntry(cert.getIssuerDN().getName(), cert);
            logger.info("adding {} from {}", cert.getIssuerDN(), trustedPem.getAbsolutePath());
        }

        return keystore;
    }

    public SslContextFactory createJettyContextFactory(final SSLClientConfig sslConfig, final String clientName) throws Exception {
        final Path certDir = PathUtil.resolvePath(sslConfig.getCertDir());
        Path clientCert = certDir.resolve("client.crt");
        Path clientKey = certDir.resolve("client.key");
        Path trustedCert = certDir.resolve("trusted.crt");

        SslContextFactory f = new SslContextFactory(false);
        f.setKeyStore(createKeyStore(clientKey.toFile(), clientCert.toFile(), EPHEMERAL_KEYSTORE_PASSWORD));
        f.setKeyStorePassword(EPHEMERAL_KEYSTORE_PASSWORD);
        f.setTrustStore(createTrustStore(trustedCert.toFile()));
        
        return f;
    }
}
