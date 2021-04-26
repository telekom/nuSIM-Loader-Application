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

package de.scoopgmbh.nusimapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import de.scoopgmbh.nusimapp.http.SSLContextFactory;
import de.scoopgmbh.nusimapp.http.SSLServerConfig;
import de.scoopgmbh.nusimapp.server.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.error.ServerErrorHandler;
import ratpack.handling.Context;
import ratpack.server.RatpackServer;

import java.util.Collections;

public class HTTPSimulators {
    private static final Logger logger = LoggerFactory.getLogger(HTTPSimulators.class);


    public static void main(String... args) {

        final Config config = ConfigFactory.empty()
                .withValue("enabled", ConfigValueFactory.fromAnyRef(true))
                .withValue("certDir", ConfigValueFactory.fromAnyRef("server/src/test/resources/selfsignedcerts"))
                .withValue("password", ConfigValueFactory.fromAnyRef(""))
                .withValue("supportedCipherSuites", ConfigValueFactory.fromAnyRef(Collections.singletonList("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256")))
                .withValue("supportedProtocols", ConfigValueFactory.fromAnyRef(Collections.singletonList("TLSv1.2")));


        try {
            RatpackServer.start(server -> {
                server
                        .serverConfig(d -> {
                            d.development(false);
                                    d.port(9090);
                            d.ssl(new SSLContextFactory().createServerContext(ConfigBeanFactory.create(config, SSLServerConfig.class)));
                                }
                        )
                        .registryOf(r -> r
                                .add(ServerErrorHandler.class, (ctx, error) -> {
                                    logger.error(error.getMessage(), error);
                                    ctx.getResponse().status(ErrorMessages.DEFAULT.getHttpStatus()).send(error.toString());
                                })
                                .add(new ObjectMapper()
                                        .registerModule(new JavaTimeModule())
                                        .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false
                                        ))
                        )
                        .handlers(mainChain -> {
                                    mainChain.options("", ctx -> ctx.render(""));
                                    mainChain.prefix("sim", apiChain -> {
                                        apiChain.post("if1/GetKPubDP", new GetKPubDPHandler());
                                        apiChain.post("if1/GetEncProfile", new GetEncProfileHandler());
                                        apiChain.post("nusim/GetSimData", new GetSimDataHandler());
                                        apiChain.post("nusim/LoadProfile", new LoadProfileHandler());
                                    });
                            mainChain.all(Context::notFound);
                                }
                        );

            });
        } catch (Exception e) {
            logger.error("Failed to start simulator", e);
            System.exit(1);
        }
    }
}
