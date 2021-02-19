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
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.if3.IF3Adapter;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

public class LoadCertificatesHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoadCertificatesHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String rootKeyId;
    private final DAO dao;
    private final IF3Adapter if3Adapter;

    public LoadCertificatesHandler(String rootKeyId, DAO dao, IF3Adapter if3Adapter) {
        this.rootKeyId = rootKeyId;
        this.dao = dao;
        this.if3Adapter = if3Adapter;
    }

    @SuppressWarnings("WeakerAccess")
    public static final class LoadCertificatesResponse {
        /** total number of EIDs in database */
        public final int total;
        /** number of EIDs processed in this request (ie. number of EIDs without cert) */
        public final int processed;
        /** number of EIDs processed sucessfully during this request */
        public final int success;
        /** number of EIDs processed with error during this request */
        public final int error;

        public LoadCertificatesResponse(int total, int processed, int success, int error) {
            this.total = total;
            this.processed = processed;
            this.success = success;
            this.error = error;
        }
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) {
        try {
            logger.info("start retrieving certificates for EIDs from CM");
            LoadCertificatesResponse response = dao.visitBulkEIDs(eid -> {
                logger.info("retrieving certificate for EID {}", eid);
                return if3Adapter.getCertificate(eid, rootKeyId);
            });
            logger.info("finished retrieving certificates. {} total EIDs in DB, {} EIDs handled, {} successfully, {} errors", response.total, response.processed, response.success, response.error);
            ctx.render(objectMapper.writeValueAsString(response));
        } catch (Exception ex) {
            ctx.error(ex);
        }
    }
}
