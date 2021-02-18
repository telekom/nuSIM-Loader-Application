package de.scoopgmbh.osdfcreator;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class OSDFCreator {

    private static String adocFileName;
    private static final String backendLicenseReport = "../build/licenses/index.json";
    private static final String ALLLICENSESBACKEND = "src/main/resources/allLicensesBackend.json";
    private static final String javaScriptLicenseReport = "../gui/build/javascript-licensesText.json";
    private static final String ALLLICENSESJAVASCRIPT = "src/main/resources/allLicensesJavaScript.json";
    private final static Logger LOGGER = Logger.getLogger(OSDFCreator.class.getName());

    public static void main(String[] args) throws Exception {
        adocFileName = args[0];
        if (doNewBackendDepsExist(openJSON(backendLicenseReport), openJSON(ALLLICENSESBACKEND)) || doNewJavaScriptDepsExist(openJSON(javaScriptLicenseReport), openJSON(ALLLICENSESJAVASCRIPT))) {
            throw new Exception("Dependencies have changed, check log for further information.");
        } else {
            new File("../build/asciidocs/").mkdirs();
            createOpening();
            writeGradleDepends();
            writeGUIDepends();
            createEnding();
        }
    }

    public static boolean doNewBackendDepsExist(JSONObject licenseReporter, JSONObject documentedLicenses) {
        boolean retVal = false;
        HashSet<String> notDocumented = new HashSet<>();
        HashSet<String> newVersionList = new HashSet<>();
        JSONArray depListReporter = (JSONArray) licenseReporter.get("dependencies");
        JSONArray depListDocumented = (JSONArray) documentedLicenses.get("dependencies");

        for (Object objReporter : depListReporter) {
            JSONObject licenceReporter = (JSONObject) objReporter;
            boolean foundEntry = false;
            boolean newVersion = false;
            for (Object objDocumented : depListDocumented) {
                JSONObject licenceDocumented = (JSONObject) objDocumented;
                if (licenceReporter.get("moduleName").toString().equals(licenceDocumented.get("moduleName").toString())) {
                    foundEntry = true;
                    if (!licenceReporter.get("moduleVersion").toString().equals(licenceDocumented.get("moduleVersion").toString())) {
                        newVersion = true;
                    }
                }
            }
            if (!foundEntry) {
                retVal = true;
                notDocumented.add(licenceReporter.get("moduleName").toString());
            }
            if (newVersion) {
                newVersionList.add(licenceReporter.get("moduleName").toString());
            }
        }
        if (!notDocumented.isEmpty()) {
            notDocumented.forEach(ent -> LOGGER.warning("NEW DEPENDENCY, please add it to 'allLicensesBackend.json': " + ent));
        }
        if (!newVersionList.isEmpty()) {
            newVersionList.forEach(ent -> LOGGER.warning("NEW VERSION OF DEPENDENCY, please edit 'allLicensesBackend.json': " + ent));
        }
        return retVal;
    }

    public static boolean doNewJavaScriptDepsExist(JSONObject licenseReporter, JSONObject documentedLicenses) {
        boolean retVal = false;
        HashSet<String> notDocumented = new HashSet<>();

        for (Object objReporter : licenseReporter.keySet()) {
            String keyReporter = (String) objReporter;
            boolean foundEntry = false;
            for (Object objDocumented : documentedLicenses.keySet()) {
                String keyDocumented = (String) objDocumented;
                if (keyReporter.equals(keyDocumented)) {
                    foundEntry = true;
                    break;
                }
            }
            if (!foundEntry) {
                retVal = true;
                notDocumented.add(keyReporter);
            }
        }
        if (!notDocumented.isEmpty()) {
            notDocumented.forEach(ent -> LOGGER.warning("NEW JAVASCRIPT DEPENDENCY, please add it to 'allLicensesJavaScript.json': " + ent));
        }

        return retVal;
    }

    public static JSONObject openJSON(String path) {
        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(path)) {
            Object obj = jsonParser.parse(reader);
            jsonObject = (JSONObject) obj;

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static void createOpening() throws IOException {
        FileWriter myWriter = new FileWriter(adocFileName);
        myWriter.append("= OSDF (Open Source Declaration File) for the nuSIM Loader Application +\n\n");
        myWriter.append("== Overview of open source software (OSS) (components / dependencies):\n\n");

        myWriter.close();
    }

    private static void createEnding() throws IOException {
        FileWriter myWriter = new FileWriter(adocFileName, true);
        myWriter.append("== Appendix \n\n");
        JSONParser jsonParser = new JSONParser();
        // list all licenses which only need to be written down once (Apache2, LGPL, GPL, AGPL, etc.)
        try (FileReader reader = new FileReader("src/main/resources/generalLicenses.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray licList = (JSONArray) jsonObject.get("generalLicenses");

            for (Object o : licList) {
                JSONObject licence = (JSONObject) o;
                myWriter.append("[[" + licence.get("anchor").toString() + "]]\n");

                myWriter.append("=== " + licence.get("name").toString() + " +\n");
                myWriter.append("\n....\n" + licence.get("licenseText").toString() + "\n....\n");
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        myWriter.close();
    }

    private static void writeGradleDepends() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        try {
            FileWriter myWriter = new FileWriter(adocFileName, true);
            myWriter.append("* Components (Java Application)\n\n");
            JSONObject jsonObject = openJSON("src/main/resources/allLicensesBackend.json");
            JSONArray depList = (JSONArray) jsonObject.get("dependencies");

            for (Object o : depList) {
                JSONObject licence = (JSONObject) o;
                myWriter.append("** <<" + licence.get("moduleName").toString() + ">> ");
                myWriter.append(" - " + licence.get("spdxID").toString() + " +\n");
            }

            myWriter.append("* Components (JavaScript Application)\n\n");
            JSONObject jsLicenses = openJSON(ALLLICENSESJAVASCRIPT);

            for (Object o : jsLicenses.keySet()) {
                String key = (String) o;
                JSONObject module = (JSONObject) jsLicenses.get(key);
                myWriter.append("** <<" + key.replace('@', '_').replace('/', '-') + ">>");
                myWriter.append(" - " + module.get("spdxID").toString() + " +\n");
            }
            myWriter.append(" + \n\n");

            myWriter.append("\n* Appendix\n\n");
            JSONObject generalLicenses = openJSON("src/main/resources/generalLicenses.json");
            JSONArray genLicList = (JSONArray) generalLicenses.get("generalLicenses");

            for (Object o : genLicList) {
                JSONObject licence = (JSONObject) o;
                myWriter.append("** <<" + licence.get("anchor").toString() + ">> \n");
            }

            myWriter.append("\n== written offer: +\n\n");
            myWriter.append("This project contains sub-components licensed under copyleft licenses and we are obliged to hand over the source code. For doing so, we select the method of granting you a 'Written Offer':\n" +
                    "\n....\n" +
                    "Deutsche Telekom\n" +
                    "\n" +
                    "hereby offers, valid for at least three years, to give you or any third party, for a charge no more than the cost of physically performing source distribution, on a medium customarily used for software interchange, a complete machine-readable copy of the corresponding source code of the software given to you under any copyleft license (particularly - but not exclusively - under the GNU General Public License (GPL), the GNU Lesser General Public License (LGPL), or the GNU Affero General Public License (AGPL) , the Eclipse Public License (EPL), the Mozilla Public License (MPL) ). To receive such source code please contact us as follows:\n" +
                    "\nDeutsche Telekom Technik GmbH" +
                    "\n" +
                    "T-CSS Smart Card Engineering" +
                    "\n" +
                    "Dr. Klaus Krebs" +
                    "\n" +
                    "Landgrabenweg 151" +
                    "\n" +
                    "53227 Bonn" +
                    "\n" +
                    "Germany" +
                    "\n\n" +
                    "nusim.info@telekom.de" + "\n....\n");
            myWriter.append("\n== Overview of open source software (OSS) (components / dependencies): +\n\n");
            for (Object o : depList) {
                JSONObject licence = (JSONObject) o;
                myWriter.append("=== Component: [[" + licence.get("moduleName").toString() + "]]" + licence.get("moduleName").toString() + " +\n");
                myWriter.append("* Component name: " + licence.get("moduleName").toString() + " +\n");
                myWriter.append("* Component version: " + licence.get("moduleVersion").toString() + " +\n");
                myWriter.append("* License type: " + licence.get("spdxID").toString() + " +\n");
                if (licence.get("moduleUrl") != null) {
                    myWriter.append("* Public homepage: " + licence.get("moduleUrl").toString() + " +\n\n");
                }
                myWriter.append("* Public repository: " + licence.get("repository").toString() + " +\n\n");

                switch (licence.get("moduleLicense").toString()) {
                    case "Apache License, Version 2.0":
                        if (licence.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + licence.get("anchor").toString() + " +\n");
                        }
                        if (licence.get("noticeFile") != null) {
                            myWriter.append("\nNOTICE file in the repository: \n....\n" + licence.get("noticeFile").toString() + "\n....\n");
                        } else {
                            myWriter.append("Project does not use a file named NOTICE +\n\n");
                        }
                        break;
                    case "Bouncy Castle Licence":
                    case "BSD 3-Clause License":
                    case "New BSD License":
                    case "The 2-Clause BSD License":
                    case "MIT License":
                        if (licence.get("noticeFile") != null) {
                            myWriter.append("\nLicense Text: \n....\n" + licence.get("noticeFile").toString() + "\n....\n");
                        }
                        break;
                    case "GNU GENERAL PUBLIC LICENSE, Version 2 + Classpath Exception":
                    case "GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1":
                        if (licence.get("copyrightOwner") != null) {
                            myWriter.append("\nCopyright owners: \n" + licence.get("copyrightOwner").toString() + " +\n");
                        }
                        if (licence.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + licence.get("anchor").toString() + " +\n");
                        }
                        if (licence.get("warranty") != null) {
                            myWriter.append("\nDisclaimer of warranty: \n....\n" + licence.get("warranty").toString() + "\n....\n");
                        }
                        break;
                    case "Mozilla Public License Version 1.1":
                        if (licence.get("copyrightOwner") != null) {
                            myWriter.append("\nCopyright owners: \n" + licence.get("copyrightOwner").toString() + " +\n");
                        }
                        if (licence.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + licence.get("anchor").toString() + " +\n");
                        }
                        break;
                    case "GNU LESSER GENERAL PUBLIC LICENSE, Version 3, Apache License, Version 2.0":
                    case "Eclipse Public License - v 1.0, Apache License, Version 2.0":
                        myWriter.append("\nWe are redistributing this component under the terms of the Apache License, Version 2.0 license. +\n");
                        myWriter.append("\nLicense Text: " + licence.get("anchor").toString() + " +\n");
                        if (licence.get("noticeFile") != null) {
                            myWriter.append("\nNOTICE file in the repository: \n....\n" + licence.get("noticeFile").toString() + "\n....\n");
                        } else {
                            myWriter.append("Project does not use a file named NOTICE +\n\n");
                        }
                        break;
                    case "Common Public License - v 1.0":
                        myWriter.append("\nNo warranty disclaimer: \n....\n" + licence.get("warranty").toString() + "\n....\n");
                        myWriter.append("\nLicense Text: " + licence.get("anchor").toString() + " +\n");
                        break;

                    case "BSD 3-Clause License, Eclipse Public License - v 1.0, GNU GENERAL PUBLIC LICENSE, Version 2 + Classpath Exception":
                        //GPL2 with Classpath
                        if (licence.get("copyrightOwner") != null) {
                            myWriter.append("\nCopyright owners: \n" + licence.get("copyrightOwner").toString() + " +\n");
                        }
                        //BSD 3-Clause
                        if (licence.get("noticeFile") != null) {
                            myWriter.append("\nLicense Text: \n....\n" + licence.get("noticeFile").toString() + "\n....\n");
                        }
                        //all links to the complete license texts
                        myWriter.append("\nLicense Text: " + licence.get("anchor1").toString() + " +\n");
                        myWriter.append("\nLicense Text: " + licence.get("anchor2").toString() + " +\n");
                        //EPLv1
                        myWriter.append("\nDisclaimer of warranty GPL 2.0: \n....\n" + licence.get("warrantyGPL").toString() + "\n....\n");
                        myWriter.append("\nDisclaimer of warranty EPL 1.0: \n....\n" + licence.get("warrantyEPL").toString() + "\n....\n");

                        break;
                    default:
                        if (licence.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + licence.get("anchor").toString() + " +\n");
                        }
                }

                myWriter.append("\n\n");
            }
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeGUIDepends() {
        try {
            FileWriter myWriter = new FileWriter(adocFileName, true);
            JSONObject jsonObject = openJSON(ALLLICENSESJAVASCRIPT);
            for (Object o : jsonObject.keySet()) {
                String key = (String) o;
                JSONObject module = (JSONObject) jsonObject.get(key);
                myWriter.append("=== Component: [[" + key.replace('@', '_').replace('/', '-') + "]]" + key + " +\n");
                myWriter.append("* Component name: " + key + " +\n");
                myWriter.append("* Component version: " + module.get("version").toString() + " +\n");
                myWriter.append("* License type: " + module.get("spdxID").toString() + " +\n");
                if (module.get("url") != null) {
                    myWriter.append("* Public homepage: " + module.get("url").toString() + " +\n\n");
                }
                myWriter.append("* Public repository: " + module.get("repository").toString() + " +\n\n");

                switch (module.get("licenses").toString()) {
                    case "Apache-2.0":
                        if (module.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + module.get("anchor").toString() + " +\n");
                        }
                        if (module.get("noticeFile") != null) {
                            myWriter.append("\nNOTICE file in the repository: \n....\n" + module.get("noticeFile").toString() + "\n....\n");
                        } else {
                            myWriter.append("Project does not use a file named NOTICE +\n\n");
                        }
                        break;
                    case "BSD-3-Clause":
                    case "BSD-2-Clause":
                    case "MIT":
                    case "ISC":
                        if (module.get("licenseText") != null) {
                            myWriter.append("\nLicense Text: \n....\n" + module.get("licenseText").toString() + "\n....\n");
                        }
                        break;
                    case "CC-BY-3.0":
                        if (module.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + module.get("anchor").toString() + " +\n");
                        }
                        myWriter.append("\nThis licensed material has not been modified. +\n\n");
                        break;
                    case "(CC-BY-4.0 AND MIT)":
                        if (module.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + module.get("anchor").toString() + " +\n");
                        }
                        myWriter.append("\nLicense Text: \n....\n" + module.get("licenseText").toString() + "\n....\n");
                        break;
                    case "(MIT AND CC-BY-3.0)":
                        if (module.get("name").toString().equals("spdx-ranges")) {
                            myWriter.append("\nThe Linux Foundation and its contributors license the SPDX standard under the terms of the Creative Commons Attribution License 3.0 Unported (SPDX: \"CC-BY-3.0\"). \"SPDX\" is a United States federally registered trademark of the Linux Foundation. The authors of this package license their work under the terms of the MIT License. +\n");
                        }
                        myWriter.append("\nLicense Text: \n....\n" + module.get("licenseText").toString() + "\n....\n");
                        if (module.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + module.get("anchor").toString() + " +\n");
                        }
                        myWriter.append("\nThis licensed material has not been modified. +\n\n");
                        break;
                    case "CC-BY-4.0, SIL OFL 1.1, MIT":
                        myWriter.append("\nModified notice: " + module.get("materialModified").toString() + " +\n");
                        myWriter.append("\nLicense Text: " + module.get("anchor").toString() + " +\n");
                        myWriter.append("\nLicense Text: " + module.get("anchor2").toString() + " +\n");
                        myWriter.append("\nLicense Text: \n....\n" + module.get("licenseText").toString() + "\n....\n");
                        break;
                    default:
                        if (module.get("anchor") != null) {
                            myWriter.append("\nLicense Text: " + module.get("anchor").toString() + " +\n\n");
                        }
                }
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
