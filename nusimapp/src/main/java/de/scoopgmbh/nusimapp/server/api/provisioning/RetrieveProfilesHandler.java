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
import de.scoopgmbh.nusimapp.db.txn.TransactionRuntimeException;
import de.scoopgmbh.nusimapp.if1.IF1Exception;
import de.scoopgmbh.nusimapp.if1.RetrieveBulkEncProfilesResponse;
import de.scoopgmbh.nusimapp.provisioning.ProvisioningModule;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.util.List;
import java.util.stream.Collectors;

public class RetrieveProfilesHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveProfilesHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ProvisioningModule provisioning;
    private final DAO dao;

    private static final class Request {
        public String referenceId;
    }

    private static final class Response {
        public String referenceId;
        public String errorCode;
        public int retrievedTotal;
        public int retrievedGood;
        public int addedLocally;
    }

    public RetrieveProfilesHandler(ProvisioningModule provisioning, DAO dao) {
        this.provisioning = provisioning;
        this.dao = dao;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        try {
            Request request = objectMapper.readValue(requestBody.getInputStream(), Request.class);
            logger.info("start retrieving profiles for referenceId {}", request.referenceId);

            Response response = new Response();
            response.referenceId = request.referenceId;
            try {
                dao.runTransactional(db -> {
                    dao.finishReferenceId(db, request.referenceId);
                    RetrieveBulkEncProfilesResponse.ResultPart r = provisioning.retrieveBulkEncProfiles(request.referenceId);
                    response.retrievedTotal = r.getProfileList().size();
                    List<RetrieveBulkEncProfilesResponse.Profile> goodProfiles = r.getProfileList().stream().filter(p -> {
                        if (p.getErrorCode() != null) {
                            logger.error("retrieved error {}: {}", p.getErrorCode(), p.getErrorMessage());
                            return false;
                        } else {
                            return true;
                        }
                    }).collect(Collectors.toList());
                    response.retrievedGood = goodProfiles.size();
                    response.addedLocally = dao.addProfileData(db, goodProfiles, provisioning.getkPubDP(), provisioning.getSigKpubDP());

                });
                logger.info("finished retrieving profiles for referenceId {}: {} answers, {} without error, {} stored locally", request.referenceId, response.retrievedTotal, response.retrievedGood, response.addedLocally);
            } catch (TransactionRuntimeException e) {
                if (e.getCause() instanceof IF1Exception && "405".equals(((IF1Exception) e.getCause()).getErrorCode())) {
                    response.errorCode = ((IF1Exception) e.getCause()).getErrorCode();
                } else {
                    throw e.getCause();
                }
            }

            ctx.render(objectMapper.writeValueAsString(response));
        } catch (Throwable ex) {
            ctx.error(ex);
        }
    }
}
