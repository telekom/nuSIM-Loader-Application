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
