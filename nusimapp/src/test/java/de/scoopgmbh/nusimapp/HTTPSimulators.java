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
                                        apiChain.post("if3/GetCertFromCM", new GetCertFromCMHandler());
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
