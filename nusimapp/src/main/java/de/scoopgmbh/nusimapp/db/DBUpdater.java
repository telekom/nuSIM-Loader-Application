package de.scoopgmbh.nusimapp.db;

import de.scoopgmbh.nusimapp.lifecycle.Managed;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class DBUpdater implements Managed {
    public static final String NAME = "DBUpdater";
    private final DAO dao;

    public DBUpdater(DAO dao) {
        this.dao = dao;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        java.sql.Connection connection = dao.getConnection();

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase("de/scoopgmbh/nusimapp/db/database.xml", new ClassLoaderResourceAccessor(), database);

        liquibase.update(new Contexts());
    }

    @Override
    public void stop() throws Exception {

    }
}
