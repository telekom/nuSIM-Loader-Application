package de.scoopgmbh.nusimapp;

import java.time.Duration;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.db.DAO;
import de.scoopgmbh.nusimapp.db.DBUpdater;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NusimSimAdapter;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NusimSimAdapterFactory;
import de.scoopgmbh.nusimapp.fileimport.FileImporter;
import de.scoopgmbh.nusimapp.if1.IF1Adapter;
import de.scoopgmbh.nusimapp.if3.IF3Adapter;
import de.scoopgmbh.nusimapp.if3.IF3AdapterFactory;
import de.scoopgmbh.nusimapp.lifecycle.AbstractApplicationContext;
import de.scoopgmbh.nusimapp.provisioning.ProvisioningModule;
import de.scoopgmbh.nusimapp.server.ServerStateProvider;

public class LoaderAppApplicationContext extends AbstractApplicationContext {

    private ServerStateProvider serverStateProvider;

    @Override
    protected void init() {
        Config currentConfig = getConfig();
        
        addManaged(new DAO(currentConfig.getConfig("database")));
        addManaged(new DBUpdater(getDAO()));
        addManaged(new FileImporter(currentConfig.getConfig("fileimport"), getDAO()));
        addManaged(new IF1Adapter(currentConfig.getConfig("if1"), () -> getConfig().getConfig("if1").getString("certCM")));
        addManaged(new IF3AdapterFactory(this));
        addManaged(new NusimSimAdapterFactory(this));
        addManaged(new ProvisioningModule(getDAO(), this::getConfig, currentConfig.getConfig("provisioning").getString("rootKeyId"), currentConfig.getConfig("provisioning").getString("kPubCI"), this));
        serverStateProvider = new ServerStateProvider(Duration.ofMillis(500), getProvisioning());
    }

    public NusimSimAdapter getNusimSimAdaptor() {
        NusimSimAdapterFactory a = getManaged(NusimSimAdapterFactory.NAME);
        return a.getAdapter();
    }

    public IF1Adapter getIf1Adapter() {
        return getManaged("IF1Adapter");
    }

    public IF3Adapter getIf3Adapter() {
        IF3AdapterFactory a = getManaged(IF3AdapterFactory.NAME);
        return a.getAdapter();
    }

    public DAO getDAO() {
        return getManaged(DAO.NAME);
    }

    public ProvisioningModule getProvisioning() {
        return getManaged(ProvisioningModule.class.getName());
    }

    public ServerStateProvider getServerStateProvider() {
        return serverStateProvider;
    }
}
