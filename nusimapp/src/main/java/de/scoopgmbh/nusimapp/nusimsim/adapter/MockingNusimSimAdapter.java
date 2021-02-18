package de.scoopgmbh.nusimapp.nusimsim.adapter;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Random;

public class MockingNusimSimAdapter extends NusimSimAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MockingNusimSimAdapter.class);
    private final Random random = new Random(System.currentTimeMillis());

    public MockingNusimSimAdapter(Config adapterConfig) {
        super(adapterConfig);
    }

    @Override
    public String getEID() {
        try {
            Thread.sleep(random.nextInt() % 1000 + 1000);
        } catch (InterruptedException ignore) {
        }
        return Long.toHexString(random.nextLong());
    }

    @Override
    public String getCertNusim() {
        return Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());
    }

    @Override
    public String getNusimCapabilities() {
        return "XY";
    }

    @Override
    public String loadProfile(String eid, String mac, String encP, String eKPubDP, @Nullable String sigEKPubDP, @Nullable String kPubDP, @Nullable String sigKPubDP, @Nullable String kPubCI) {
        logger.info("loading eid='{}', mac='{}', eKPubDB='{}'", eid, mac, eKPubDP);
        return Long.toHexString(random.nextLong()).toUpperCase();
    }

}
