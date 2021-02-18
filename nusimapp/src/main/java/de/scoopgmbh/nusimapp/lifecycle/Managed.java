package de.scoopgmbh.nusimapp.lifecycle;

/**
 * Created on 26.04.17.
 */
public interface Managed {
    String getName();

    void start() throws Exception;

    void stop() throws Exception;
}
