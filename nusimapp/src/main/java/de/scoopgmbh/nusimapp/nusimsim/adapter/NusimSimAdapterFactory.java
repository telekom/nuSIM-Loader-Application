package de.scoopgmbh.nusimapp.nusimsim.adapter;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;
import de.scoopgmbh.nusimapp.lifecycle.Managed;

public class NusimSimAdapterFactory implements Managed {

    public static final String NAME = "nusimSimAdapterFactory";

    private final LoaderAppApplicationContext context;
    private NusimSimAdapter adapter;

    public NusimSimAdapterFactory(LoaderAppApplicationContext context) {
        this.context = context;
    }

    public NusimSimAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        Config adapterConfig = context.getConfig().getConfig("nusimSimAdapter");
        String adapterClassName = adapterConfig.getString("adapterClass");
        Class<? extends NusimSimAdapter> adapterClazz = Class.forName(adapterClassName).asSubclass(NusimSimAdapter.class);
        adapter = adapterClazz.getConstructor(Config.class).newInstance(adapterConfig);
    }

    @Override
    public void stop() throws Exception {
        if (adapter != null) {
            adapter.stop();
        }
    }
}
