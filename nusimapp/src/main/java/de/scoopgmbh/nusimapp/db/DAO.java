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

import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.scoopgmbh.BatchCollector;
import de.scoopgmbh.nusimapp.db.jooq.tables.pojos.Profiles;
import de.scoopgmbh.nusimapp.db.jooq.tables.records.EidsRecord;
import de.scoopgmbh.nusimapp.db.jooq.tables.records.ProfilesRecord;
import de.scoopgmbh.nusimapp.db.jooq.tables.records.ReferenceIdsRecord;
import de.scoopgmbh.nusimapp.db.txn.ITransactionManager;
import de.scoopgmbh.nusimapp.db.txn.JOOQTransactionManager;
import de.scoopgmbh.nusimapp.if1.RequestBulkEncProfilesRequest;
import de.scoopgmbh.nusimapp.if1.RetrieveBulkEncProfilesResponse;
import de.scoopgmbh.nusimapp.lifecycle.Managed;
import de.scoopgmbh.nusimapp.server.api.provisioning.LoadCertificatesHandler.LoadCertificatesResponse;
import de.scoopgmbh.nusimapp.server.api.provisioning.ReferenceIdRecord;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jooq.*;
import org.jooq.conf.RenderKeywordStyle;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.scoopgmbh.nusimapp.db.jooq.Tables.*;

public class DAO implements Managed {
    private static final Logger logger = LoggerFactory.getLogger(DAO.class);

    public static final String NAME = "DAO";
    private final Config config;
    private HikariDataSource datasource;
    private ITransactionManager transactionManager;

    public DAO(final Config config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        this.datasource = createDataSource();
        this.transactionManager = createTransactionManager();
    }

    @Override
    public void stop() {
        if (datasource != null) {
            datasource.close();
        }
    }

    Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    private ITransactionManager createTransactionManager() {
        return new JOOQTransactionManager(datasource, config.getEnum(SQLDialect.class, "dialect"), new Settings()
                .withExecuteWithOptimisticLocking(true)
                .withExecuteWithOptimisticLockingExcludeUnversioned(true)
//                .withQueryTimeout(60000)
                .withExecuteLogging(config.getBoolean("executeLogging"))
                .withRenderCatalog(false)
                .withRenderSchema(false)
                .withRenderFormatted(true)
                .withRenderKeywordStyle(RenderKeywordStyle.UPPER)
        );
    }

    private HikariDataSource createDataSource() throws IOException {
        Properties hikariProps = new Properties();
        InputStream resourceAsStream = getClass().getResourceAsStream("hikari.properties");
        hikariProps.load(resourceAsStream);
        HikariConfig hikariConfig = new HikariConfig(hikariProps);
        hikariConfig.setJdbcUrl(config.getString("jdbcURL"));
        hikariConfig.setUsername(config.getString("username"));
        hikariConfig.setPassword(new String(Base64.getDecoder().decode(config.getString("password"))));
        // disable autocommit, because our JOOQTransactionManager will care about committing
        hikariConfig.setAutoCommit(false);
        return new HikariDataSource(hikariConfig);
    }

    public void createRecord(final String eid, String refInfo1, String refInfo2, String refInfo3) {
        transactionManager.run(ctx -> {
            final Timestamp now = new Timestamp(System.currentTimeMillis());
            ctx.deleteFrom(EIDS).where(EIDS.EID.lower().eq(eid.toLowerCase())).execute();
            ctx.insertInto(EIDS).set(new EidsRecord(eid, now, now, null, null, null, refInfo1, refInfo2, refInfo3)).execute();
        });
    }

    public int createBulkRecord(final Stream<String> eids) {
        AtomicInteger i = new AtomicInteger(0);
        transactionManager.run(ctx -> {
            eids.forEach((eid) -> {
                final Timestamp now = new Timestamp(System.currentTimeMillis());
                ctx.deleteFrom(PROFILES).where(PROFILES.EID.lower().eq(eid.toLowerCase())).execute();
                ctx.insertInto(PROFILES).set(new ProfilesRecord(eid, now, now, null, null, null, null, null, null, null, null)).execute();
                i.incrementAndGet();
            });
        });
        return i.intValue();
    }

    public void createBulkRecord(final String eid, final String nusimCert) {
        transactionManager.run(ctx -> {
            final Timestamp now = new Timestamp(System.currentTimeMillis());
            ctx.deleteFrom(PROFILES).where(PROFILES.EID.eq(eid)).execute();
            ctx.insertInto(PROFILES).set(new ProfilesRecord(eid, now, now, nusimCert, null, null, null, null, null, null, null)).execute();
        });
    }

    public void addNusimCertificate(final String eid, final String nusimCert) {
        transactionManager.run(ctx -> {
            final Timestamp now = new Timestamp(System.currentTimeMillis());
            ctx.update(EIDS).set(EIDS.NUSIMCERT, nusimCert).set(EIDS.UPDATE_TS, now).where(EIDS.EID.lower().eq(eid.toLowerCase())).execute();
        });
    }

    public void addError(String eid, String errorText) {
        transactionManager.run(ctx -> {
            final Timestamp now = new Timestamp(System.currentTimeMillis());
            ctx.update(EIDS).set(EIDS.ERRORTEXT, errorText).set(EIDS.UPDATE_TS, now).where(EIDS.EID.lower().eq(eid.toLowerCase())).execute();
        });
    }

    public Optional<String> getNusimCertificate(String eid) {
        return transactionManager.eval(ctx -> ctx.select(PROFILES.NUSIMCERT).from(PROFILES).where(PROFILES.EID.eq(eid)).and(PROFILES.NUSIMCERT.isNotNull()).fetchOptional(EIDS.NUSIMCERT));
    }

    public void addIccid(String eid, String iccid) {
        transactionManager.run(ctx -> {
            final Timestamp now = new Timestamp(System.currentTimeMillis());
            ctx.update(EIDS).set(EIDS.ICCID, iccid).set(EIDS.UPDATE_TS, now).where(EIDS.EID.lower().eq(eid.toLowerCase())).execute();
        });
    }

    public Profiles readProfile(String eid) {
        return transactionManager.eval(ctx -> {
            return ctx.selectFrom(PROFILES).where(PROFILES.EID.eq(eid).and(PROFILES.MAC.isNotNull())).fetchOneInto(Profiles.class);
        });
    }

    public void deleteProfile(String eid) {
        transactionManager.run(ctx -> {
            ctx.deleteFrom(PROFILES).where(PROFILES.EID.eq(eid)).execute();
        });
    }

    public LoadCertificatesResponse visitBulkEIDs(final String eidPrefix, final int maxRequestQuantity, final CheckedFunction<List<String>, Map<String, String>> certificateLoader) {
        return transactionManager.eval(ctx -> {
            final int total = ctx.selectCount().from(PROFILES).where(PROFILES.EID.lower().startsWith(eidPrefix.toLowerCase())).fetchOne(0, int.class);
            final AtomicInteger processed = new AtomicInteger();
            final AtomicInteger success = new AtomicInteger();
            final AtomicInteger error = new AtomicInteger();
            final Result<Record1<String>> records = ctx.select(PROFILES.EID).from(PROFILES).where(PROFILES.EID.lower().startsWith(eidPrefix.toLowerCase()).and(PROFILES.NUSIMCERT.isNull())).forUpdate().fetch();
            records.stream().map(Record1::value1).
                    collect(new BatchCollector<>(maxRequestQuantity, null, (eids, state) -> {
                        processed.addAndGet(eids.size());
                        try {
                            Map<String, String> certificateMap = certificateLoader.apply(eids);
                            error.addAndGet(eids.size() - certificateMap.size());
                            certificateMap.entrySet().forEach(e -> {
                                ctx.transaction(inner -> {
                                    inner.dsl().update(PROFILES)
                                            .set(PROFILES.NUSIMCERT, e.getValue())
                                            .set(PROFILES.UPDATE_TS, new Timestamp(System.currentTimeMillis()))
                                            .where(PROFILES.EID.eq(e.getKey())).execute();
                                });
                                success.incrementAndGet();
                            });
                        } catch (Exception e) {
                            logger.error("Could not retrieve certificates: {}", ExceptionUtils.getRootCause(e).getLocalizedMessage());
                            error.incrementAndGet();
                        }
                        return null;
                    }));
            return new LoadCertificatesResponse(total, processed.get(), success.get(), error.get());
        });
    }

    public List<RequestBulkEncProfilesRequest.Eid> getUnrequestedEIDs(DSLContext ctx, int requestCount) {
        final Result<Record2<String, String>> records = ctx.select(PROFILES.EID, PROFILES.NUSIMCERT).from(PROFILES).where(PROFILES.NUSIMCERT.isNotNull().and(PROFILES.REFERENCE_ID.isNull()).and(PROFILES.MAC.isNull())).orderBy(PROFILES.EID).forUpdate().fetch();
        return records.stream().limit(requestCount).map(r -> new RequestBulkEncProfilesRequest.Eid(r.value1(), r.value2())).collect(Collectors.toList());
    }

    public void addReferenceId(DSLContext ctx, List<RequestBulkEncProfilesRequest.Eid> unrequestedEIDs, String referenceId, ZonedDateTime retrieveTime, int count, String refInfo1, String refInfo2, String refInfo3) {
        ctx.update(PROFILES).set(PROFILES.REFERENCE_ID, referenceId).where(PROFILES.EID.in(unrequestedEIDs.stream().map(eid -> eid.eid).collect(Collectors.toList()))).execute();
        ctx.insertInto(REFERENCE_IDS).set(new ReferenceIdsRecord(referenceId, new Timestamp(System.currentTimeMillis()), count, refInfo1, refInfo2, refInfo3, new Timestamp(retrieveTime.toInstant().toEpochMilli()))).execute();
    }

    public List<ReferenceIdRecord> getReferenceIds() {
        return transactionManager.eval(ctx -> {
            List<ReferenceIdRecord> fetch = ctx.select(REFERENCE_IDS.REFERENCE_ID, REFERENCE_IDS.REQUESTED_TS, REFERENCE_IDS.REQUEST_COUNT, REFERENCE_IDS.REFINFO1, REFERENCE_IDS.REFINFO2, REFERENCE_IDS.REFINFO3, REFERENCE_IDS.AVAILABLE_TS).from(REFERENCE_IDS).orderBy(REFERENCE_IDS.AVAILABLE_TS.asc()).fetch(r -> {
                ReferenceIdRecord result = new ReferenceIdRecord();
                result.referenceId = r.value1();
                result.requestedAt = r.value2().toInstant();
                result.requestCount = r.value3();
                result.refInfo1 = r.value4();
                result.refInfo2 = r.value5();
                result.refInfo3 = r.value6();
                result.availableAt = r.value7().toInstant();
                return result;
            });
            return fetch;
        });
    }

    public int addProfileData(DSLContext db, List<RetrieveBulkEncProfilesResponse.Profile> profileList, final String kPubDP, final String sigKPubDP) {
        int updateCount = 0;
        for (RetrieveBulkEncProfilesResponse.Profile p : profileList) {
            updateCount += db.update(PROFILES)
                    .set(PROFILES.MAC, p.getMac())
                    .set(PROFILES.EKPUBDP, p.geteKpubDP())
                    .set(PROFILES.ENCP, p.getEncP())
                    .set(PROFILES.KPUBDP, kPubDP)
                    .set(PROFILES.SIGEKPUB, p.getSigEKpubDP())
                    .set(PROFILES.SIGKPUBDP, sigKPubDP)
                    .set(PROFILES.UPDATE_TS, new Timestamp(System.currentTimeMillis()))
                    .where(PROFILES.EID.eq(p.getEid())).execute();
        }
        return updateCount;
    }

    public <R> R evalTransactional(CheckedFunction<DSLContext, R> f) {
        return transactionManager.eval(f::apply);
    }

    public void runTransactional(CheckedConsumer<DSLContext> f) {
        transactionManager.run(f::accept);
    }

    public void finishReferenceId(DSLContext db, String referenceId) {
        db.deleteFrom(REFERENCE_IDS).where(REFERENCE_IDS.REFERENCE_ID.eq(referenceId)).execute();
    }

    public void deleteReferenceId(String referenceId) {
        transactionManager.run(ctx -> {
            ctx.deleteFrom(REFERENCE_IDS).where(REFERENCE_IDS.REFERENCE_ID.eq(referenceId)).execute();
            ctx.update(PROFILES).set(PROFILES.REFERENCE_ID, (String) null).where(PROFILES.REFERENCE_ID.eq(referenceId)).execute();
        });
    }

    public interface CheckedConsumer<T> {
        void accept(T var1) throws Exception;
    }

    public interface CheckedFunction<T, R> {
        R apply(T var1) throws Exception;
    }

}
