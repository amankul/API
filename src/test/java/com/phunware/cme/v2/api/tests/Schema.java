package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
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

    public static String SERVICE_END_POINT = null;
    public static String JWT = null;
    public static String ORGID = null;
    public static String postSchemaRequestURL = null;
    private String containerName;
    private String name;
    static Logger log;
    public static HashMap<String, String> schemaMap = new HashMap<String, String>();

    @BeforeSuite
    @Parameters({"env", "jwt", "orgId", "containerName"})
    private void setEnv(String env, String jwt, String orgId, String containerName) {
        this.containerName = containerName;
        System.out.println(containerName);
        JWT = jwt;
        ORGID = orgId;
        if ("PROD".equalsIgnoreCase(env)) {
            SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
        postSchemaRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.SCHEMAS_END_POINT;
        System.out.println("schemaUrl: " + postSchemaRequestURL);
    }

    @BeforeClass
    public void preTestSteps() {

        log = Logger.getLogger(Schema.class);
    }


    /**
     * Method to post schema.  Source files are from resources directory.
     **/
    @Test(dataProvider = "usesParameter")
    public void verify_Post_Schema(String path) throws IOException {

        log.info("FILE PATH for SCHEMA JSON: " + path);
        String datetime = HelperMethods.getDateAsString();
        String requestBody = FileUtils.getJsonTextFromFile(path);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

        // DignityHealth json's have a VSC prefix in name and so making exception here.
        if (containerName.equals("DignityHealth")) {
            int index = path.indexOf("Vsc");
            name = path.substring(index).replaceAll(".json", "");
        } else {
            name = new File(path).getName();
            name = name.substring(10, name.lastIndexOf('.'));
            System.out.println("file name: " + name);
        }

        requestBodyData.put("name", name + datetime);
        requestBodyData.put("orgId", ORGID);

        // logging Request Details
        log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
        log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", JWT)
                        .body(requestBodyJSONObject.toString())
                        .post(postSchemaRequestURL)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());

        response.then().statusCode(200);
        // Get schema ID and put it into Hashmap
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
                System.out.println("else if block of data provider");
                return new Object[][]{
                        {context.getCurrentXmlTest().getParameter("postSchemaDirectory")}
                };

            default:
                System.out.println("in else block for data provider");
                return null;
        }

    }

    @AfterClass
    public void tearDown() {

        System.out.println("Schema Map: " + schemaMap);
    }
}
