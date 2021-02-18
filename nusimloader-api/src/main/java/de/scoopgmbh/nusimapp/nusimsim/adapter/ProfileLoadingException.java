package de.scoopgmbh.nusimapp.nusimsim.adapter;

public class ProfileLoadingException extends Exception {
    private static final long serialVersionUID = -8615641395576988007L;

    public ProfileLoadingException() {
    }

    public ProfileLoadingException(String message) {
        super(message);
    }

    public ProfileLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileLoadingException(Throwable cause) {
        super(cause);
    }

    public ProfileLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
