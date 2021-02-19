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

package de.scoopgmbh.nusimapp.server;

public enum ErrorMessages {
    DEFAULT(500, "An error occurred. Please contact you administrator and check the logs"),
    CHANGE_PASSWORD(500, "Passwort konnte nicht aktualisiert werden"),
    DELETE_USER(500, "Beim Löschen ist ein Fehler aufgetreten");


    private final int httpStatus;
    private final String message;

    ErrorMessages(int httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
