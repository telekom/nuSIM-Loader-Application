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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.ProxyAuthenticationProtocolHandler;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class HttpSender {
    private static final Logger logger = LoggerFactory.getLogger(HttpSender.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpSenderConfig httpSenderConfig;
    private final String clientName;
    private URI baseURI;
    private static final AtomicLong callId = new AtomicLong(1);
    private HttpClient httpClient;
    private static final long CONNECT_TIMEOUT = 60_000;

    public HttpSender(HttpSenderConfig httpSenderConfig, String clientName) {
        this.httpSenderConfig = httpSenderConfig;
        this.clientName = clientName;
    }

    public void start() throws Exception {
        final SslContextFactory sslContext;
        baseURI = new URI(httpSenderConfig.getUrl());
        if (baseURI.getScheme().equals("https")) {
            sslContext = new SSLContextFactory().createJettyContextFactory(httpSenderConfig.getSsl(), clientName);
        } else {
            sslContext = null;
        }

        httpClient = new HttpClient(sslContext);
        httpClient.setConnectTimeout(CONNECT_TIMEOUT);

        if (!Strings.isNullOrEmpty(httpSenderConfig.getProxyURI())) {
            try {
                configureProxy();
            } catch (URISyntaxException | RuntimeException e) {
                throw new Exception("Could not configure proxy for " + clientName + ": " + e);
            }
        }
//        httpClient.setRequestBufferSize(httpSenderConfig.getMaxContentSize());
//        httpClient.setResponseBufferSize(httpSenderConfig.getMaxContentSize());
        httpClient.start();
    }

    private void configureProxy() throws URISyntaxException {
        URI proxyURI = new URI(httpSenderConfig.getProxyURI());
        logger.debug("configuring {} to proxy requests via {}", clientName, proxyURI);
        httpClient.getProxyConfiguration().getProxies().add(new HttpProxy(proxyURI.getHost(), proxyURI.getPort()));

        if (!Strings.isNullOrEmpty(httpSenderConfig.getProxyUser())) {
            logger.debug("configuring basic auth for {} proxy {} using user '{}' and pass '*****'", clientName, proxyURI, Authentication.ANY_REALM, httpSenderConfig.getProxyUser());

            AuthenticationStore auth = httpClient.getAuthenticationStore();
            auth.addAuthentication(new BasicAuthentication(
                    proxyURI,
                    Authentication.ANY_REALM,
                    httpSenderConfig.getProxyUser(),
                    httpSenderConfig.getProxyPass()));
            httpClient.getProtocolHandlers().put(new ProxyAuthenticationProtocolHandler(httpClient));
        }
    }

    public void stop() {
        if (httpClient != null) {
            try {
                httpClient.stop();
            } catch (Exception e) {
                logger.warn("could not safely stop httpclient for {}: {}", clientName, e.toString());
            }
        }
    }

    public <REQ, RES> RES send(String relativeUri, REQ request, Class<RES> responseClass) throws Exception {
        final long theCallId = callId.getAndIncrement();
        final URI uri = baseURI.resolve(relativeUri);

        final Request r = httpClient.POST(uri);
        final String json = objectMapper.writeValueAsString(request);
        r.header(HttpHeader.ACCEPT, "application/json");
        r.content(new StringContentProvider(json), "application/json");

        if (logger.isTraceEnabled()) {
            logger.trace("OUT #{}: POST request for {} headers: {} body: {}", theCallId, uri, r.getHeaders().stream().map(HttpField::toString).collect(Collectors.joining(",")), json);
        }

        InputStreamResponseListener listener = new InputStreamResponseListener();
        r.send(listener);

        Response response = listener.get(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);

        if (response.getStatus() == 200) {
            RES res = objectMapper.readValue(listener.getInputStream(), responseClass);
            if (logger.isTraceEnabled()) {
                logger.trace("OUT #{}: Response {} for {} headers: {} body: {}", theCallId, response.getStatus(), uri, r.getHeaders().stream().map(HttpField::toString).collect(Collectors.joining(",")), objectMapper.writeValueAsString(res));
            }
            return res;
        } else {
            Exception exception = new Exception("HTTP " + response.getStatus());
            response.abort(exception);
            throw exception;
        }


    }
}
