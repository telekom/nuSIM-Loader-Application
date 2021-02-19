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
