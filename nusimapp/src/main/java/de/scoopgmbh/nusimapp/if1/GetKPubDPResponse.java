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

public class GetKPubDPResponse {

    private ResultPart result;
    private String status;
    private ErrorPart error;

    public GetKPubDPResponse() {
        result = new ResultPart();
    }

    public GetKPubDPResponse(@JsonProperty("result") ResultPart result) {
        this.result = result;
    }

    public GetKPubDPResponse(String kPubDP, String sigKPubDP, String kPubCI) {
        result = new ResultPart();
        result.setkPubCI(kPubCI);
        result.setkPubDP(kPubDP);
        result.setSigKpubDP(sigKPubDP);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResultPart getResult() {
        return result;
    }

    public void setResult(ResultPart result) {
        this.result = result;
    }

    public ErrorPart getError() {
        return error;
    }

    public void setError(ErrorPart error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "GetKPubDPResponse{" +
                "result=" + result +
                ", status='" + status + '\'' +
                ", error=" + error +
                '}';
    }

    public static class ErrorPart {
        private String errorCode;
        private String errorMessage;

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public String toString() {
            return "ErrorPart{" +
                    "errorCode='" + errorCode + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    public static class ResultPart {
        private String reqVersion = "1.0";

        @JsonProperty("KpubDP")
        private String kPubDP;

        @JsonProperty("SigKpubDP")
        private String sigKpubDP;

        @JsonProperty("KpubCI")
        private String kPubCI;

        public ResultPart() {

        }

        public String getReqVersion() {
            return reqVersion;
        }

        public void setReqVersion(String reqVersion) {
            this.reqVersion = reqVersion;
        }

        public String getkPubDP() {
            return kPubDP;
        }

        public void setkPubDP(String kPubDP) {
            this.kPubDP = kPubDP;
        }

        public String getSigKpubDP() {
            return sigKpubDP;
        }

        public void setSigKpubDP(String sigKpubDP) {
            this.sigKpubDP = sigKpubDP;
        }

        public String getkPubCI() {
            return kPubCI;
        }

        public void setkPubCI(String kPubCI) {
            this.kPubCI = kPubCI;
        }

        @Override
        public String toString() {
            return "ResultPart{" +
                    "reqVersion='" + reqVersion + '\'' +
                    ", kPubDP='" + kPubDP + '\'' +
                    ", sigKpubDP='" + sigKpubDP + '\'' +
                    ", kPubCI='" + kPubCI + '\'' +
                    '}';
        }
    }
}
