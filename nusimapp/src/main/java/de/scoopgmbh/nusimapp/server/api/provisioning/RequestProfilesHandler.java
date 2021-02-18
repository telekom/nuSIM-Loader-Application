package de.scoopgmbh.nusimapp.server.api.provisioning;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.db.txn.TransactionRuntimeException;
import de.scoopgmbh.nusimapp.if1.RequestBulkEncProfilesRequest;
import de.scoopgmbh.nusimapp.if1.RequestBulkEncProfilesResponse;
import de.scoopgmbh.nusimapp.provisioning.ProvisioningModule;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RequestProfilesHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestProfilesHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ProvisioningModule provisioning;
    private final DAO dao;

    private static final class Request {
        public int count;
        public String refInfo1;
        public String refInfo2;
        public String refInfo3;
    }

    private static final class Response {
        public String referenceId;
        public Instant retrieveTime;
        public int requested;
    }

    public RequestProfilesHandler(ProvisioningModule provisioning, DAO dao) {
        this.provisioning = provisioning;
        this.dao = dao;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        try {
            Request request = objectMapper.readValue(requestBody.getInputStream(), Request.class);
            logger.info("start requesting {} profiles for EIDs (refInfo1={}, refInfo2={}, refInfo3={})", request.count, request.refInfo1, request.refInfo2, request.refInfo3);

            Response response = new Response();


            try {
                dao.runTransactional(db -> {
                    List<RequestBulkEncProfilesRequest.Eid> unrequestedEIDs = dao.getUnrequestedEIDs(db, request.count);
                    if (unrequestedEIDs.isEmpty()) {
                        throw new Exception("No EIDs (with known certificate) available in local database.");
                    }
                    logger.info("retrieved {} EIDs from Database", unrequestedEIDs.size());
                    RequestBulkEncProfilesResponse.ResultPart requestBulkEncProfilesResponse = provisioning.requestBulkEncProfiles(unrequestedEIDs, request.refInfo1, request.refInfo2, request.refInfo3);
                    ZonedDateTime retrieveTime = ZonedDateTime.now().plus(Integer.valueOf(requestBulkEncProfilesResponse.getWaitTime()), ChronoUnit.MINUTES);
                    dao.addReferenceId(db, unrequestedEIDs, requestBulkEncProfilesResponse.getReferenceId(), retrieveTime, request.count, request.refInfo1, request.refInfo2, request.refInfo3);
                    response.referenceId = requestBulkEncProfilesResponse.getReferenceId();
                    response.retrieveTime = retrieveTime.toInstant();
                    response.requested = unrequestedEIDs.size();
                });
            } catch (TransactionRuntimeException e) {
                if (e.getCause() instanceof Exception) {
                    throw ((Exception) e.getCause());
                } else {
                    throw e;
                }
            }

            logger.info("finished requesting {} profiles. referenceId {} is available at {}", request.count, response.referenceId, response.retrieveTime);
            ctx.render(objectMapper.writeValueAsString(response));
        } catch (Exception ex) {
            ctx.error(ex);
        }
    }
}
