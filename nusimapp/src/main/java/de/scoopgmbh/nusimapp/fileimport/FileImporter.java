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

package de.scoopgmbh.nusimapp.fileimport;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.PathUtil;
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.lifecycle.Managed;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileImporter implements Managed {
    public static final String NAME = "FileImporter";
    public static final Pattern CERT_FILENAME_PATTERN = Pattern.compile("cert_(.*)\\.pem");
    public static final Pattern IGNORE_LINES_PATTERN = Pattern.compile("^(#.*|\\s*)$");
    private static final Logger logger = LoggerFactory.getLogger(FileImporter.class);
    private static final int DELAY = 5;
    private final Config config;
    private final DAO dao;
    private Path importDir;
    private Path doneDir;
    private Path errorDir;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("fileimport").build());

    public FileImporter(Config config, DAO dao) {
        this.config = config;
        this.dao = dao;

        ArchiveStreamFactory.findAvailableArchiveInputStreamProviders().forEach((k,v) -> {
            logger.debug("we can read archvives of type '{}'", k);
        });
        CompressorStreamFactory.findAvailableCompressorInputStreamProviders().forEach((k,v) -> {
            logger.debug("we can read compressed files of type '{}'", k);
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        Path importBaseDir = PathUtil.resolvePath(config.getString("importBaseDir"));

        this.importDir = getDir(importBaseDir, "in");
        this.doneDir = getDir(importBaseDir, "out");
        this.errorDir = getDir(importBaseDir, "error");

        logger.info("starting processing files located at {}", importDir);
        executor.schedule(this::run, 0, TimeUnit.SECONDS);
    }

    private Path getDir(final Path baseDir, final String path) throws IOException {
        final Path targetPath = baseDir.resolve(path);

        if (Files.notExists(targetPath)) {
            Files.createDirectories(targetPath);
            logger.info("created directory {}", targetPath);
        }
        if (!Files.isDirectory(targetPath)) {
            throw new IOException(targetPath + " is not a directory");
        }
        return targetPath;
    }

    private void process(final List<String> fileEndings, final CheckedConsumer<Path> pathConsumer) throws IOException {
        try (Stream<Path> list = Files.list(importDir).filter((p) -> {
            for (String suffix : fileEndings) {
                if (p.toString().endsWith(suffix)) {
                    return true;
                }
            }
            return false;
        })) {
            list.sorted().forEach(path -> {
                try {
                    if (!Files.isReadable(path)) {
                        logger.warn("ignoring non-readable file {}", path);
                    } else {
                        logger.info("starting processing file {}", path);
                        String resultMessage = pathConsumer.consume(path);
                        logger.info("successfully processed file {}: {}", path, resultMessage);
                        try {
                            Files.move(path, doneDir.resolve(path.getFileName()), REPLACE_EXISTING);
                        } catch (IOException e1) {
                            throw new RuntimeException("could not move file: " + e1);
                        }
                    }
                } catch (IOException | ArchiveException | RuntimeException e) {
                    try {
                        Files.move(path, errorDir.resolve(path.getFileName()), REPLACE_EXISTING);
                    } catch (IOException e1) {
                        throw new RuntimeException("could not move file: " + e1);
                    }
                    throw new RuntimeException("could not process file " + path + ": " + e);
                }
            });
        }
    }

    private void run() {
        try {
            process(config.getStringList("eidFileEndings"), (path) -> {
                int i = dao.createBulkRecord(Files.lines(path).map(String::trim).filter(s -> ! IGNORE_LINES_PATTERN.matcher(s).matches()).peek(s -> logger.debug("importing EID '{}'", s)));
                return i + " EIDs imported";
            });

            process(config.getStringList("certificateFileEndings"), (path) -> {
                AtomicInteger count = new AtomicInteger(0);
                InputStream inStream;
                try {
                    inStream = new CompressorStreamFactory().createCompressorInputStream(new BufferedInputStream(Files.newInputStream(path), 64000));
                    logger.debug("{} is compressed", path);
                } catch (CompressorException ex) {
                    inStream = Files.newInputStream(path);
                    logger.debug("{} is uncompressed", path);
                }

                try (ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(inStream, 64000))) {
                    ArchiveEntry entry;
                    while ((entry = i.getNextEntry()) != null) {

                        if (!entry.isDirectory()) {
                            File f = new File(entry.getName());
                            Matcher matcher = CERT_FILENAME_PATTERN.matcher(f.getName());
                            if (!matcher.matches()) {
                                logger.warn("ignoring badly named file {}", f.getName());
                            } else {
                                final String eid = matcher.group(1);
                                logger.debug("importing certificate for EID '{}'", eid);
                                dao.createBulkRecord(eid, new String(IOUtils.toByteArray(i)));
                                count.incrementAndGet();
                            }
                        }
                    }
                }
                return count.intValue()+" certificates imported";
            });
        } catch (RuntimeException | IOException ex) {
            logger.error("could not process files: {}", ex.toString());
        } finally {
            executor.schedule(this::run, DELAY, TimeUnit.SECONDS);
        }
    }

    @Override
    public void stop() throws Exception {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }

    @FunctionalInterface
    public interface CheckedConsumer<T> {
        String consume(T t) throws IOException, ArchiveException, DataAccessException;
    }
}
