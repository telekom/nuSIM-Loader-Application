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
