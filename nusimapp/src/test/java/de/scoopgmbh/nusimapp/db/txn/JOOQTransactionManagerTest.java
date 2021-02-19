/*
 * nusim-loader
 *
 * (c) 2020 Deutsche Telekom AG.
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package de.scoopgmbh.nusimapp.db.txn;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.scoopgmbh.nusimapp.db.EmbeddedDBRule;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.jooq.exception.SQLStateSubclass.C25006_READ_ONLY_SQL_TRANSACTION;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JOOQTransactionManagerTest {

    @ClassRule
    public static EmbeddedDBRule db = new EmbeddedDBRule();

    private HikariDataSource dataSource;
    private JOOQTransactionManager ta;


    @Before
    public void setup() {
        HikariConfig hikariConfig = new HikariConfig("/de/scoopgmbh/nusimapp/db/hikari.properties");
        hikariConfig.setJdbcUrl(db.getJdbcUrl());
        hikariConfig.setUsername(db.getJdbcUsername());
        hikariConfig.setPassword(db.getJdbcPassword());
        hikariConfig.setAutoCommit(false); // disable autocommit, because JOOQTransactionManager will care about committing
        dataSource = new HikariDataSource(hikariConfig);
        ta = new JOOQTransactionManager(dataSource, SQLDialect.POSTGRES, new Settings());
        ta.run(ctx -> ctx.execute("CREATE TABLE config (name VARCHAR(50) NOT NULL, value VARCHAR(50))"));
    }


    @After
    public void teardown() {
        if (ta != null) ta.run(ctx -> ctx.execute("DROP TABLE config"));
        if (dataSource != null) dataSource.close();
    }


    @Test
    public void testSimpleTransaction() {
        ta.run(ctx -> {
            insert("name1", "val1", ctx);
            insert("name2", "val2", ctx);

            String val1 = selectValueWhereNameIs("name1", ctx);
            assertThat(val1, equalTo("val1"));

            String val2 = selectValueWhereNameIs("name2", ctx);
            assertThat(val2, equalTo("val2"));
        });

        String val1 = ta.evalReadOnly(ctx -> selectValueWhereNameIs("name1", ctx));
        assertThat(val1, equalTo("val1"));

        String val2 = ta.evalReadOnly(ctx -> selectValueWhereNameIs("name2", ctx));
        assertThat(val2, equalTo("val2"));
    }


    @Test
    public void testRollback() {
        try {
            ta.run(ctx -> {
                insert("name1", "val1", ctx);
                throw new MyCustomRuntimeException();
            });
            fail("we shouldn't come here because an exception is thrown from the transaction lambda");
        } catch (MyCustomRuntimeException expected) {
            // expected
        }

        // record must not exist
        int count = ta.evalReadOnly(ctx -> selectCountWhereNameIs("name1", ctx));
        assertThat(count, equalTo(0));
    }


    /**
     * Test that one transaction cannot see the data inserted in another transaction.
     * <p>
     * A second thread inserts data, and the outer jUnit thread waits until the INSERT
     * happened, but the COMMIT; the outer thread should not see the inserted data.
     */
    @Test
    public void testTransactionIsolation() throws InterruptedException {
        final Semaphore insertSemaphore = new Semaphore(1);
        insertSemaphore.acquire();
        final Semaphore selectSemaphore = new Semaphore(1);
        selectSemaphore.acquire();

        Thread thread = new Thread(() -> ta.run(ctx -> {
            // insert a row
            insert("name1", "val1", ctx);
            insertSemaphore.release();
            // block until we release the semaphore for the outer select, so that the transaction is not committed
            boolean noTimeout = selectSemaphore.tryAcquire(5, TimeUnit.SECONDS);
            assertThat("timeout waiting for select", noTimeout, equalTo(true));
            // if we come here the semaphore has been released
            // we continue normally, letting the transaction commit itself
        }));
        thread.start();

        // wait for insert to happen (ie. insertSemaphore is released)
        boolean noTimeout = insertSemaphore.tryAcquire(5, TimeUnit.SECONDS);
        assertThat("timeout waiting for insert", noTimeout, equalTo(true));

        // as long as the above transaction is not committed, the record must not exist
        int count = ta.evalReadOnly(ctx -> selectCountWhereNameIs("name1", ctx));
        assertThat(count, equalTo(0));

        // release the semaphore for the select, this will continue the execution of the thread (and the transaction, which will be committed)
        selectSemaphore.release();

        // wait as most as 5 seconds for the thread to die
        thread.join(5000);
        assertThat(thread.isAlive(), equalTo(false));

        // the row should now be there
        String value = ta.evalReadOnly(ctx -> selectValueWhereNameIs("name1", ctx));
        assertThat(value, equalTo("val1"));
    }


    @Test
    public void testNestedTransaction() {
        ta.run(ctx -> {
            insert("name1", "val1", ctx);
            try {
                ta.run(nestedCtx -> {
                    insert("name2", "val2", nestedCtx);
                    throw new MyCustomRuntimeException();
                });
                fail("we shouldn't come here because an exception is thrown from the nested transaction lambda");
            } catch (MyCustomRuntimeException ignore) {
                // ignore the exception of the nested transaction
            }
        });

        // row from the outer transaction should be there
        String value = ta.evalReadOnly(ctx -> selectValueWhereNameIs("name1", ctx));
        assertThat(value, equalTo("val1"));

        // row from the inner transaction should not be there
        int count = ta.evalReadOnly(ctx -> selectCountWhereNameIs("name2", ctx));
        assertThat(count, equalTo(0));
    }


    @Test
    public void testNestedTransactionWithJooqAPI() {
        ta.run(ctx -> {
            insert("name1", "val1", ctx);
            try {
                ctx.transaction(nestedConfig -> {
                    DSLContext nestedCtx = DSL.using(nestedConfig);
                    insert("name2", "val2", nestedCtx);
                    throw new MyCustomRuntimeException();
                });
                fail("we shouldn't come here because an exception is thrown from the nested transaction lambda");
            } catch (MyCustomRuntimeException ignore) {
                // ignore the exception of the nested transaction
            }
        });

        // row from the outer transaction should be there
        String value = ta.evalReadOnly(ctx -> selectValueWhereNameIs("name1", ctx));
        assertThat(value, equalTo("val1"));

        // row from the inner transaction should not be there
        int count = ta.evalReadOnly(ctx -> selectCountWhereNameIs("name2", ctx));
        assertThat(count, equalTo(0));
    }


    @Test
    public void testReadOnlyTransaction() {
        // Try to insert a row inside a read-only transaction. This should fail.

        // Note: PostgreSQL throws an SQLException if someone tries to INSERT
        // inside a read-only transaction. H2 does _not_ throw an exception,
        // instead it simply does not insert the record and silently completes.
        // So the behavior is database-specific. Since we test with an embedded
        // PostgreSQL database here, we explictly check for the PostgreSQL
        // exception text.

        try {
            ta.runReadOnly(ctx -> {
                insert("name1", "val1", ctx);
                fail("Expected an exception");
            });
        } catch (DataAccessException ex) {
            // check for SQL standard error code (PostgreSQL sends that)
            assertThat(ex.sqlStateClass(), equalTo(C25006_READ_ONLY_SQL_TRANSACTION.sqlStateClass()));
            assertThat(ex.sqlStateSubclass(), equalTo(C25006_READ_ONLY_SQL_TRANSACTION));
        }
    }


    @Test
    public void testReadOnlyWithNewInnerWritableTransaction() {
        // run a writable transaction within a read-only transaction
        ta.runReadOnly(ctx -> {
            ta.run(nestedCtx -> {
                // the insert should be allowed because we are in a writable transaction
                insert("nameInner", "val", nestedCtx);
                assertThat(selectCountWhereNameIs("nameInner", nestedCtx), equalTo(1));
            });
            // the inner writable TA is completed and committed when we come here,
            // so check if the data is there
            assertThat(selectCountWhereNameIs("nameInner", ctx), equalTo(1));
            // check that we cannot insert anything into this read-only transaction
            try {
                insert("nameOuter", "val", ctx);
                fail("Expected an exception");
            } catch (DataAccessException ex) {
                // check for SQL standard error code (PostgreSQL sends that)
                assertThat(ex.sqlStateClass(), equalTo(C25006_READ_ONLY_SQL_TRANSACTION.sqlStateClass()));
                assertThat(ex.sqlStateSubclass(), equalTo(C25006_READ_ONLY_SQL_TRANSACTION));
            }
        });

        // after the TA only the row from the inner writable TA should be there
        assertThat(ta.evalReadOnly(ctx -> selectCountWhereNameIs("nameOuter", ctx)), equalTo(0));
        assertThat(ta.evalReadOnly(ctx -> selectCountWhereNameIs("nameInner", ctx)), equalTo(1));
    }


    private static void insert(String name, String value, DSLContext ctx) {
        ctx.execute("INSERT INTO config (name, value) VALUES (?,?)", name, value);
    }


    private static String selectValueWhereNameIs(String name, DSLContext ctx) {
        return ctx.fetchOne("SELECT value FROM config WHERE name = ?", name).get(0, String.class);
    }


    private static int selectCountWhereNameIs(String name, DSLContext ctx) {
        return ctx.fetchOne("SELECT count(*) FROM config WHERE name = ?", name).get(0, Integer.class);
    }


    private static class MyCustomRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 2279888026706010506L;
    }
}
