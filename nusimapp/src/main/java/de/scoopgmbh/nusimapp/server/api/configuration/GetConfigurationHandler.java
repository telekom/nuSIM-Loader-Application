package de.scoopgmbh.nusimapp.server.api.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.io.IOException;
import java.util.Base64;
import java.util.function.Supplier;

public class GetConfigurationHandler extends HttpHandler {

    private final Supplier<? extends Config> config;

    public GetConfigurationHandler(final Supplier<? extends Config> config) {
        this.config = config;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) throws Exception {
        ctx.render(readConfiguration().root().render(ConfigRenderOptions.concise()));
    }

    public Config readConfiguration() throws IOException {
        Config currentConfig = this.config.get();
        return currentConfig.withValue("database.password", ConfigValueFactory.fromAnyRef(new String(Base64.getDecoder().decode(currentConfig.getString("database.password")))));
    }
}
