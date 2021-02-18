package de.scoopgmbh.nusimapp.nusimsim.adapter;

public class NoEidAvailableException extends Exception {
    private static final long serialVersionUID = -8894121522092654195L;

    public NoEidAvailableException() {
    }

    public NoEidAvailableException(String message) {
        super(message);
    }

    public NoEidAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEidAvailableException(Throwable cause) {
        super(cause);
    }

    public NoEidAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
