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

package de.scoopgmbh.nusimapp.if3;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public abstract class IF3Adapter {

    private final Config adapterConfig;

    /**
     * this constructor will be called by the adapter factory
     *
     * @param adapterConfig the configuration section specific for this adapter
     * @see IF3HTTPAdapter for a reference implementation
     * @see IF3AdapterFactory for the adapter factory
     * @see LoaderAppApplicationContext#getIf3Adapter() for accessing the adapter
     */
    IF3Adapter(Config adapterConfig) {
        this.adapterConfig = adapterConfig;
    }

    /**
     * retrieves a certificate from the chip manufacturer. This default implementation delegates to {@link #getCertificates(Collection, String)}
     *
     * @param eid       the EID the certificate is requested for
     * @param rootKeyId the root key ID to be used
     * @return the certificate in PEM format
     * @see #getCertificates(Collection, String)
     */
    public String getCertificate(String eid, String rootKeyId) throws IF3Exception {
        Map<String, String> certificates = getCertificates(Collections.singletonList(eid), rootKeyId);
        String cert = certificates.get(eid);
        if (cert == null || cert.isEmpty()) {
            throw new IF3Exception("no data for EID retrieved in response");
        } else {
            return cert;
        }
    }

    /**
     * retrieves certificates from the chip manufacturer
     *
     * @param eids      the EIDs the certifates are requested for
     * @param rootKeyId the root key ID to be used
     * @return a map containing all retrieved certificates, keyed by their eid
     */
    public abstract Map<String, String> getCertificates(final Collection<String> eids, String rootKeyId) throws IF3Exception;

    public int getMaxRequestQuantity() {
        return adapterConfig.getInt("maxRequestQuantity");
    }

    public String getEidPrefix() {
        return adapterConfig.getString("eidPrefix");
    }
}
