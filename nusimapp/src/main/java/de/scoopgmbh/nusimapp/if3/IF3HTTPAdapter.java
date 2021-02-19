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
import com.typesafe.config.ConfigBeanFactory;
import de.scoopgmbh.nusimapp.http.HttpSender;
import de.scoopgmbh.nusimapp.http.HttpSenderConfig;

public class IF3HTTPAdapter extends IF3Adapter {
    private HttpSender sender;

    public IF3HTTPAdapter(Config config) throws Exception {
        super(config);
        HttpSenderConfig httpSenderConfig = ConfigBeanFactory.create(config, HttpSenderConfig.class);
        sender = new HttpSender(httpSenderConfig, "CM connector");
        sender.start();
    }

    @Override
    public String getCertificate(String eid, String rootKeyId) throws IF3Exception {
        try {
            return sender.send("GetCertFromCM", new GetCertFromCMRequest(eid, rootKeyId), GetCertFromCMResponse.class).getCert();
        } catch (Exception e) {
            throw new IF3Exception(e);
        }
    }
}
