package de.scoopgmbh.nusimapp.db.txn;

import org.jooq.DSLContext;

import java.sql.Connection;

/**
 * A closure for code that should run in a database transaction and return a
 * computed value. Use this with the {@link ITransactionManager#eval(ITransactionEval)
 * eval} method of {@link ITransactionManager}.
 */
@FunctionalInterface
public interface ITransactionEval<R> {

    /**
     * Run code in a JOOQ transaction, using a {@link DSLContext}, and return a
     * value computed inside the transaction.
     *
     * @param ctx the JOOQ context for this transaction.
     * @return the computed value
     * @throws Exception implementors may throw any Exception. It will be
     *                   reported to the caller as a {@link TransactionRuntimeException}. The
     *                   connection will be {@link Connection#rollback() rolled back} on
     *                   <i>any</i> exception.
     */
    R doInTransaction(DSLContext ctx) throws Exception;

}
