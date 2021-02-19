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

package de.scoopgmbh.nusimapp.if1;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class RetrieveBulkEncProfilesResponse {
    private RetrieveBulkEncProfilesResponse.ResultPart result;
    private RetrieveBulkEncProfilesResponse.ErrorPart error;
    private String status;

    public RetrieveBulkEncProfilesResponse.ResultPart getResult() {
        return result;
    }

    public void setResult(RetrieveBulkEncProfilesResponse.ResultPart result) {
        this.result = result;
    }

    public RetrieveBulkEncProfilesResponse.ErrorPart getError() {
        return error;
    }

    public void setError(RetrieveBulkEncProfilesResponse.ErrorPart error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Profile {
        private String eid;
        private String encP;
        private String mac;
        private String eKpubDP;
        @JsonProperty("SigEKpubDP")
        private String sigEKpubDP;
        /** old deprecated attribute replaced by SigEKpubDP, still there for backwards compatibility with old DP */
        @JsonProperty("SigEKpub")
        private String sigEKpub;
        private String errorCode;
        private String errorMessage;

        public Profile() {
        }

        public String getEid() {
            return eid;
        }

        public String getEncP() {
            return encP;
        }

        public String getMac() {
            return mac;
        }

        public String geteKpubDP() {
            return eKpubDP;
        }

        public String getSigEKpubDP() {
            return Optional.ofNullable(sigEKpubDP).orElse(sigEKpub);
        }

        public String getSigEKpub() {
            return Optional.ofNullable(sigEKpubDP).orElse(sigEKpub);
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class ResultPart {

        private String reqVersion;
        private List<Profile> profileList;
        private String remainingTTL;
        @JsonProperty("Num")
        private String num;

        public String getRemainingTTL() {
            return remainingTTL;
        }

        public String getNum() {
            return num;
        }

        public String getChunkSize() {
            return chunkSize;
        }

        private String chunkSize;

        public String getReqVersion() {
            return reqVersion;
        }

        public List<Profile> getProfileList() {
            return profileList;
        }
    }

    public static class ErrorPart {
        private String errorCode;
        private String errorMessage;
        private String waitTime;

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getWaitTime() {
            return waitTime;
        }
    }
}
