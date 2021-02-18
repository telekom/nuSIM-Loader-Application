package de.scoopgmbh.nusimapp.if3;

public class IF3Exception extends Exception {
    private static final long serialVersionUID = 238643163238328831L;

    public IF3Exception() {
    }

    public IF3Exception(String message) {
        super(message);
    }

    public IF3Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public IF3Exception(Throwable cause) {
        super(cause);
    }

    public IF3Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
