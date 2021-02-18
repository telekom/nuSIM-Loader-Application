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

public class GetSimDataHandler extends HttpHandler {

    private final Random random = new Random(System.currentTimeMillis());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        NusimSimHTTPAdapter.GetSimDataRequest request = ctx.parse(requestBody, fromJson(NusimSimHTTPAdapter.GetSimDataRequest.class, objectMapper));

        final NusimSimHTTPAdapter.GetSimDataResponse response = new NusimSimHTTPAdapter.GetSimDataResponse();

        switch (request.getType()) {
            case "eid":
                response.setEid(Long.toHexString(random.nextLong()));
                break;
            case "certNUSIM":
                response.setCert(Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()));
                break;
            case "capabilities":
                response.setCapabilities("XY");
                break;
            case "iccid":
                response.setIccid(Long.toHexString(random.nextLong()).toUpperCase());
                break;
        }

        JsonRender json = json(response);
        responseContainer.body = json.getObject();

        ctx.render(json);
    }
}
