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
import org.junit.internal.matchers.StringContains;

import static org.junit.Assert.assertEquals;

public class OpenJSONTest {

    @Test
    public void openJSON() {
        JSONObject jsonObject = OSDFCreator.openJSON("src/test/resources/testBackend.json");
        String testJSON = jsonObject.get("dependencies").toString();
        Assert.assertThat(testJSON, StringContains.containsString("GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1"));
    }

    @Test(expected = NullPointerException.class)
    public void openNonJSON() {
        JSONObject jsonObject = OSDFCreator.openJSON("src/test/resources/someSample.txt");
        String testJSON = jsonObject.get("dependencies").toString();
    }

}
