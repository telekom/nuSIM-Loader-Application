package de.scoopgmbh.nusimapp.provisioning;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;
import de.scoopgmbh.nusimapp.PathUtil;
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.db.jooq.tables.pojos.Profiles;
import de.scoopgmbh.nusimapp.if1.GetEncProfileResponse;
import de.scoopgmbh.nusimapp.if1.GetKPubDPResponse;
import de.scoopgmbh.nusimapp.if1.IF1Exception;
import de.scoopgmbh.nusimapp.if1.QueryProfileStockResponse;
import de.scoopgmbh.nusimapp.if1.RequestBulkEncProfilesRequest;
import de.scoopgmbh.nusimapp.if1.RequestBulkEncProfilesResponse;
import de.scoopgmbh.nusimapp.if1.RetrieveBulkEncProfilesResponse;
import de.scoopgmbh.nusimapp.if3.IF3Exception;
import de.scoopgmbh.nusimapp.lifecycle.Managed;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NoEidAvailableException;
import de.scoopgmbh.nusimapp.nusimsim.adapter.ProfileLoadingException;
import de.scoopgmbh.nusimapp.provisioning.single.CertificateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.ExecStarter;
import ratpack.exec.Execution;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ProvisioningModule implements Managed {
    private static final Logger logger = LoggerFactory.getLogger(ProvisioningModule.class);

    private final DAO dao;
    private final Supplier<? extends Config> config;
    private final String kPubCI;
    private final LoaderAppApplicationContext context;
    private final String rootKeyId;
    private GetKPubDPResponse kPubDPResponse;
    private boolean started;
    private int provisioningCount;
    private String refInfo1;
    private String refInfo2;
    private String refInfo3;
    private ExecStarter execStarter;
    private CertificateSource certificateSource;
    private ProvisioningMode provisioningMode;
    private boolean repeatableDownloadsFeature;

    private enum ProvisioningMode {
        single("Single Mode Provisioning"),
        bulk("Bulk Mode Provisioning");

        private final String name;

        ProvisioningMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public ProvisioningModule(DAO dao, Supplier<? extends Config> config, String rootKeyId, String kPubCI, LoaderAppApplicationContext context) {
        this.dao = dao;
        this.config = config;
        this.kPubCI = kPubCI;
        this.context = context;
        this.rootKeyId = rootKeyId;
    }

    private ExecStarter getExecution() {
        if (execStarter == null) {
            execStarter = Execution.fork();
        }
        return execStarter;
    }

    private void run() {
        final Instant startTime = Instant.now();
        try {
            Path provisioning = PathUtil.getAppHomeDir().resolve("provisioning");
            Files.createDirectories(provisioning);
            final Path fileName = provisioning.resolve("provisioning_" + DateTimeFormatter.ofPattern("uuuu-MM-dd_HH-mm-ss").withZone(ZoneId.systemDefault()).format(startTime) + ".csv");
            try (ProvisioningLogWriter writer = new ProvisioningLogWriter(fileName, startTime, provisioningCount, refInfo1, refInfo2, refInfo3)) {
                int i = 1;
                String eid = null;
                try {
                    init();
                    for (; i <= provisioningCount && started; i++) {

                        logger.trace("#{}: retrieving EID...", i);
                        eid = context.getNusimSimAdaptor().getEID();
                        dao.createRecord(eid, refInfo1, refInfo2, refInfo3);
                        ProvisioningResult provisioningResult;
                        switch (provisioningMode) {
                            case bulk:
                                provisioningResult = performBulkModeProvisioning(i, eid, refInfo1, refInfo2, refInfo3);
                                break;
                            case single:
                                provisioningResult = performSingleModeProvisioning(i, eid, refInfo1, refInfo2, refInfo3);
                                break;
                            default:
                                throw new IllegalArgumentException("unsupported enum " + provisioningMode);
                        }
                        writer.write(provisioningResult);
                    }
                    writer.writeSuccess();
                } catch (NoEidAvailableException | IF1Exception | IF3Exception | ProfileLoadingException | RuntimeException e) {
                    logger.error("could not perform provisioning task at number {}: {}", i, e.toString());
                    writer.writeException(e);
                    dao.addError(eid, getFullExceptionStackTrace(e));
                }
                logger.info("finished {} with {} of {} provisions", provisioningMode, i - 1, provisioningCount);
                logger.info("ICCID file written to {}", fileName);
            }
        } catch (IOException e) {
            logger.error("could not start provisioning: " + e.toString());
        } finally {
            started = false;
        }
    }

    private static String getFullExceptionStackTrace(Exception e) {
        try (StringWriter sw = new StringWriter()) {
            try (PrintWriter pw = new PrintWriter(sw)) {
                e.printStackTrace(pw);
                return sw.toString();
            }
        } catch (IOException ignore) {
            return e.toString();
        }
    }

    public void start(int provisioningCount, String refInfo1, String refInfo2, String refInfo3) {
        Config currentConfig = this.config.get();

        this.provisioningCount = provisioningCount;
        this.refInfo1 = refInfo1;
        this.refInfo2 = refInfo2;
        this.refInfo3 = refInfo3;
        this.started = true;
        this.provisioningMode = currentConfig.getConfig("provisioning").getEnum(ProvisioningMode.class, "mode");
        this.certificateSource = currentConfig.getConfig("provisioning").getEnum(CertificateSource.class, "certificateSource");
        this.repeatableDownloadsFeature = currentConfig.getConfig("provisioning").getBoolean("repeatableDownloadsFeature");
        logger.info("starting {} with {} provisions to do (refInfo1={}, refInfo2={}, refInfo3={})", provisioningMode, provisioningCount, refInfo1, refInfo2, refInfo3);

        getExecution().start(e -> run());
    }

    private String pemToDer(String in) {
        return in.replaceAll("(-+BEGIN CERTIFICATE-+\\n|\\n|-+END CERTIFICATE-+\\n)", "");
    }

    @Override
    public String getName() {
        return ProvisioningModule.class.getName();
    }

    @Override
    public void start() {
        // NOOP
    }

    public void stop() {
        logger.info("awaiting termination of {}...", provisioningMode);
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    private void init() throws IF1Exception {
        if (provisioningMode == null || certificateSource == null) {
            Config currentConfig = config.get();
            provisioningMode = currentConfig.getConfig("provisioning").getEnum(ProvisioningMode.class, "mode");
            certificateSource = currentConfig.getConfig("provisioning").getEnum(CertificateSource.class, "certificateSource");
        }

        if (isDpAuthRequired()) {
            final Config config = this.config.get();
            if (!config.getConfig("if1").getConfig("KpubDP").hasPath("KpubDP")) {
                throw new IF1Exception("DP_AUTH is required, but KPubDP configuration is missing. Please run \"Get KpubDP\" first.");
            }
            logger.info("DP_AUTH is required - reading KPubDP from configuration");
            try {
                kPubDPResponse = new GetKPubDPResponse(config.getString("if1.KpubDP.KpubDP"), config.getString("if1.KpubDP.SigKpubDP"), config.getString("if1.KpubDP.KpubCI"));
            } catch (ConfigException.Missing e) {
                throw new IF1Exception("could not read KPubDP from configuration: " + e.getMessage(), e);
            }
            logger.debug("KPubDP is " + kPubDPResponse);
        } else {
            kPubDPResponse = new GetKPubDPResponse(null, null, null);
        }
    }

    private GetEncProfileResponse.ResultPart getEncProfile(String eid, String derCertificate, String refInfo1, String refInfo2, String refInfo3) throws IF1Exception {
        logger.info("retrieving profile for EID {}...", eid);
        return context.getIf1Adapter().getEncProfile(rootKeyId, eid, isDpAuthRequired(), derCertificate, refInfo1, refInfo2, refInfo3);
    }

    public RequestBulkEncProfilesResponse.ResultPart requestBulkEncProfiles(List<RequestBulkEncProfilesRequest.Eid> eids, String refInfo1, String refInfo2, String refInfo3) throws IF1Exception {
        logger.info("requesting profiles for {} EIDs...", eids.size());
        List<RequestBulkEncProfilesRequest.Eid> collect = eids.stream().map(e -> new RequestBulkEncProfilesRequest.Eid(e.eid, pemToDer(e.certNUSIM))).collect(Collectors.toList());
        return context.getIf1Adapter().requestBulkEncProfiles(rootKeyId, isDpAuthRequired(), refInfo1, refInfo2, refInfo3, collect);
    }

    public RetrieveBulkEncProfilesResponse.ResultPart retrieveBulkEncProfiles(String referenceId) throws IF1Exception {
        logger.info("retrieving profiles for referenceId {}...", referenceId);
        return context.getIf1Adapter().retrieveBulkEncProfiles(referenceId);
    }

    public QueryProfileStockResponse.ResultPart queryProfileStock(String refInfo1, String refInfo2, String refInfo3) throws IF1Exception {
        try {
            Path provisioning = PathUtil.getAppHomeDir().resolve("provisioning");
            Files.createDirectories(provisioning);
            final Path fileName = provisioning.resolve("queryProfileStock_" + DateTimeFormatter.ofPattern("uuuu-MM-dd").withZone(ZoneId.systemDefault()).format(Instant.now()) + ".csv");
            try (final QueryProfileStockWriter w = new QueryProfileStockWriter(fileName)) {
                QueryProfileStockResponse encProfile = context.getIf1Adapter().queryProfileStock(refInfo1, refInfo2, refInfo3);
                w.write(refInfo1, refInfo2, refInfo3, encProfile);
                if (encProfile.getError() != null) {
                    throw new IF1Exception(encProfile.getError().getErrorCode(), encProfile.getError().getErrorMessage());
                }
                return encProfile.getResult();
            }
        } catch (IOException ex) {
            throw new IF1Exception("Could not write log file: " + ex.toString());
        }
    }

    public String getSigKpubDP() throws IF1Exception {
        if (kPubDPResponse == null) {
            init();
        }
        return kPubDPResponse.getResult().getSigKpubDP();
    }

    public String getkPubDP() throws IF1Exception {
        if (kPubDPResponse == null) {
            init();
        }
        return kPubDPResponse.getResult().getkPubDP();
    }

    public static final class ProvisioningResult {
        private final int index;
        private final String iccid;
        private final String eid;

        public ProvisioningResult(int index, String iccid, String edit) {
            this.index = index;
            this.iccid = iccid;
            this.eid = edit;
        }

        @Override
        public String toString() {
            return index + ", " + iccid + ", " + eid;
        }
    }

    private String retrieveNusimCertificate(final String eid) throws IF3Exception {
        switch (certificateSource) {
            case nusim:
                return context.getNusimSimAdaptor().getCertNusim();
            case cm:
                return context.getIf3Adapter().getCertificate(eid, rootKeyId);
            case db:
                return dao.getNusimCertificate(eid).orElseThrow(() -> new IF3Exception("no certificate found for eid " + eid));
            default:
                throw new IllegalArgumentException("certificateSource " + certificateSource + " not supported");
        }
    }

    private ProvisioningResult performSingleModeProvisioning(int n, String eid, String refInfo1, String refInfo2, String refInfo3) throws IF1Exception, ProfileLoadingException, IF3Exception {
        logger.info("#{}: retrieving nuSIM Certificate from {}...", n, certificateSource.name());
        final String nusimCertificate = retrieveNusimCertificate(eid);
        dao.addNusimCertificate(eid, nusimCertificate);

        final String derCertificate = pemToDer(nusimCertificate);

        final GetEncProfileResponse.ResultPart r = getEncProfile(eid, derCertificate, refInfo1, refInfo2, refInfo3);

        logger.info("#{}: loading profile for EID {}", n, r.getEid());
        final String iccid = context.getNusimSimAdaptor().loadProfile(r.getEid(), r.getMac(), r.getEncP(), r.geteKpubDP(), r.getSigEKpubDP(), getkPubDP(), getSigKpubDP(), kPubCI);

        logger.info("#{}: success (EID {}, ICCID {})", n, eid, iccid);
        dao.addIccid(eid, iccid);

        if (repeatableDownloadsFeature) {
            logger.trace("#{}: NOT removing provisioned profile data from lookup table: repeatableDownloadsFeature is active", n);
        } else {
            logger.trace("#{}: removing successfully provisioned profile data from lookup table", n);
            dao.deleteProfile(eid);
        }
        return new ProvisioningResult(n, iccid, eid);
    }

    protected ProvisioningResult performBulkModeProvisioning(int n, String eid, String refInfo1, String refInfo2, String refInfo3) throws IF1Exception, ProfileLoadingException {
        logger.info("#{}: retrieving provisioning data from DB...", n);
        Profiles profile = dao.readProfile(eid);
        if (profile == null) {
            throw new ProfileLoadingException("No Profile data found for EID " + eid);
        }

        logger.info("#{}: loading profile for EID {}", n, eid);
        final String iccid = context.getNusimSimAdaptor().loadProfile(profile.getEid(), profile.getMac(), profile.getEncp(), profile.getEkpubdp(), profile.getSigekpub(), getkPubDP(), getSigKpubDP(), kPubCI);

        logger.info("#{}: success (EID {}, ICCID {})", n, eid, iccid);
        dao.addIccid(eid, iccid);

        if (repeatableDownloadsFeature) {
            logger.trace("#{}: NOT removing provisioned profile data from lookup table: repeatableDownloadsFeature is active", n);
        } else {
            logger.trace("#{}: removing successfully provisioned profile data from lookup table", n);
            dao.deleteProfile(eid);
        }

        return new ProvisioningResult(n, iccid, eid);
    }

    private boolean isDpAuthRequired() {
        if (this.provisioningMode == ProvisioningMode.single) {
            final String nusimCapabilities = this.context.getNusimSimAdaptor().getNusimCapabilities();
            return this.context.getNusimSimAdaptor().isDPAuthSupported(nusimCapabilities);
        } else {
            final Config config = this.config.get();
            if (config.getConfig("if1").hasPath("bulkRequestAuthFlag")) {
                return config.getConfig("if1").getBoolean("bulkRequestAuthFlag");
            } else {
                return false;
            }
        }
    }
}
