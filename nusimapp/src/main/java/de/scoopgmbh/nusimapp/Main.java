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
import de.scoopgmbh.nusimapp.http.SSLContextFactory;
import de.scoopgmbh.nusimapp.http.SSLServerConfig;
import de.scoopgmbh.nusimapp.if1.LoadKPubDPHandler;
import de.scoopgmbh.nusimapp.server.ErrorMessages;
import de.scoopgmbh.nusimapp.server.HttpHandler;
import de.scoopgmbh.nusimapp.server.api.configuration.GetConfigurationHandler;
import de.scoopgmbh.nusimapp.server.api.configuration.SetConfigurationHandler;
import de.scoopgmbh.nusimapp.server.api.logging.WebsocketLogAppender;
import de.scoopgmbh.nusimapp.server.api.provisioning.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Handler;
import ratpack.http.Response;
import ratpack.server.RatpackServer;
import ratpack.websocket.WebSockets;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {

    static {
        // init java.util.logging to SLF4J bridge
        // see https://www.slf4j.org/api/org/slf4j/bridge/SLF4JBridgeHandler.html
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {

        System.setProperty("org.jooq.no-logo", "true");

        final WebsocketLogAppender o = new WebsocketLogAppender(500, Duration.ofMillis(500), e -> System.err.println("discarding log"), null);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(o);
        o.start();

        final LoaderAppApplicationContext context = new LoaderAppApplicationContext();
        try {
            context.start();

            final Config config = context.getConfig();

            final Config provisioningConfig = config.getConfig("provisioning");
            if (provisioningConfig.hasPath("demoModeFeature") && provisioningConfig.getBoolean("demoModeFeature")) {
                logger.warn("Application is running in DEMO MODE !!!");
            }

            final Config serverConf = config.getConfig("server");
            SSLServerConfig sslConfig = ConfigBeanFactory.create(serverConf.getConfig("ssl"), SSLServerConfig.class);

            final String strippedPathPrefix;
            if (serverConf.hasPath("pathPrefix") && !serverConf.getString("pathPrefix").trim().equals("/")) {
                strippedPathPrefix = StringUtils.strip(serverConf.getString("pathPrefix"), "/");
            } else {
                strippedPathPrefix = "";
            }

            RatpackServer server = RatpackServer.start(s -> s
                        .serverConfig(d -> {
                                    if (System.getenv("NUSIM_APP_DEV") != null) {
                                        Path webPath = Paths.get(System.getenv("NUSIM_APP_DEV")).toRealPath();
                                        d.baseDir(webPath);
                                        logger.info("WebDir path is {}", webPath);
                                        logger.warn("Using development mode");
                                        d.development(true);
                                    } else if (System.getProperty("APP_HOME") != null) {
                                        Path webPath = Paths.get(System.getProperty("APP_HOME"), "web").toRealPath();
                                        d.baseDir(webPath);
                                        logger.info("WebDir path is {}", webPath);
                                        d.development(false);
                                    } else {
                                        Path path = Paths.get(serverConf.getString("baseDir"), "web");
                                        if (Files.exists(path)) {
                                            Path webPath = path.toRealPath();
                                            d.baseDir(webPath);
                                            logger.info("WebDir path is {}", webPath);
                                            d.development(false);
                                        } else {
                                            throw new FileNotFoundException("WebDir path " + path + " does not exist; please set APP_HOME correctly, so that APP_HOME contains a subfolder named 'web'");
                                        }
                                    }

                                    if (sslConfig.isEnabled()) {
                                        d.ssl(new SSLContextFactory().createServerContext(sslConfig));
                                    }
                                    d.port(serverConf.getInt("port"));
                                    // FIX: single core machine hangs because we need more than two threads. So use at least 4
                                    d.threads(Math.max(Runtime.getRuntime().availableProcessors() * 2, 4));
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
                        .handlers(rootChain -> {

                            Action<Chain> mainChainAction = mainChain -> {

                                Handler dynamicFileHandler = ctx -> {
                                    final String href = strippedPathPrefix.isEmpty() ? "/" : ("/" + strippedPathPrefix + "/");
                                    Path file = ctx.file("index.html");
                                    Stream<String> stream = Files.lines(file).map(s1 -> s1.replaceAll("<base href=[^ ]*", "<base href=\"" + href + "\""));
                                    String collect = stream.collect(Collectors.joining());
                                    Response response = ctx.getResponse();
                                    response.getHeaders().add("content-type", "text/html");
                                    response.send(collect);
                                };
                                mainChain.get("", dynamicFileHandler);
                                mainChain.get("configuration", dynamicFileHandler);
                                mainChain.get("about", dynamicFileHandler);

                                mainChain.files(f -> f.dir("").indexFiles("index.html"));
                                mainChain.files(f -> f.path("configuration").dir("").indexFiles("index.html"));
                                mainChain.files(f -> f.path("about").dir("").indexFiles("index.html"));

                                mainChain.options("", ctx -> ctx.render(""));

                                mainChain.prefix("api/v1", apiChain -> {
                                    apiChain.put("provisioning/status", new SetProvisioningStatusHandler(context.getProvisioning()));
                                    apiChain.path("configuration", ctx ->
                                        ctx.byMethod(m -> {
                                            m.get(new GetConfigurationHandler(context::getConfig));
                                            m.put(new SetConfigurationHandler(PathUtil.getAppHomeDir().resolve("conf").resolve("nusimapp.conf")));
                                        })
                                    );
                                    apiChain.path("provisioning/loadKPubDP", new LoadKPubDPHandler(context::getConfig, context.getConfig().getConfig("provisioning").getString("rootKeyId"), context.getDAO(), context.getIf1Adapter(), new GetConfigurationHandler(context::getConfig), new SetConfigurationHandler(PathUtil.getAppHomeDir().resolve("conf").resolve("nusimapp.conf"))));
                                    apiChain.path("provisioning/loadCertificates", new LoadCertificatesHandler(context.getConfig().getConfig("provisioning").getString("rootKeyId"), context.getDAO(), context.getIf3Adapter()));
                                    apiChain.path("provisioning/requestProfiles", new RequestProfilesHandler(context.getProvisioning(), context.getDAO()));
                                    apiChain.path("provisioning/retrieveProfiles", new RetrieveProfilesHandler(context.getProvisioning(), context.getDAO()));
                                    apiChain.path("provisioning/referenceIds", new GetReferenceIdsHandler(context.getDAO()));
                                    apiChain.delete("provisioning/referenceId/:referenceId", new DeleteReferenceIdHandler(context.getDAO()));
                                    apiChain.path("provisioning/queryProfileStock", new QueryProfileStockHandler(context.getProvisioning()));

                                    apiChain.get("logging/live", ctx -> {
                                        HttpHandler.logWebsocketConnection(ctx.getRequest());
                                        WebSockets.websocketBroadcast(ctx, o.getStream());
                                    });
                                    apiChain.get("server/state", ctx -> {
                                        HttpHandler.logWebsocketConnection(ctx.getRequest());
                                        WebSockets.websocketBroadcast(ctx, context.getServerStateProvider().getStream());
                                    });
                                });
                            };
                            if (!strippedPathPrefix.isEmpty()) {
                                logger.info("configuring server with pathPrefix /{}", strippedPathPrefix);
                                rootChain.prefix(strippedPathPrefix, mainChainAction);
                            } else {
                                logger.info("configuring server without pathPrefix");
                                mainChainAction.execute(rootChain);
                            }
                            rootChain.all(ctx -> ctx.clientError(404));
                        })
            );
            System.out.println(String.format("Server started successfully, listening at %s://%s:%s/%s", server.getScheme(), server.getBindHost(), server.getBindPort(), strippedPathPrefix));
        } catch (Exception e) {
            logger.error("Failed to start gui server: {}", e.toString(), e);
            System.err.println("Failed to start gui server: " + e.toString());
            Throwable rootCause = ExceptionUtils.getRootCause(e);

            if (rootCause != null && rootCause != e) {
                System.err.println("Caused by: " + rootCause.toString());
            }
            context.stop();
            System.exit(1);
        }

    }

}
