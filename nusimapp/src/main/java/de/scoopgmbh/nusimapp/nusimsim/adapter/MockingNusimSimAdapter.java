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
