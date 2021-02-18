package de.scoopgmbh.nusimapp.db.txn;

import org.jooq.DSLContext;

import java.sql.Connection;

/**
 * A simple closure for code that should run in a database transaction.
 * <p>
 * Note: this transaction cannot return a value to the caller. If you want this,
 * use {@link ITransactionManager#eval} with a closure of type {@link
 * ITransactionEval} instead.
 */
@FunctionalInterface
public interface ITransaction {

    /**
     * Run code in a JOOQ transaction, using a {@link DSLContext}.
     *
     * @param ctx the JOOQ context for this transaction.
     * @throws Exception implementors may throw any Exception. It will be
     *                   reported to the caller as a {@link TransactionRuntimeException}. The
     *                   connection will be {@link Connection#rollback() rolled back} on
     *                   <i>any</i> exception.
     */
    void doInTransaction(DSLContext ctx) throws Exception;

}

