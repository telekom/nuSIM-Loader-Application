package de.scoopgmbh.nusimapp.if1;

import java.util.List;

public class QueryProfileStockResponse {
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

    public static class Stock {
        private String refInfo1;
        private String refInfo2;
        private String refInfo3;
        private String available;

        public Stock() {

        }

        public String getRefInfo1() {
            return refInfo1;
        }

        public String getRefInfo2() {
            return refInfo2;
        }

        public String getRefInfo3() {
            return refInfo3;
        }

        public String getAvailable() {
            return available;
        }
    }

    public static class ResultPart {

        private String reqVersion;
        private List<Stock> stockList;

        public String getReqVersion() {
            return reqVersion;
        }

        public List<Stock> getStockList() {
            return stockList;
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
