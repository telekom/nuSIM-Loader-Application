package de.scoopgmbh.nusimapp.server.api.provisioning;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.util.List;

public class GetReferenceIdsHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(GetReferenceIdsHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DAO dao;

    private static final class Response {
        public List<ReferenceIdRecord> entries;
    }

    public GetReferenceIdsHandler(DAO dao) {
        this.dao = dao;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        try {
            logger.debug("start requesting open referenceIds");
            List<ReferenceIdRecord> referenceIds = dao.getReferenceIds();
            final Response r = new Response();
            r.entries = referenceIds;
            logger.info("finished requesting open referenceIds: {} found", referenceIds.size());
            ctx.render(objectMapper.writeValueAsString(r));
        } catch (Exception ex) {
            ctx.error(ex);
        }
    }
}
