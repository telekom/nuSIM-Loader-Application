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

package de.scoopgmbh.nusimapp.provisioning;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ProvisioningLogWriter implements AutoCloseable {

    private final Path fileName;
    private final Instant startTime;
    private final int provisioningCount;
    private final String refInfo1;
    private final String refInfo2;
    private final String refInfo3;

    private PrintWriter writer = null;

    public ProvisioningLogWriter(Path fileName, Instant startTime, int provisioningCount, String refInfo1, String refInfo2, String refInfo3) {
        this.fileName = fileName;
        this.startTime = startTime;
        this.provisioningCount = provisioningCount;
        this.refInfo1 = refInfo1;
        this.refInfo2 = refInfo2;
        this.refInfo3 = refInfo3;
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }

    private PrintWriter getWriter() throws IOException {
        if (writer == null) {

            final String refInfoString = String.format("(refInfo1=%s, refInfo2=%s, refInfo3=%s)", refInfo1, refInfo2, refInfo3);

            writer = new PrintWriter(Files.newBufferedWriter(fileName));
            writer.println("# Provisioning started at " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(startTime) + refInfoString);
            writer.println("# Requested number of SIMs to provision: " + provisioningCount);
            writer.println("Index, ICCID, EID");
        }
        return writer;
    }

    public void write(final ProvisioningModule.ProvisioningResult provisioningResult) throws IOException {
        getWriter().println(provisioningResult.toString());
    }

    public void writeSuccess() throws IOException {
        if (writer != null) {
            writer.println("# Success");
        }
    }

    public void writeException(Exception e) throws IOException {
        if (writer != null) {
            writer.println("# Aborted: " + e.toString());
        }
    }

}
