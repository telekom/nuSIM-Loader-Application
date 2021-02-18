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
