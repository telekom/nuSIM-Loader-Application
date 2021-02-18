package de.scoopgmbh.nusimapp.db.txn;

import com.google.common.base.Suppliers;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class JOOQTransactionManager implements ITransactionManager {

    private final DataSource dataSource;
    private final SQLDialect sqlDialect;
    private final Settings settings;
    private final Supplier<DSLContext> defaultDSLContextSupplier = Suppliers.memoize(this::createDefaultDSLContext);
    private final Supplier<DSLContext> readonlyDSLContextSupplier = Suppliers.memoize(this::createReadOnlyDSLContext);

    public JOOQTransactionManager(DataSource dataSource, SQLDialect sqlDialect, Settings settings) {
        this.dataSource = dataSource;
        this.sqlDialect = sqlDialect;
        this.settings = settings;
    }

    public void run(final ITransaction transaction) {
        run((ctx) -> {
            transaction.doInTransaction(ctx);
            return null;
        }, false);
    }

    public void runReadOnly(final ITransaction transaction) {
        run((ctx) -> {
            transaction.doInTransaction(ctx);
            return null;
        }, true);
    }

    public <R> R eval(final ITransactionEval<R> transaction) {
        return run(transaction, false);
    }

    public <R> R evalReadOnly(final ITransactionEval<R> transaction) {
        return run(transaction, true);
    }

    private <R> R run(final ITransactionEval<R> callback, final boolean readonly) {
        DSLContext dslContext = (readonly ? readonlyDSLContextSupplier : defaultDSLContextSupplier).get();
        R result;
        try {
            result = dslContext.transactionResult(configuration -> {
                try (DSLContext innerCtx = DSL.using(configuration)) {
                    R innerResult = callback.doInTransaction(innerCtx);
                    if (readonly) {
                        // perform rollback. Alas, with JOOQ transaction API, the only way to do it
                        // is by throwing an exception
                        throw new RollbackException(innerResult);
                    }
                    return innerResult;
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    throw new TransactionRuntimeException(ex);
                }
            });
        } catch (RollbackException ex) {
            result = ex.result();
        }
        return result;
    }

    private synchronized DSLContext createDefaultDSLContext() {
        return DSL.using(new DataSourceConnectionProvider(dataSource, false), sqlDialect, settings);
    }

    private synchronized DSLContext createReadOnlyDSLContext() {
        return DSL.using(new DataSourceConnectionProvider(dataSource, true), sqlDialect, settings);
    }

    private static class DataSourceConnectionProvider extends org.jooq.impl.DataSourceConnectionProvider {
        private final boolean readOnly;

        public DataSourceConnectionProvider(DataSource dataSource, boolean readOnly) {
            super(dataSource);
            this.readOnly = readOnly;
        }

        @Override
        public Connection acquire() {
            try {
                Connection connection = dataSource().getConnection();
                connection.setReadOnly(readOnly);
                return connection;
            } catch (SQLException e) {
                throw new DataAccessException("Error getting connection from data source " + dataSource(), e);
            }
        }
    }

    private static class RollbackException extends RuntimeException {
        private static final long serialVersionUID = -1512264145484491431L;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Object result;

        public RollbackException(Object result) {
            this.result = result;
        }

        @SuppressWarnings("unchecked")
        public <R> R result() {
            return (R) result;
        }
    }
}
