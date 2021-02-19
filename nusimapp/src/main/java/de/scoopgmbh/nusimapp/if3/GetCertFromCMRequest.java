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

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCertFromCMRequest {
    private String eid;
    @JsonProperty("rootKeyID")
    private String rootKeyId;

    public GetCertFromCMRequest() {
    }

    public GetCertFromCMRequest(String eid, String rootKeyId) {
        this.eid = eid;
        this.rootKeyId = rootKeyId;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getRootKeyId() {
        return rootKeyId;
    }

    public void setRootKeyId(String rootKeyId) {
        this.rootKeyId = rootKeyId;
    }
}
