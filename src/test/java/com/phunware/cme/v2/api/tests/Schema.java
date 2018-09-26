package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import com.phunware.utility.JWTUtils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

/**
 * Created by VinayKarumuri on 7/13/18.
 */
public class Schema {

    private static String serviceEndPoint = null;
    private static String jwt = null;
    private int orgId;
    private static String postSchemaRequestURL = null;
    public static String containerName;
    private String name;
    private static Logger log;
    protected static HashMap<String, String> schemaMap = new HashMap<String, String>();


    @BeforeSuite
    @Parameters("containerName")
    private void setUpSuite(String containerName) {
        this.containerName=containerName;
    }


    @BeforeClass
    @Parameters({"env", "orgId" })
    private void setEnv(String env, int orgId) {

        log = Logger.getLogger(Schema.class);
        this.orgId = orgId;

        jwt = JWTUtils.getJWTForAdmin(env, orgId);

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
        postSchemaRequestURL = serviceEndPoint + CmeV2_API_Constants.SCHEMAS_END_POINT;
        log.info("POST Schema URL: " + postSchemaRequestURL);
    }


    /**
     * Create schema.  Source files are from resources directory.
     **/
    @Test(dataProvider = "usesParameter")
    public void verify_Post_Schema(String path) throws IOException {

        log.info("FILE PATH for SCHEMA JSON: " + path);
        String datetime = HelperMethods.getDateAsString();
        String requestBody = FileUtils.getJsonTextFromFile(path);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");


        // DignityHealth json's have a VSC prefix in name and so making exception here. Should fix this in future.
        if ("DignityHealth".equals(containerName)) {
            int index = path.indexOf("Vsc");
            name = path.substring(index).replaceAll(".json", "");
        } else {
            name = new File(path).getName();
            name = name.substring(10, name.lastIndexOf('.'));
        }

        log.info("orgId: " + orgId);
        requestBodyData.put("name", name + datetime);
        requestBodyData.put("orgId", orgId);

        // logging Request Details
        log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
        log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(postSchemaRequestURL)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);

            // Get schema ID and put it into hash map
            schemaMap.put(name, response.getBody().jsonPath().get("id"));
    }


    /**
     * Data provider for schema.
     **/
    @DataProvider(name = "usesParameter")
    public Object[][] provideTestParam(ITestContext context) {

        switch (containerName) {
            case "DignityHealth":
                return new Object[][]{
                        {context.getCurrentXmlTest().getParameter("postSchemaVscAdvertisingSetting")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscApp")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscAppVersion")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscBeacon")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscBuilding")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscDatabaseVersion")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscFloor")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscGeoSettings")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscMapSettings")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscPlatform")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscPreCachingConfiguration")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscProximityAlert")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscSettings")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscVenue")},
                        {context.getCurrentXmlTest().getParameter("postSchemaVscCampus")}
                };
            case "Directory":
                return new Object[][]{
                        {context.getCurrentXmlTest().getParameter("postSchemaDirectory")}
                };

            default:
                return null;
        }

    }


}
