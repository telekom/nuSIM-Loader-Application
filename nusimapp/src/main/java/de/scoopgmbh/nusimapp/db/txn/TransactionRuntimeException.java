package de.scoopgmbh.nusimapp.db.txn;

public class TransactionRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 165038822415954183L;

    public TransactionRuntimeException() {
    }

    public TransactionRuntimeException(String message) {
        super(message);
    }

    public TransactionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionRuntimeException(Throwable cause) {
        super(cause);
    }

}
