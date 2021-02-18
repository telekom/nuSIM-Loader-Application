package de.scoopgmbh.nusimapp.if1;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestBulkEncProfilesResponse {
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
        @JsonProperty("referenceID")
        private String referenceId;
        private String waitTime;

        public String getRemainingTTL() {
            return remainingTTL;
        }

        private String remainingTTL;

        public String getReqVersion() {
            return reqVersion;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public String getWaitTime() {
            return waitTime;
        }
    }

    public static class ErrorPart {
        private String errorCode;
        private String errorMessage;

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
