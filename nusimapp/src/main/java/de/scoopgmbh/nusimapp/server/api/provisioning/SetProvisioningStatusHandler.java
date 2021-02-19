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

package de.scoopgmbh.nusimapp.server.api.provisioning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.provisioning.ProvisioningModule;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import ratpack.handling.Context;
import ratpack.http.TypedData;

public class SetProvisioningStatusHandler extends HttpHandler {
    private final ProvisioningModule provisioning;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SetProvisioningStatusHandler(ProvisioningModule provisioning) {
        this.provisioning = provisioning;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {

        JsonNode jsonNode = objectMapper.readTree(requestBody.getText());
        final boolean startRequested = jsonNode.get("status").asBoolean();
        if (startRequested) {
            final int provisioningCount = jsonNode.get("count").asInt();
            final String refInfo1 = jsonNode.get("refInfo1").asText();
            final String refInfo2 = jsonNode.get("refInfo2").asText();
            final String refInfo3 = jsonNode.get("refInfo3").asText();
            provisioning.start(provisioningCount, refInfo1, refInfo2, refInfo3);
        } else {
            provisioning.stop();
        }
        ctx.render(Boolean.toString(provisioning.isStarted()));
    }
}
