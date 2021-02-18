package de.scoopgmbh.nusimapp.db;

import com.opentable.db.postgres.embedded.DatabasePreparer;
import com.opentable.db.postgres.embedded.PreparedDbProvider;
import org.junit.rules.ExternalResource;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

/**
 * JUnit rule to use an embedded PostgreSQL database.
 * <p>
 * Usage:
 * <pre>
 * &#64;ClassRule
 * public static EmbeddedDBRule db = new EmbeddedDBRule();
 *
 * &#64;Test
 * public void mytest() {
 *   DataSource ds = new DataSource(db.getJdbcUrl(), ...);
 *   ...
 * }
 * </pre>
 */
public class EmbeddedDBRule extends ExternalResource {

    static {
        // init java.util.logging to SLF4J bridge
        // see https://www.slf4j.org/api/org/slf4j/bridge/SLF4JBridgeHandler.html
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    // PostgreSQL database preparer. For having a clean database, it is easiest to just drop the public schema
    // and recreate it accordingly (create new and reset access rights).
    // It's easier then dropping and recreating the database itself, as many jdbc connections are already linked
    // to a database and database drop/create requires users with more access rights than users working within their
    // database (and dropping schema in there).
    private final static DatabasePreparer dbPreparer = ds -> {
        Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();
        // Delete all data and resetup, thus just delete and recreate
        // public schema and rerun our update routine.
        stmt.executeUpdate("DROP SCHEMA IF EXISTS public CASCADE");
        stmt.executeUpdate("CREATE SCHEMA public");
        stmt.executeUpdate("GRANT ALL ON SCHEMA public TO postgres");
        stmt.executeUpdate("GRANT ALL ON SCHEMA public TO public");
        stmt.close();
        conn.close();
    };

    private volatile PreparedDbProvider provider;
    private volatile String jdbcUrl;
    private volatile String username;

    @Override
    protected void before() throws Throwable {
        provider = PreparedDbProvider.forPreparer(dbPreparer);
        // start embedded db and obtain jdbc url and username
        Map<String, String> infos = provider.getConfigurationTweak("test");
        jdbcUrl = infos.get("ot.db.test.uri");
        username = infos.get("ot.db.test.ds.user");
    }

    @Override
    protected void after() {
        provider = null;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUsername() {
        return username;
    }

    public String getJdbcPassword() {
        return "";
    }

}
