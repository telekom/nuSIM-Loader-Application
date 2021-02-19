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
