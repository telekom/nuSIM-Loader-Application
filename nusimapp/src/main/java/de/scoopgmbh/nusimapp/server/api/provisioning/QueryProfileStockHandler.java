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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.if1.QueryProfileStockResponse;
import de.scoopgmbh.nusimapp.provisioning.ProvisioningModule;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.util.List;

public class QueryProfileStockHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(QueryProfileStockHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ProvisioningModule provisioning;

    private static final class Request {
        public String refInfo1;
        public String refInfo2;
        public String refInfo3;
    }

    private static final class Response {
        public List<QueryProfileStockResponse.Stock> stockList;
    }

    public QueryProfileStockHandler(ProvisioningModule provisioning) {
        this.provisioning = provisioning;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {

        try {
            Request request = objectMapper.readValue(requestBody.getInputStream(), Request.class);
            logger.info("start querying profile stock for (refInfo1={}, refInfo2={}, refInfo3={})", request.refInfo1, request.refInfo2, request.refInfo3);

            Response response = new Response();
            QueryProfileStockResponse.ResultPart r = provisioning.queryProfileStock(request.refInfo1, request.refInfo2, request.refInfo3);
            response.stockList = r.getStockList();
            logger.info("finished querying profile stock. {} stock entries found", response.stockList.size());
            ctx.render(objectMapper.writeValueAsString(response));
        } catch (Exception ex) {
            ctx.error(ex);
        }
    }
}
