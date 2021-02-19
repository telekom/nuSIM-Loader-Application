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

public class IF1Exception extends Exception {
    private static final long serialVersionUID = -982070338500363977L;

    private final String errorCode;

    public IF1Exception(String message) {
        super("Internal error while communicating with DP: " + message);
        this.errorCode = "ERR";
    }

    public IF1Exception(String message, Throwable cause) {
        super("Internal error while communicating with DP: " + message + ": " + cause.getMessage(), cause);
        this.errorCode = "ERR";
    }

    public IF1Exception(String errorCode, String message) {
        super("DP returned error code " + errorCode + ": " + message);
        this.errorCode = errorCode;
    }

    public IF1Exception(String errorCode, String message, Throwable cause) {
        super("DP returned error code " + errorCode + ": " + message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
