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

import javax.annotation.Nullable;

/**
 * Adaptor for IF.2
 */
public abstract class NusimSimAdapter {
    private static final int BITMASK_DB_AUTH = 0x0001;
    private final Config adapterConfig;

    /**
     * This constructor will be called by the adapter factory.
     * <p>
     * See {@code NusimSimHTTPAdapter} for a reference implementation. <br>
     * See {@code NusimSimAdapterFactory} for the adapter factory. <br>
     * See {@code LoaderAppApplicationContext.getNusimSimAdaptor()} for accessing the adapter.
     *
     * @param adapterConfig the configuration section specific for this adapter
     */
    protected NusimSimAdapter(Config adapterConfig) {
        this.adapterConfig = adapterConfig;
    }

    protected Config getConfig() {
        return adapterConfig;
    }

    public boolean isDPAuthSupported(final String nusimCapabilities) {
        return (Integer.valueOf(nusimCapabilities, 16) & BITMASK_DB_AUTH) == BITMASK_DB_AUTH;
    }

    /**
     * This method shall return an EID or - if no EID can be provided at the time
     * of invocation - shall block until an EID is available. Only if an EID is
     * known to be unavailable for a longer period of time, the method may throw
     * a NoEidAvailableException.
     *
     * @return a valid EID
     * @throws NoEidAvailableException if the adapter is unable to provide an EID
     */
    public abstract String getEID() throws NoEidAvailableException;

    /**
     * Get the NUSIM certificate.
     *
     * @return the NUSIM Certificate in PEM Format
     */
    public abstract String getCertNusim();

    /**
     * Get the NUSIM capabilities.
     *
     * @return the capabilities hex string, of the form "1001"
     */
    public abstract String getNusimCapabilities();

    /**
     * Upload a profile.
     *
     * @param eid        the EID (unique ID of the Eagle SIM), long form, not null, 32 digits
     * @param mac        the MAC, not null, 16 bytes in hex
     * @param encP       the profile, not null, base64 encoded
     * @param eKPubDP    ephemeral public ECC key, created by DP, not null, base64 encoded
     * @param sigEKPubDP signature of {@code eKPubDP}, created by DP, optional, base64 encoded
     * @param kPubDP     public ECC key of the Eagle SIM, optional, base64 encoded
     * @param sigKPubDP  signature of {@code kPubDP}, created by a CI, optional, base64 encoded
     * @param kPubCI     public ECC key of the CI, optional, base64 encoded
     * @return the ICCID of the loaded profile
     * @throws ProfileLoadingException in case of any errors
     */
    public abstract String loadProfile(
            final String eid,
            final String mac,
            final String encP,
            final String eKPubDP,
            @Nullable final String sigEKPubDP,
            @Nullable final String kPubDP,
            @Nullable final String sigKPubDP,
            @Nullable final String kPubCI)
            throws ProfileLoadingException;

    /**
     * Called by the framework when the loader application is about to be
     * stopped. Gives implementors of this abstract class a chance to cleanup.
     * The default implementation does nothing.
     */
    public void stop() throws Exception {
    }

}
