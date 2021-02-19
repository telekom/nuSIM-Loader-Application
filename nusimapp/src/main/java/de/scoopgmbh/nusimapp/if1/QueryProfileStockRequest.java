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

import java.util.Optional;

public class QueryProfileStockRequest {
    private String reqVersion = "1.0";
    private String refInfo1;
    private String refInfo2;
    private String refInfo3;

    public QueryProfileStockRequest() {
    }

    public QueryProfileStockRequest(String refInfo1, String refInfo2, String refInfo3) {
        this.refInfo1 = Optional.ofNullable(refInfo1).orElse("");
        this.refInfo2 = Optional.ofNullable(refInfo2).orElse("");
        this.refInfo3 = Optional.ofNullable(refInfo3).orElse("");
    }

    public String getReqVersion() {
        return reqVersion;
    }

    public String getRefInfo1() {
        return refInfo1;
    }

    public String getRefInfo2() {
        return refInfo2;
    }

    public String getRefInfo3() {
        return refInfo3;
    }
}
