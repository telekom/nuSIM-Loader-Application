package de.scoopgmbh.nusimapp.if1;

public class IF1Exception extends Exception {
    private static final long serialVersionUID = -982070338500363977L;

    private final String errorCode;

    public IF1Exception(String message) {
        super("Internal error while communicating with DP: " + message);
        this.errorCode = "ERR";
    }

    public IF1Exception(String message, Throwable cause) {
        super("Internal error while communicating with DP: " + message + ": " + cause.getMessage(), cause);
        this.errorCode = "ERR";
    }

    public IF1Exception(String errorCode, String message) {
        super("DP returned error code " + errorCode + ": " + message);
        this.errorCode = errorCode;
    }

    public IF1Exception(String errorCode, String message, Throwable cause) {
        super("DP returned error code " + errorCode + ": " + message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
