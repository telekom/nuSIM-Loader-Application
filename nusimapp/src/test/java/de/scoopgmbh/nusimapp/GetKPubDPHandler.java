package de.scoopgmbh.nusimapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.if1.GetKPubDPRequest;
import de.scoopgmbh.nusimapp.if1.GetKPubDPResponse;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import ratpack.handling.Context;
import ratpack.http.TypedData;
import ratpack.jackson.JsonRender;

import java.util.Random;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class GetKPubDPHandler extends HttpHandler {

    private final Random random = new Random(System.currentTimeMillis());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        GetKPubDPRequest request = ctx.parse(requestBody, fromJson(GetKPubDPRequest.class, objectMapper));

        GetKPubDPResponse response = new GetKPubDPResponse();

        response.setStatus("Success");
        response.setResult(new GetKPubDPResponse.ResultPart());
        response.getResult().setkPubCI("BEUWaTcKOcFKoWYEipOcYGHcYGZBjohftxtNWVZH4C2ih1U6U4oIGyy84u7HkEP03GdDcUVvul2Bi+WLNTk7O/Q=");
        response.getResult().setkPubDP("BBt8xGgltev0y6A9Tb/cHdwusy8Q9yylK2T5xY7f7ONmg3bHQdMVmOOMHCLoc7rSDaFQa8+QBMMF3L6HgoNdWPo=");
        response.getResult().setReqVersion("1.0");
        response.getResult().setSigKpubDP("QhiwEki4yxZiO6ulZ2h7liOPLXAemlbtm5+16JqAEQlZAwHWRPM1nLy7ErlMBWZC79xdThem8wWmNvO8wdHp9g==");

//        String kPubDP = Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());
//        String sigKPubDP = Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());
//        String kPubCI = Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());
//        GetKPubDPResponse response = new GetKPubDPResponse(request.getReqVersion(), kPubDP, sigKPubDP, kPubCI);

        JsonRender json = json(response);
        responseContainer.body = json.getObject();

        ctx.render(json);
    }
}
