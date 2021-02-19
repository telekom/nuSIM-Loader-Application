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

package de.scoopgmbh.nusimapp.nusimsim.adapter;

public class NoEidAvailableException extends Exception {
    private static final long serialVersionUID = -8894121522092654195L;

    public NoEidAvailableException() {
    }

    public NoEidAvailableException(String message) {
        super(message);
    }

    public NoEidAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEidAvailableException(Throwable cause) {
        super(cause);
    }

    public NoEidAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
