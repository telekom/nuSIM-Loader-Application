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

package de.scoopgmbh.nusimapp.if1;

import java.util.List;
import java.util.function.Supplier;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import de.scoopgmbh.nusimapp.http.HttpSender;
import de.scoopgmbh.nusimapp.http.HttpSenderConfig;
import de.scoopgmbh.nusimapp.lifecycle.Managed;

import javax.annotation.Nullable;

public class IF1Adapter implements Managed {
    public static final String NAME = "IF1Adapter";
    private HttpSender sender;

    private static final String UNUSED = "not used";
    private final Supplier<String> certCMSupplier;
    private final HttpSenderConfig httpSenderConfig;

    public IF1Adapter(Config config, Supplier<String> certCMSupplier) {
        httpSenderConfig = ConfigBeanFactory.create(config, HttpSenderConfig.class);
        this.certCMSupplier = certCMSupplier;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Nullable
    private String getCertCM() {
        final String certCM = certCMSupplier.get();
        return UNUSED.equals(certCM) ? null : certCM;
    }

    @Override
    public void start() throws Exception {
        sender = new HttpSender(httpSenderConfig, "DP connector");
        sender.start();
    }

    @Override
    public void stop() {
        if (sender != null) {
            sender.stop();
        }
    }

    public GetEncProfileResponse.ResultPart getEncProfile(String rootKeyId, String eid, boolean dpAuthRequired, String derCertificate, String refInfo1, String refInfo2, String refInfo3) throws IF1Exception {
        final GetEncProfileRequest request = new GetEncProfileRequest(rootKeyId, eid, dpAuthRequired ? "1" : "0", derCertificate, getCertCM(), refInfo1, refInfo2, refInfo3);
        try {
            GetEncProfileResponse result = sender.send("GetEncProfile", request, GetEncProfileResponse.class);
            if (result.getError() != null) {
                throw new IF1Exception(result.getError().getErrorCode(), "Could not call GetEncProfile: " + result.getError().getErrorMessage());
            }
            return result.getResult();
        } catch (Exception e) {
            throw new IF1Exception("Could not call GetEncProfile", e);
        }
    }

    public RequestBulkEncProfilesResponse.ResultPart requestBulkEncProfiles(String rootKeyId, boolean dpAuthRequired, String refInfo1, String refInfo2, String refInfo3, List<RequestBulkEncProfilesRequest.Eid> list) throws IF1Exception {
        final RequestBulkEncProfilesRequest request = new RequestBulkEncProfilesRequest(rootKeyId, dpAuthRequired ? "1" : "0", getCertCM(), refInfo1, refInfo2, refInfo3, String.valueOf(list.size()), list);
        try {
            RequestBulkEncProfilesResponse result = sender.send("RequestBulkEncProfiles", request, RequestBulkEncProfilesResponse.class);
            if (result.getError() != null) {
                throw new IF1Exception(result.getError().getErrorCode(), "Could not call RequestBulkEncProfiles: " + result.getError().getErrorMessage());
            }
            return result.getResult();
        } catch (Exception e) {
            throw new IF1Exception("Could not call RequestBulkEncProfiles", e);
        }
    }

    public RetrieveBulkEncProfilesResponse.ResultPart retrieveBulkEncProfiles(String referenceId) throws IF1Exception {
        final RetrieveBulkEncProfilesRequest request = new RetrieveBulkEncProfilesRequest(referenceId);
        try {
            RetrieveBulkEncProfilesResponse result = sender.send("RetrieveBulkEncProfiles", request, RetrieveBulkEncProfilesResponse.class);
            if (result.getError() != null) {
                throw new IF1Exception(result.getError().getErrorCode(), "Could not call RetrieveBulkEncProfiles: " + result.getError().getErrorMessage());
            }
            return result.getResult();
        } catch (IF1Exception ex) {
            throw ex;
        } catch (Exception e) {
            throw new IF1Exception("Could not call RetrieveBulkEncProfiles", e);
        }
    }

    public QueryProfileStockResponse queryProfileStock(String refInfo1, String refInfo2, String refInfo3) throws IF1Exception {
        final QueryProfileStockRequest request = new QueryProfileStockRequest(refInfo1, refInfo2, refInfo3);
        try {
            return sender.send("QueryProfileStock", request, QueryProfileStockResponse.class);
        } catch (Exception e) {
            throw new IF1Exception("Could not call QueryProfileStock", e);
        }
    }

    public GetKPubDPResponse getKPubDP(GetKPubDPRequest request) throws IF1Exception {
        try {
            return sender.send("GetKpubDP", request, GetKPubDPResponse.class);
        } catch (Exception e) {
            throw new IF1Exception("Could not call GetKpubDP", e);
        }
    }
}
