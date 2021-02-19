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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import de.scoopgmbh.nusimapp.http.HttpSender;
import de.scoopgmbh.nusimapp.http.HttpSenderConfig;

import javax.annotation.Nullable;

public class NusimSimHTTPAdapter extends NusimSimAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NusimSimHTTPAdapter.class);

    private HttpSender sender;

    /**
     * creates a new instance of an NusimSimAdapter. This constructor is called
     * by the LoaderApplication
     *
     * @param adapterConfig a specific config subtree that contains the configuration under this adapters simpleClassName.
     *                      Example: If the classname of the adapter is com.mycompany.MySimAdapter, the configuration
     *                      section that is passed here is MySimAdapter
     */
    public NusimSimHTTPAdapter(Config adapterConfig) throws Exception {
        super(adapterConfig);
        HttpSenderConfig httpSenderConfig = ConfigBeanFactory.create(adapterConfig, HttpSenderConfig.class);
        sender = new HttpSender(httpSenderConfig, "nuSIM HTTP connector");
        sender.start();
    }

    @Override
    public String getEID() throws NoEidAvailableException {
        try {
            return sender.send("GetSimData", new GetSimDataRequest("eid"), GetSimDataResponse.class).getEid();
        } catch (Exception e) {
            throw new NoEidAvailableException(e);
        }
    }

    @Override
    public String getCertNusim() {
        try {
            return sender.send("GetSimData", new GetSimDataRequest("cert"), GetSimDataResponse.class).getCert();
        } catch (Exception e) {
            throw new RuntimeException("Could not get NUSIM Certificate: " + e);
        }
    }

    @Override
    public String getNusimCapabilities() {
        try {
            return sender.send("GetSimData", new GetSimDataRequest("capabilities"), GetSimDataResponse.class).getCapabilities();
        } catch (Exception e) {
            throw new RuntimeException("Could not get NUSIM Capabilities: " + e);
        }
    }

    @Override
    public String loadProfile(String eid, String mac, String encP, String eKPubDP, @Nullable String sigEKPubDP, @Nullable String kPubDP, @Nullable String sigKPubDP, @Nullable String kPubCI) {
        if (kPubCI != null && !kPubCI.isEmpty()) {
            logger.debug("kPubCI is configured to {}", kPubCI);
        }
        try {
            LoadProfileRequest request = new LoadProfileRequest(eid, mac, encP, eKPubDP, sigEKPubDP, kPubDP, sigKPubDP, kPubCI);
            return sender.send("LoadProfile", request, LoadProfileResponse.class).getIccid();
        } catch (Exception e) {
            throw new RuntimeException("Could not load profile: " + e);
        }
    }

    public static class GetSimDataRequest {
        private String type;

        public GetSimDataRequest() {
        }

        public GetSimDataRequest(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class GetSimDataResponse {
        private String eid;
        private String cert;
        private String capabilities;
        private String iccid;

        public String getEid() {
            return eid;
        }

        public void setEid(String eid) {
            this.eid = eid;
        }

        public String getCapabilities() {
            return capabilities;
        }

        public void setCapabilities(String capabilities) {
            this.capabilities = capabilities;
        }

        public String getIccid() {
            return iccid;
        }

        public void setIccid(String iccid) {
            this.iccid = iccid;
        }

        public String getCert() {
            return cert;
        }

        public void setCert(String cert) {
            this.cert = cert;
        }
    }

    public static class LoadProfileRequest {
        private String eid;
        private String encP;
        private String mac;
        @JsonProperty("eKpubDP")
        private String eKpubDP;
        @JsonProperty("SigEKpub")
        private String sigEKpub;
        @JsonProperty("KpubDP")
        private String kPubDP;
        @JsonProperty("SigKpubDP")
        private String sigKpubDP;
        @JsonProperty("KpubCI")
        private String kpubCI;


        public LoadProfileRequest() {
        }

        public LoadProfileRequest(String eid, String mac, String encP, String eKPubDP, @Nullable String sigEKPubDP, @Nullable String kPubDP, @Nullable String sigKPubDP, @Nullable String kPubCI) {
            this.eid = eid;
            this.encP = encP;
            this.mac = mac;
            this.eKpubDP = eKPubDP;
            this.sigEKpub = sigEKPubDP;
            this.kPubDP = kPubDP;
            this.sigKpubDP = sigKPubDP;
            this.kpubCI = kPubCI;
        }

        public String getEid() {
            return eid;
        }

        public void setEid(String eid) {
            this.eid = eid;
        }

        public String getEncP() {
            return encP;
        }

        public void setEncP(String encP) {
            this.encP = encP;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String geteKpubDP() {
            return eKpubDP;
        }

        public void seteKpubDP(String eKpubDP) {
            this.eKpubDP = eKpubDP;
        }

        public String getSigEKpub() {
            return sigEKpub;
        }

        public void setSigEKpub(String sigEKpub) {
            this.sigEKpub = sigEKpub;
        }

        public String getKPubDP() {
            return kPubDP;
        }

        public void setKPubDP(String KPubDP) {
            this.kPubDP = KPubDP;
        }

        public String getSigKpubDP() {
            return sigKpubDP;
        }

        public void setSigKpubDP(String sigKpubDP) {
            this.sigKpubDP = sigKpubDP;
        }

        public String getKpubCI() {
            return kpubCI;
        }

        public void setKpubCI(String kpubCI) {
            this.kpubCI = kpubCI;
        }
    }

    public static class LoadProfileResponse {
        private String iccid;

        public LoadProfileResponse(String iccid) {
            this.iccid = iccid;
        }

        public LoadProfileResponse() {
        }

        public String getIccid() {
            return iccid;
        }

        public void setIccid(String iccid) {
            this.iccid = iccid;
        }
    }
}
