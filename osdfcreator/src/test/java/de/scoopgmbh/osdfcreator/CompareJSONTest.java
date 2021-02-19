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

package de.scoopgmbh.osdfcreator;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class CompareJSONTest {

    @Test
    public void newDepsBackend() {
        JSONObject firstJSON = OSDFCreator.openJSON("src/test/resources/testBackend.json");
        JSONObject secondJSON = OSDFCreator.openJSON("src/test/resources/testSecondBackend.json");
        Assert.assertTrue(OSDFCreator.doNewBackendDepsExist(firstJSON, secondJSON));
    }

    @Test
    public void noNewDepsBackend() {
        JSONObject firstJSON = OSDFCreator.openJSON("src/test/resources/testBackend.json");
        JSONObject secondJSON = OSDFCreator.openJSON("src/test/resources/testBackend.json");
        Assert.assertFalse(OSDFCreator.doNewBackendDepsExist(firstJSON, secondJSON));
    }

    @Test
    public void newVersionDepsBackend() {
        JSONObject firstJSON = OSDFCreator.openJSON("src/test/resources/testBackend.json");
        JSONObject secondJSON = OSDFCreator.openJSON("src/test/resources/testSecondBackend.json");
        Assert.assertTrue(OSDFCreator.doNewBackendDepsExist(firstJSON, secondJSON));
    }

    @Test
    public void newDepsJavaScript() {
        JSONObject firstJSON = OSDFCreator.openJSON("src/test/resources/testJavaScript.json");
        JSONObject secondJSON = OSDFCreator.openJSON("src/test/resources/testSecondJavaScript.json");
        Assert.assertTrue(OSDFCreator.doNewJavaScriptDepsExist(firstJSON, secondJSON));
    }

    @Test
    public void noNewDepsJavaScript() {
        JSONObject firstJSON = OSDFCreator.openJSON("src/test/resources/testJavaScript.json");
        JSONObject secondJSON = OSDFCreator.openJSON("src/test/resources/testJavaScript.json");
        Assert.assertFalse(OSDFCreator.doNewJavaScriptDepsExist(firstJSON, secondJSON));
    }
}
