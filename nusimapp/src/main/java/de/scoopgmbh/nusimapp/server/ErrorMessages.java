package de.scoopgmbh.nusimapp.server;

public enum ErrorMessages {
    DEFAULT(500, "An error occurred. Please contact you administrator and check the logs"),
    CHANGE_PASSWORD(500, "Passwort konnte nicht aktualisiert werden"),
    DELETE_USER(500, "Beim Löschen ist ein Fehler aufgetreten");


    private final int httpStatus;
    private final String message;

    ErrorMessages(int httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
