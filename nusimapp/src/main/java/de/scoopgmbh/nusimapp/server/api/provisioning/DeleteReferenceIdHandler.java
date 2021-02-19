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

import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

public class DeleteReferenceIdHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(DeleteReferenceIdHandler.class);

    private final DAO dao;

    public DeleteReferenceIdHandler(DAO dao) {
        this.dao = dao;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        final String referenceId = ctx.getPathTokens().get("referenceId");
        try {
            logger.debug("removing referenceId {}", referenceId);
            dao.deleteReferenceId(referenceId);
            logger.info("finished removing referenceId {}", referenceId);
            ctx.render(referenceId);
        } catch (Exception ex) {
            ctx.error(ex);
        }
    }
}
