package de.scoopgmbh.nusimapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NusimSimHTTPAdapter;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import ratpack.handling.Context;
import ratpack.http.TypedData;
import ratpack.jackson.JsonRender;

import java.util.Random;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class LoadProfileHandler extends HttpHandler {

    private final Random random = new Random(System.currentTimeMillis());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        NusimSimHTTPAdapter.LoadProfileRequest request = ctx.parse(requestBody, fromJson(NusimSimHTTPAdapter.LoadProfileRequest.class, objectMapper));

        NusimSimHTTPAdapter.LoadProfileResponse response = new NusimSimHTTPAdapter.LoadProfileResponse(Long.toHexString(random.nextLong()).toUpperCase());

        JsonRender json = json(response);
        responseContainer.body = json.getObject();

        ctx.render(json);
    }
}
