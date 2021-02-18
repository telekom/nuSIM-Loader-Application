package de.scoopgmbh.nusimapp.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
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
//        httpClient.setRequestBufferSize(httpSenderConfig.getMaxContentSize());
//        httpClient.setResponseBufferSize(httpSenderConfig.getMaxContentSize());
        httpClient.start();
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
