package de.scoopgmbh.nusimapp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Request;
import ratpack.http.Response;
import ratpack.http.TypedData;

import java.util.concurrent.atomic.AtomicLong;

public abstract class HttpHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    private static final AtomicLong callId = new AtomicLong(1);

    public static void logWebsocketConnection(Request request) {
        if (logger.isTraceEnabled()) {
            logger.trace("IN #{}: {} WS-Request for {} [ " +
                            "timestamp: {}, " +
                            "remoteAddress: {}, " +
                            "localAddress: {}, " +
                            "uri: {}, " +
                            "headers: {} ]",
                    callId.incrementAndGet(),
                    request.getMethod(),
                    request.getUri(),
                    request.getTimestamp(),
                    request.getRemoteAddress(),
                    request.getLocalAddress(),
                    request.getUri(),
                    request.getHeaders().asMultiValueMap());
        }
    }

    @Override
    public void handle(Context ctx) {
        ResponseContainer responseContainer = new ResponseContainer();
        final long theCallId = callId.getAndIncrement();
        ctx.getRequest().getBody()
                .onError(e -> logger.error("Reading request body for {} failed.", ctx.getRequest().getUri()))
                .then(body -> {
                    logRequest(ctx.getRequest(), body, theCallId);
                    asyncHandle(ctx, body, responseContainer).then(success -> {

                        if (logger.isTraceEnabled()) {
                            logResponse(ctx.getResponse(), responseContainer.body, ctx.getRequest().getUri(), theCallId);
                        }
                    });
                });
    }

    private Promise<Boolean> asyncHandle(Context ctx, TypedData requestBody, ResponseContainer responseContainer) {
        return Promise.async(down -> {
            handleRequest(ctx, requestBody, responseContainer);
            down.success(true);
        });
    }

    protected abstract void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception;

    void logRequest(Request request, TypedData requestBody, long theCallId) {
        logger.trace("IN #{}: {} Request for {} [ " +
                        "timestamp: {}, " +
                        "remoteAddress: {}, " +
                        "localAddress: {}, " +
                        "uri: {}, " +
                        "headers: {}, " +
                        "body: {} ]",
                theCallId,
                request.getMethod(),
                request.getUri(),
                request.getTimestamp(),
                request.getRemoteAddress(),
                request.getLocalAddress(),
                request.getUri(),
                request.getHeaders().asMultiValueMap(),
                requestBody.getText());
    }

    void logResponse(Response response, Object responseBody, String requestUri, long theCallId) {
        logger.trace("IN #{}: Response for {} [ " +
                        "status: \"{}\", " +
                        "headers: {}, " +
                        "body: {} ]",
                theCallId,
                requestUri,
                response.getStatus(),
                response.getHeaders().asMultiValueMap(),
                responseBody);
    }

    protected static class ResponseContainer {
        public Object body;
    }
}
