package de.scoopgmbh.nusimapp.if3;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;

public abstract class IF3Adapter {

    /**
     * this constructor will be called by the adapter factory
     * @param adapterConfig the configuration section specific for this adapter
     * @see IF3HTTPAdapter for a reference implementation
     * @see IF3AdapterFactory for the adapter factory
     * @see LoaderAppApplicationContext#getIf3Adapter() for accessing the adapter
     */
    IF3Adapter(Config adapterConfig) {
    }

    /**
     * retrieves a certificate from the chip manufacturer
     * @param eid the EID the certifcate is requested for
     * @param rootKeyId the root key ID to be used
     * @return the certificate in PEM format
     */
    public abstract String getCertificate(final String eid, String rootKeyId) throws IF3Exception;
}
