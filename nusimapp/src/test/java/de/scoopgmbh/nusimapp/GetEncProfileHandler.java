package de.scoopgmbh.nusimapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.if1.GetEncProfileRequest;
import de.scoopgmbh.nusimapp.if1.GetEncProfileResponse;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import ratpack.handling.Context;
import ratpack.http.TypedData;
import ratpack.jackson.JsonRender;

import java.util.Random;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class GetEncProfileHandler extends HttpHandler {

    private final Random random = new Random(System.currentTimeMillis());
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        GetEncProfileRequest request = ctx.parse(requestBody, fromJson(GetEncProfileRequest.class, objectMapper));

        String eKPubDP = Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());
        String mac = Long.toHexString(random.nextLong());
        GetEncProfileResponse.ResultPart r = new GetEncProfileResponse.ResultPart();
        r.setEid(request.getEid());
        r.seteKpubDP(eKPubDP);
        r.setMac(mac);
        r.setReqVersion(request.getReqVersion());

        GetEncProfileResponse response = new GetEncProfileResponse();
        response.setStatus("Success");
        response.setResult(r);

        JsonRender json = json(response);
        responseContainer.body = json.getObject();

        ctx.render(json);
    }
}
