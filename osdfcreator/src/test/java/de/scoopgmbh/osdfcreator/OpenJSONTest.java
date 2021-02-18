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
