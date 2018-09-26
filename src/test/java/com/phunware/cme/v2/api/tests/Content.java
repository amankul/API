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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.phunware.cme.v2.api.tests.Structure.structureMap;
import static com.phunware.cme.v2.api.tests.Structure.containerId;
import static com.phunware.cme.v2.api.tests.Schema.schemaMap;
import static com.phunware.cme.v2.api.tests.Schema.containerName;
import static io.restassured.RestAssured.given;


/**
 * Created by VinayKarumuri on 8/10/18.
 */
public class Content {

    private static Logger log;
    private static String serviceEndPoint = null;
    private static String jwt = null;
    private static String postContentRequestURL;
    private static String deleteContainerRequestURL;
    private static String deleteSchemaRequestURL;
    private String contentId;

    protected static String dateTime = null;
    protected static HashMap<String, String> contentMap = new HashMap<String, String>();


    @BeforeClass
    @Parameters({"env", "orgId"})
    private void setEnv(String env, int orgId) throws IOException {
        log = Logger.getLogger(Structure.class);
        jwt = JWTUtils.getJWTForAdmin(env, orgId);


        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check testng xml.");
            Assert.fail("Environment is not set properly. Please check testng xml.");
        }
        postContentRequestURL = serviceEndPoint + CmeV2_API_Constants.CONTENT_END_POINT;
        deleteContainerRequestURL = serviceEndPoint + CmeV2_API_Constants.CONTAINERS_END_POINT + "/";
        deleteSchemaRequestURL = serviceEndPoint + CmeV2_API_Constants.SCHEMAS_END_POINT + "/";

        log.info("Content URL: " + postContentRequestURL);
        dateTime = HelperMethods.getDateAsString();
    }


    /**
     * Creating Content for container
     **/
    @Test(dataProvider = "usesParameter", priority = 0)
    public void verify_Post_Content(String path, Integer structureId, String parentId) throws IOException {


        log.info("File Path for Content JSON: " + path);
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
                        .header("Authorization", jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(postContentRequestURL)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());

        response.then().statusCode(200);
        contentId = response.getBody().jsonPath().get("id");
        int index = path.indexOf("Content");

        //stripping prefix and postfix from source file name and pushing into hashMap
        String name = path.substring(index).replaceAll(".json", "").replaceAll("Content", "");
        contentMap.put(name, contentId);
        log.info("Content Map : " + contentMap);

    }


    @Test(priority = 0)
    public void verify_Get_Content() {

    }

    /** Deleting Container. This will delete content and structure contained it." **/
    @Test(priority = 1)
    public void deleteContainer() {
        // logging Request Details
        log.info("-------------------------------------------------------------------------------------");
        log.info("Deleting Container Test: ");
        log.info("REQUEST-URL:DELETE-" + deleteContainerRequestURL + containerId);

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", jwt)
                        .delete(deleteContainerRequestURL + containerId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        log.info("-------------------------------------------------------------------------------------");

    }

    /**
     * Schema can be deleted after the parent Container has been deleted.
     **/
    @Test(priority = 2)
    public void deleteSchema() {
        // logging Request Details
        log.info("Schema Map" + schemaMap);
        log.info("Deleting the Schema");
        log.info("REQUEST-URL:DELETE-" + deleteSchemaRequestURL);

        for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
            log.info("Deleting " + deleteSchemaRequestURL + entry.getValue().toString());
            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", jwt)
                            .delete(deleteSchemaRequestURL + entry.getValue().toString())
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();
            log.info("-------------------------------------------------------------------------------------");
        }
    }


    @DataProvider(name = "usesParameter")
    public Object[][] provideTestParam(ITestContext context) {

        switch (Schema.containerName) {
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
                        {context.getCurrentXmlTest().getParameter("postContentAAA"), structureMap.get("Item"), "Items"},
                        {context.getCurrentXmlTest().getParameter("postContentAthleta"), structureMap.get("Item"), "Items"},
                        {context.getCurrentXmlTest().getParameter("postContentBSpot"), structureMap.get("Item"), "Items"},
                        {context.getCurrentXmlTest().getParameter("postContentStarbucks"), structureMap.get("Item"), "Items"},
                        {context.getCurrentXmlTest().getParameter("postContentSubway"), structureMap.get("Item"), "Items"}
                };
            default:
                return null;
        }

    }
}
