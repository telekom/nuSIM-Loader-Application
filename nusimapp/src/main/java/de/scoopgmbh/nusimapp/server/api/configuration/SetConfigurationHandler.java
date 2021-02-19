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

package de.scoopgmbh.nusimapp.server.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.ConfigValueFactory;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.TypedData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class SetConfigurationHandler extends HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(SetConfigurationHandler.class);
    private static final ConfigParseOptions configParseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path targetDir;

    public SetConfigurationHandler(final Path targetDir) {
        this.targetDir = targetDir;
    }

    @Override
    protected void handleRequest(Context ctx, TypedData requestBody, ResponseContainer responseContainer) {
        Config newConfig = ConfigFactory.parseString(requestBody.getText(), configParseOptions);

        try {
            storeConfiguration(newConfig);
            ctx.render("OK");
        } catch (IOException e) {
            logger.error("could not store configuration into {}: {}", targetDir, e.getMessage());
            ctx.error(e);
        }
    }

    public void storeConfiguration(Config newConfig) throws IOException {
        newConfig = newConfig.withValue("database.password", ConfigValueFactory.fromAnyRef(new String(Base64.getEncoder().encode(newConfig.getString("database.password").getBytes()))));

        String render = newConfig.root().render(ConfigRenderOptions.concise().setJson(true).setComments(false).setFormatted(true).setOriginComments(false));
        Files.write(targetDir, render.getBytes());
        logger.info("successfully stored configuration into {}", targetDir);
    }
}
