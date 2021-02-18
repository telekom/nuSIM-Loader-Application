package de.scoopgmbh.nusimapp.if1;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class GetEncProfileResponse {

    private ResultPart result;
    private ErrorPart error;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class ResultPart {

        private String reqVersion;
        private String eid;
        private String encP;
        private String mac;
        private String eKpubDP;
        @JsonProperty("SigEKpubDP")
        private String sigEKpubDP;
        /** old deprecated attribute replaced by SigEKpubDP, still there for backwards compatibility with old DP */
        @JsonProperty("SigEKpub")
        private String sigEKpub;

        public String getReqVersion() {
            return reqVersion;
        }

        public void setReqVersion(String reqVersion) {
            this.reqVersion = reqVersion;
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

        public String getSigEKpubDP() {
            return Optional.ofNullable(sigEKpubDP).orElse(sigEKpub);
        }

        public void setSigEKpubDP(String sigEKpubDP) {
            this.sigEKpubDP = sigEKpubDP;
            this.sigEKpub = sigEKpubDP;
        }

        public String getSigEKpub() {
            return Optional.ofNullable(sigEKpubDP).orElse(sigEKpub);
        }

        public void setSigEKpub(String sigEKpub) {
            this.sigEKpubDP = sigEKpub;
            this.sigEKpub = sigEKpub;
        }
    }

    public static class ErrorPart {
        private String errorCode;
        private String errorMessage;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
