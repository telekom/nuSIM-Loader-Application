package de.scoopgmbh.nusimapp.if3;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;
import de.scoopgmbh.nusimapp.lifecycle.Managed;

public class IF3AdapterFactory implements Managed {

    public static final String NAME = "if3AdapterFactory";

    private final LoaderAppApplicationContext context;
    private IF3Adapter adapter;

    public IF3AdapterFactory(LoaderAppApplicationContext context) {
        this.context = context;
    }

    public IF3Adapter getAdapter() {
        return adapter;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        Config if3Config = context.getConfig().getConfig("if3");
        String adapterClassName = if3Config.getString("adapterClass");
        Class<? extends IF3Adapter> adapterClazz = Class.forName(adapterClassName).asSubclass(IF3Adapter.class);
        adapter = adapterClazz.getConstructor(Config.class).newInstance(if3Config);
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public void stop() throws Exception {
    }
}
