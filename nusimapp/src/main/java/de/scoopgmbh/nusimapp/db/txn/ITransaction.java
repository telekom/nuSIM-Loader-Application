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

