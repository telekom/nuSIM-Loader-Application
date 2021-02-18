package de.scoopgmbh.nusimapp.if1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import de.scoopgmbh.nusimapp.server.api.configuration.GetConfigurationHandler;
import de.scoopgmbh.nusimapp.server.api.configuration.SetConfigurationHandler;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.util.function.Supplier;

public class LoadKPubDPHandler extends HttpHandler {

    private final Supplier<? extends Config> config;
    private final String rootKeyId;
    private final DAO dao;
    private final IF1Adapter if1Adapter;
    private final GetConfigurationHandler getConfigurationHandler;
    private final SetConfigurationHandler setConfigurationHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoadKPubDPHandler(Supplier<? extends Config> config, final String rootKeyId, DAO dao, IF1Adapter if1Adapter, GetConfigurationHandler getConfigurationHandler, SetConfigurationHandler setConfigurationHandler) {
        this.config = config;
        this.rootKeyId = rootKeyId;
        this.dao = dao;
        this.if1Adapter = if1Adapter;
        this.getConfigurationHandler = getConfigurationHandler;
        this.setConfigurationHandler = setConfigurationHandler;
    }

    private static final class Request {
        public boolean overwrite = false;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        try {
            Request request = objectMapper.readValue(requestBody.getInputStream(), Request.class);
            Config config = getConfigurationHandler.readConfiguration();

            if (!request.overwrite && config.getConfig("if1").getConfig("KpubDP").hasPath("KpubDP")) {
                ctx.render("EXISTS");
                return;
            }

            final GetKPubDPRequest getKPubDPRequest = new GetKPubDPRequest(rootKeyId);
            final GetKPubDPResponse kPubDPResponse = if1Adapter.getKPubDP(getKPubDPRequest);

            if (kPubDPResponse.getResult() != null) {
                config = config.withValue("if1.KpubDP.KpubDP", ConfigValueFactory.fromAnyRef(kPubDPResponse.getResult().getkPubDP()));
                config = config.withValue("if1.KpubDP.KpubCI", ConfigValueFactory.fromAnyRef(kPubDPResponse.getResult().getkPubCI()));
                config = config.withValue("if1.KpubDP.SigKpubDP", ConfigValueFactory.fromAnyRef(kPubDPResponse.getResult().getSigKpubDP()));
            }

            setConfigurationHandler.storeConfiguration(config);
            ctx.render("OK");
        } catch (Exception ex) {
            ctx.error(ex);
        }

    }
}
