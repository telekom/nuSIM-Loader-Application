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

import com.fasterxml.jackson.annotation.JsonProperty;

public class RetrieveBulkEncProfilesRequest {
    private String reqVersion = "1.0";

    @JsonProperty("referenceID")
    private String referenceId;

    public RetrieveBulkEncProfilesRequest() {
    }

    public RetrieveBulkEncProfilesRequest(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReqVersion() {
        return reqVersion;
    }

    public String getReferenceId() {
        return referenceId;
    }
}
