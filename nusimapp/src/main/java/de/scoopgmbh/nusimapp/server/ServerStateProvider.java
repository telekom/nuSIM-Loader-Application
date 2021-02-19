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

package de.scoopgmbh.nusimapp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.scoopgmbh.nusimapp.provisioning.ProvisioningModule;
import org.reactivestreams.Publisher;
import ratpack.stream.Streams;

import java.time.Duration;
import java.util.concurrent.Executors;

public class ServerStateProvider {

    private final ObjectWriter ow = new ObjectMapper().writer();
    private final Publisher<String> stream;

    public ServerStateProvider(final Duration pollInterval, ProvisioningModule provisioning) {
        stream = Streams.periodically(Executors.newSingleThreadScheduledExecutor(), pollInterval, (i) -> {
            final ServerState state = new ServerState();

            state.setProvisioningInProgress(provisioning.isStarted());

            return ow.writeValueAsString(state);
        });
    }

    public Publisher<String> getStream() {
        return stream;
    }

    public static final class ServerState {
        private boolean provisioningInProgress;

        public boolean isProvisioningInProgress() {
            return provisioningInProgress;
        }

        public void setProvisioningInProgress(boolean provisioningInProgress) {
            this.provisioningInProgress = provisioningInProgress;
        }
    }
}
