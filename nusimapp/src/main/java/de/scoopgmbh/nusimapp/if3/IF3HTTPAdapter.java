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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class IF3HTTPAdapter extends IF3Adapter {
    private static final Logger logger = LoggerFactory.getLogger(IF3HTTPAdapter.class);
    private HttpSender sender;

    public IF3HTTPAdapter(Config config) throws Exception {
        super(config);
        HttpSenderConfig httpSenderConfig = ConfigBeanFactory.create(config, HttpSenderConfig.class);
        sender = new HttpSender(httpSenderConfig, "CM connector");
        sender.start();
    }

    @Override
    public Map<String, String> getCertificates(Collection<String> eids, String rootKeyId) throws IF3Exception {
        try {
            List<GetBulkCertFromCMRequest.EIDEntry> eidEntries = eids.stream().map(GetBulkCertFromCMRequest.EIDEntry::new).collect(Collectors.toList());
            GetBulkCertFromCMResponse response = sender.send("GetBulkCertificates", new GetBulkCertFromCMRequest(rootKeyId, eidEntries), GetBulkCertFromCMResponse.class);
            if ("Error".equals(response.getStatus())) {
                throw new IF3Exception("general error requesting " + eids.size() + " certificates: retrieved error code " + response.getError().getErrorCode() + ": " + response.getError().getErrorMessage());
            } else {
                Map<String, String> result = new HashMap<>();
                for (GetBulkCertFromCMResponse.Certificate c : response.getResult().getCertificateList()) {
                    if (c.getEid() == null) {
                        continue;
                    }
                    if (c.getErrorCode() != null || c.getErrorMessage() != null) {
                        logger.error("error code {} retrieved for EID {}: {}", c.getErrorCode(), c.getEid(), c.getErrorMessage());
                    } else {
                        result.put(c.getEid(), c.getCertNusim());
                    }
                }
                return result;
            }
        } catch (Exception e) {
            throw new IF3Exception(e);
        }
    }
}
