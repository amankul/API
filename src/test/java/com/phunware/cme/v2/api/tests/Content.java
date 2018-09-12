package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.*;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.phunware.cme.v2.api.tests.Structure.structureMap;
import static com.phunware.cme.v2.api.tests.Structure.containerId;
import static com.phunware.cme.v2.api.tests.Schema.schemaMap;
import static io.restassured.RestAssured.given;


/**
 * Created by VinayKarumuri on 8/10/18.
 */
public class Content {

    static Logger log;
    public static String SERVICE_END_POINT = null;
    public static String JWT = null;
    public static String postContentRequestURL;
    public static String deleteContainerRequestURL;
    public static String deleteSchemaRequestURL;
    private String containerName;

    public static String dateTime = null;
    public static HashMap<String, String> contentMap = new HashMap<String, String>();


    @BeforeClass
    @Parameters({"env", "jwt", "orgId", "containerName"})
    private void setEnv(String env, String jwt, String orgId, String containerName) throws IOException {
        this.containerName = containerName;
        log = Logger.getLogger(Structure.class);
        JWT = jwt;
        if ("PROD".equalsIgnoreCase(env)) {
            SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
        postContentRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.CONTENT_END_POINT;
        deleteContainerRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.CONTAINERS_END_POINT + "/";
        deleteSchemaRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.SCHEMAS_END_POINT + "/";

        log.info("Content URL: " + postContentRequestURL);
        dateTime = HelperMethods.getDateAsString();
    }

    /**
     * Creating Content for Dignity Health
     **/
    @Test(dataProvider = "usesParameter", priority = 0)
    public void verify_Post_Content(String path, Integer structureId, String parentId) throws IOException {

        /* Request Details */
        String datetime = HelperMethods.getDateAsString();
        String requestBody = FileUtils.getJsonTextFromFile(path);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        requestBodyJSONObject.put("containerId", containerId);
        requestBodyJSONObject.put("structureId", structureId);
        requestBodyJSONObject.put("parentId", contentMap.get(parentId));


        // logging Request Details
        log.info("REQUEST-URL:POST-" + postContentRequestURL);
        log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", JWT)
                        .body(requestBodyJSONObject.toString())
                        .post(postContentRequestURL)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());

        response.then().statusCode(200);
        String contentId = response.getBody().jsonPath().get("id");

        int index = path.indexOf("Content");
        String name = path.substring(index).replaceAll(".json", "").replaceAll("Content", "");
        contentMap.put(name, contentId);
        log.info("Content Map : " + contentMap);
    }


    @DataProvider(name = "usesParameter")
    public Object[][] provideTestParam(ITestContext context) {

        switch (containerName) {
            case "DignityHealth":
                return new Object[][]{
                        {context.getCurrentXmlTest().getParameter("postContentApplication"), structureMap.get("Application"), null},
                        {context.getCurrentXmlTest().getParameter("postContentPlatform"), structureMap.get("Platform"), "Application"},
                        {context.getCurrentXmlTest().getParameter("postContentAppVersion"), structureMap.get("AppVersion"), "Platform"},
                        {context.getCurrentXmlTest().getParameter("postContentPreCachingConfiguration"), structureMap.get("CachingSettings"), "Platform"},
                        {context.getCurrentXmlTest().getParameter("postContentAdvertisingSetting"), structureMap.get("AdvertisementSettings"), "Platform"},
                        {context.getCurrentXmlTest().getParameter("postContentSettings"), structureMap.get("Settings"), "Platform"},
                        {context.getCurrentXmlTest().getParameter("postContentDatabaseVersion"), structureMap.get("Platform_Database"), "Platform"},
                        {context.getCurrentXmlTest().getParameter("postContentPlatformVenueTexas"), structureMap.get("Platform_Venue"), "Platform"},
                        {context.getCurrentXmlTest().getParameter("postContentTexasDatabaseVersion"), structureMap.get("Venue_Database"), "VenueTexas"},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexas"), structureMap.get("Venue"), null},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampus"), structureMap.get("Campus"), "VenueTexas"},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampusBuilding"), structureMap.get("Building"), "VenueTexasCampus"},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampusBuildingFloor"), structureMap.get("Floor"), "VenueTexasCampusBuilding"},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampusBuildingMapSettings"), structureMap.get("MapSettings"), "VenueTexasCampusBuilding"},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampusBuildingGeoSettings"), structureMap.get("GeoSettings"), "VenueTexasCampusBuilding"},

                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampusBuildingFloorBeacon"), structureMap.get("Beacon"), "VenueTexasCampusBuildingFloor"},
                        {context.getCurrentXmlTest().getParameter("postContentVenueTexasCampusBuildingFloorBeaconAlert"), structureMap.get("Alert"), "VenueTexasCampusBuildingFloor"},
                };
            case "Directory":
                return new Object[][]{
                        {context.getCurrentXmlTest().getParameter("")}
                };
            default:
                return null;
        }

    }


    @Test(priority = 1)
    public void deleteContainer() {
        // logging Request Details
        log.info("-------------------------------------------------------------------------------------");
        log.info("Deleting the Container - it deletes the content and structure in it");
        log.info("REQUEST-URL:DELETE-" + deleteContainerRequestURL + containerId);

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", JWT)
                        .delete(deleteContainerRequestURL + containerId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        log.info("-------------------------------------------------------------------------------------");
    }

    @Test(priority = 2)
    public void deleteSchema() {
        // logging Request Details
        log.info("Schema Map" + schemaMap);
        log.info("Deleting the Schema");
        log.info("REQUEST-URL:DELETE-" + deleteSchemaRequestURL);

        for (Object o : schemaMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            log.info("Deleting " + deleteSchemaRequestURL + entry.getValue().toString());
            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", JWT)
                            .delete(deleteSchemaRequestURL + entry.getValue().toString())
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();
            log.info("-------------------------------------------------------------------------------------");
        }
    }


}
