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
