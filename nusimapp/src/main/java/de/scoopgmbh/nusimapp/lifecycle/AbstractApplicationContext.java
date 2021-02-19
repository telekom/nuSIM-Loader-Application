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

package de.scoopgmbh.nusimapp.lifecycle;

import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import de.scoopgmbh.nusimapp.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractApplicationContext implements ApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationContext.class);
    private static final ConfigParseOptions configParseOptions = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.CONF); // allow comments in json
    private final Map<String, Managed> managedMap = new LinkedHashMap<>();
    private Config defaultConfig;

    public AbstractApplicationContext() {
    }

    private Config locateDefaultConfig() throws IOException {
        if (System.getProperty("APP_HOME") != null) {
            Path path = Paths.get(System.getProperty("APP_HOME"), "defaultConfig.json").toRealPath();
            logger.debug("reading default configuration from {}", path);
            return ConfigFactory.parseFile(path.toFile(), configParseOptions);
        } else {
            logger.info("reading default configuration from classpath");
            return ConfigFactory.parseResources("defaultConfig.json", configParseOptions);
        }
    }

    public void start() throws Exception {
        try {
            PathUtil.initAppHomeDir();
        } catch (IOException e) {
            throw new IOException("could not initialize application home directory: " + e.getMessage());
        }

        defaultConfig = locateDefaultConfig();

        if (logger.isDebugEnabled()) {
            logger.debug(getConfig().root().render());
        }

        logger.info("Initializing application context");

        init();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "app-shutdown"));

        logger.info("Starting managed modules");
        for (Managed managed : managedMap.values()) {
            logger.info("Starting '" + managed.getName() + "'");
            try {
                managed.start();
            } catch (Exception e) {
                logger.error("Error starting '" + managed.getName() + "': " + e.toString(), e);
                throw new Exception("Error starting managed module '" + managed.getName() + "'", e);
            }
        }

        logger.info("Application started successfully");
    }


    /**
     * startup initialisation should happen here
     */
    protected abstract void init() throws Exception;

    /**
     * Hook to be overridden by subclasses.
     */
    protected void onStop() {
    }

    private void shutdown() {
        logger.info("Application shutdown begins");
        stop();
        logger.info("Application shutdown finished");
    }

    public void stop() {
        logger.info("Stopping managed modules");
        for (Managed managed : Lists.reverse(new ArrayList<>(managedMap.values()))) {
            try {
                logger.info("Stopping '" + managed.getName() + "'");
                managed.stop();
            } catch (Exception e) {
                logger.error("Error stopping managed module '" + managed.getName() + "':" + e.getMessage(), e);
            }
        }
        try {
            onStop();
        } catch (Exception e) {
            logger.error("Error in onStop hook", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Managed> T getManaged(String name) {
        T managed = (T) managedMap.get(name);
        if (managed == null) {
            throw new IllegalArgumentException("Unknown managed module name='" + name + "'");
        }
        return managed;
    }

    protected <T extends Managed> T addManaged(T managed) {
        String name = managed.getName();
        if (managedMap.containsKey(name)) {
            throw new IllegalStateException("Managed module with the name='" + name + "' already registered");
        }
        managedMap.put(name, managed);
        return managed;
    }

    protected <T extends Managed> T createAndAddManaged(Supplier<T> supplier) {
        T managed = supplier.get();
        return addManaged(managed);
    }

    public Config getConfig() {
        final Path userConfigPath = PathUtil.getAppHomeDir().resolve("conf").resolve("nusimapp.conf");
        final Config userConfig;
        if (Files.exists(userConfigPath)) {
            userConfig = ConfigFactory.parseFile(userConfigPath.toFile(), configParseOptions);
            logger.debug("Reading user configuration {}", userConfigPath);
        } else {
            logger.debug("User configuration does not exist. Keeping default configuration");
            userConfig = ConfigFactory.empty();
        }

        return userConfig.withFallback(defaultConfig);
    }

}
