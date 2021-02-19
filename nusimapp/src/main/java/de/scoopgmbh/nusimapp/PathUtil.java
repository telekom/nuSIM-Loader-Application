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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);

    private static Path appHomeDir;

    private PathUtil() {
    }


    public static void initAppHomeDir() throws IOException {
        String env = System.getenv("NUSIMAPP_HOME_DIR");
        if (env != null && !env.isEmpty()) {
            appHomeDir = Paths.get(env);
            logger.info("NUSIMAPP_HOME_DIR is set. Using NUSIMAPP_HOME_DIR as application home directory: {}", appHomeDir);
        } else {
            appHomeDir = Paths.get(System.getProperty("user.dir"), "nusim-data");
            logger.info("NUSIMAPP_HOME_DIR is not set. Using Default, based on current working directory {}", appHomeDir);
        }

        if (Files.notExists(appHomeDir)) {
            logger.warn("application home directory {} does not exist. Trying to create it", appHomeDir);
            Files.createDirectories(appHomeDir);
        } else {
            if (!Files.isDirectory(appHomeDir)) {
                throw new IOException("Not a directory: " + appHomeDir);
            }
        }

        Files.createDirectories(appHomeDir.resolve("conf"));
    }

    public static Path getAppHomeDir() {
        if (appHomeDir == null) {
            throw new IllegalStateException("uninitialized appHomeDir. Initalize explicitly!");
        }
        return appHomeDir;
    }

    public static Path resolvePath(String path) {
        Path thePath = Paths.get(path);
        if (!thePath.isAbsolute()) {
            thePath = appHomeDir.resolve(thePath);
        }
        return thePath;
    }
}
