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
