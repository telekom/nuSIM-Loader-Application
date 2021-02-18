package de.scoopgmbh.nusimapp.db.txn;

public interface ITransactionManager {

    /**
     * Run a transaction closure in a new database transaction. If the closure
     * ran without exceptions, the transaction will be commited at the end.
     * <p/>
     * This method cannot return a value. If you want this use {@link #eval}
     * instead.
     * <p>
     * Note that the closure may throw <i>any</i> exception; in case of a
     * checked exception it will be wrapped into a
     * {@link TransactionRuntimeException}. This is a runtime exception by
     * design, so that you have the option to care about it or not.
     *
     * @param transaction the transaction closure to run
     * @throws TransactionRuntimeException thrown if a checked exception occured
     *                                     while executing the transaction or creating the transaction. Any database
     *                                     changes are rolled back on <i>any</i> error.
     * @throws RuntimeException            thrown if the transaction closure throws a
     *                                     runtime exception. The original runtime exception will be thrown as-is
     *                                     without further modification. Any database changes are rolled back.
     */
    void run(ITransaction transaction);

    /**
     * Run a transaction closure in a read-only transaction. Any database
     * changes are <strong>rolled back</strong> after the transaction closure
     * has run.
     * <p/>
     * The default implementation will also set the read-only flag on the
     * connection which is used for the transaction, using {@link
     * java.sql.Connection#setReadOnly(boolean)}. Note that depending on the
     * database this has various effects:
     * <ul>
     * <li>for H2 this flag is simply ignored</li>
     * <li>for PostgreSQL the transaction is set into read-only mode, using <a
     * href="https://www.postgresql.org/docs/current/static/sql-set-transaction.html">SET
     * TRANSACTION READ ONLY</a>. This actually <strong>forbids</strong> any
     * data manipulation; any INSERT/UPDATE/DELETE will throw an
     * SQLException!</li>
     * <li>for other databases the status is unknown. As the JDBC spec states,
     * the read-only flag is "merely a suggestion which can be used by
     * optimizers".</li>
     * </ul>
     * <p/>
     * This method cannot return a value. If you want this use {@link
     * #evalReadOnly} instead.
     * <p/>
     * Note that the closure may throw <i>any</i> exception; in case of a
     * checked exception it will be wrapped into a
     * {@link TransactionRuntimeException}. This is a runtime exception by
     * design, so that you have the option to care about it or not.
     *
     * @param transaction the transaction closure to run
     * @throws TransactionRuntimeException thrown if a checked exception occured
     *                                     while executing the transaction or creating the transaction. Any database
     *                                     changes are rolled back on <i>any</i> error.
     * @throws RuntimeException            thrown if the transaction closure throws a
     *                                     runtime exception. The original runtime exception will be thrown as-is
     *                                     without further modification. Any database changes are rolled back.
     */
    void runReadOnly(ITransaction transaction);

    /**
     * Run a transaction closure in a new database transaction. If the closure
     * ran without exceptions, the transaction will be commited at the end.
     * <p/>
     * The closure must return a value. If you don't want to return a value, use
     * {@link #run} instead.
     * <p>
     * Note that the closure may throw <i>any</i> exception; in case of a
     * checked exception it will be wrapped into a
     * {@link TransactionRuntimeException}. This is a runtime exception by
     * design, so that you have the option to care about it or not.
     *
     * @param transaction the transaction closure to run
     * @return the value returned from the closure
     * @throws TransactionRuntimeException thrown if a checked exception occured
     *                                     while executing the transaction or creating the transaction. Any database
     *                                     changes are rolled back on <i>any</i> error.
     * @throws RuntimeException            thrown if the transaction closure throws a
     *                                     runtime exception. The original runtime exception will be thrown as-is
     *                                     without further modification. Any database changes are rolled back.
     */
    <R> R eval(ITransactionEval<R> transaction);

    /**
     * Run a transaction closure in a read-only transaction. Any database
     * changes are <strong>rolled back</strong> after the transaction closure
     * has run.
     * <p/>
     * The default implementation will also set the read-only flag on the
     * connection which is used for the transaction, using {@link
     * java.sql.Connection#setReadOnly(boolean)}. Note that depending on the
     * database this has various effects: <ul> <li>for H2 this flag is simply
     * ignored</li> <li>for PostgreSQL the transaction is set into read-only
     * mode, using <a href="https://www.postgresql.org/docs/current/static/sql-set-transaction.html">SET
     * TRANSACTION READ ONLY</a>. This actually <strong>forbids</strong> any
     * data manipulation; any INSERT/UPDATE/DELETE will throw an
     * SQLException!</li> <li>for other databases the status is unknown. As the
     * JDBC spec states, the read-only flag is "merely a suggestion which can
     * be
     * used by optimizers".</li> </ul>
     * <p/>
     * The closure must return a value. If you don't want to return a value,
     * use
     * {@link #runReadOnly} instead.
     * <p/>
     * Note that the closure may throw <i>any</i> exception; in case of a
     * checked exception it will be wrapped into a
     * {@link TransactionRuntimeException}. This is a runtime exception by
     * design, so that you have the option to care about it or not.
     *
     * @param transaction the transaction closure to run
     * @return the value returned from the closure
     * @throws TransactionRuntimeException thrown if a checked exception occured
     *                                     while executing the transaction or creating the transaction. Any database
     *                                     changes are rolled back on <i>any</i> error.
     * @throws RuntimeException            thrown if the transaction closure throws a
     *                                     runtime exception. The original runtime exception will be thrown as-is
     *                                     without further modification. Any database changes are rolled back.
     */
    <R> R evalReadOnly(ITransactionEval<R> transaction);

}
