package de.scoopgmbh.nusimapp.lifecycle;

import com.typesafe.config.Config;

public interface ApplicationContext {

    <T extends Managed> T getManaged(String name);

    Config getConfig();

}
