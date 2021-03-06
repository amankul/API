package com.org.cme.v2.api.tests;

import com.org.cmev2_api.constants.CmeV2_API_Constants;
import com.org.utility.FileUtils;
import com.org.utility.HelperMethods;
import com.org.utility.JWTUtils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.org.cme.v2.api.tests.Schema.containerName;
import static com.org.cme.v2.api.tests.Schema.schemaMap;
import static com.org.cme.v2.api.tests.Structure.containerId;
import static com.org.cme.v2.api.tests.Structure.structureMap;
import static io.restassured.RestAssured.given;


public class Content {

    private static Logger log;
    private static String serviceEndPoint = null;
    private static String jwt = null;
    private static String contentRequestUrl;
    private static String deleteContainerRequestUrl;
    private static String deleteSchemaRequestUrl;
    private String contentId;
    private String randomContentId;
    private int spacesToIndentEachLevel = 2;
    private int structureId = 0;
    private int orgId;

    protected static String dateTime = null;
    protected static HashMap<String, String> contentMap = new HashMap<>();


    @BeforeClass
    @Parameters({"env", "orgId"})
    private void setEnv(String env, int orgId) throws IOException {
        this.orgId = orgId;
        log = Logger.getLogger(Structure.class);


        jwt = JWTUtils.getJWTForAdmin(env, orgId);


        if (env.equalsIgnoreCase("PROD")) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
            if (orgId != 96) {
                log.info("orgID is not pointing to QA TEST in PROD.");
                System.exit(0);
            }
        } else if (env.equalsIgnoreCase("STAGE")) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check testng xml.");
            Assert.fail("Environment is not set properly. Please check testng xml.");
        }
        contentRequestUrl = serviceEndPoint + CmeV2_API_Constants.CONTENT_END_POINT + "/";
        deleteContainerRequestUrl = serviceEndPoint + CmeV2_API_Constants.CONTAINERS_END_POINT + "/";
        deleteSchemaRequestUrl = serviceEndPoint + CmeV2_API_Constants.SCHEMAS_END_POINT + "/";


        log.info("Content URL: " + contentRequestUrl);
        dateTime = HelperMethods.getDateAsString();
    }


    /**
     * Creating Content for container
     **/
    @Test(dataProvider = "usesParameter", priority = 0)
    public void verify_Post_Content(String path, Integer structureId, String parentId) throws IOException {

        // verify data provider is not sending NULL values. Path should be enough to test this scenario.
        Assert.assertNotNull(path);
        log.info("File Path for Content JSON: " + path);
        String requestBody = FileUtils.getJsonTextFromFile(path);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        requestBodyJSONObject.put("containerId", containerId);
        requestBodyJSONObject.put("structureId", structureId);
        requestBodyJSONObject.put("parentId", contentMap.get(parentId));
        

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", jwt)
                        .body(requestBodyJSONObject.toString())
                        .log().all().request()
                        .post(contentRequestUrl)
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
        log.info("Content Name : " + name + " Content ID: " + contentId);

    }


    /**
     * Verify GET CONTENT by Content Id
     * *
     * *
     **/
    @Test(priority = 1)
    public void verify_Get_Content_by_contentId() {

        if (containerName.equals("DignityHealth")) {

            log.info("containerName: " + containerName);
            log.info("contentMap: " + contentMap);


            Response response =
                given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", jwt)
                    .log().all().request()
                    .get(contentRequestUrl + contentMap.get("Platform"))
                    .then()
                    .extract()
                    .response();
            log.info("Get Content Response value: " + response.asString());

            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertNotNull(response.body(), "Response Body is null");
        }
    }




    /**
     * Verify GET Content with below fields. This option is ONLY for structure type Object.
     * Structure ID
     * Parent ID
     * Limit
     * Offset
     **/

    @Test(priority = 2)
    public void verify_Get_Content_by_contentId_and_field() {

        //This testcase is dependent on Data, hence field values are hard coded.

        if (containerName.equals("DignityHealth")) {

            log.info("Get Content: By Container ID:  " + contentRequestUrl);
            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", jwt)
                            .log().all().request()
                            .get(contentRequestUrl + contentMap.get("Platform") +"/" +"Settings")
                            .then()
                            .extract()
                            .response();
            log.info("Response value: " + response.asString());

            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertNotNull(response.body(),"Response Body is null");
        }
    }



    /**
     * Verify GET CONTENT by Container ID. This option is when the structure is of type Object.
     * Structure ID
     * Parent ID
     * Org ID
     **/

    @Test(priority = 3)
    public void verify_Get_Content_by_containerId_for_Structure_Object() {

        if (containerName.equals("DignityHealth")) {

            JSONObject jo = new JSONObject();
            jo.put("orgId", orgId);
            jo.put("containerId", containerId);
            jo.put("structureId", structureMap.get("GeoSettings")  //get structure id of type Object.
            );

            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", jwt)
                            .queryParam(jo.toString())
                            .log().all().request()
                            .get(contentRequestUrl)
                            .then()
                            .extract()
                            .response();
            log.info("Get Content Response value: " + response.asString());

            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertNotNull(response.body(),"Response Body is null");

        }

    }

    /**
     * Verify GET Content. This option is when the structure is of type ARRAY.
     * Structure ID
     * Parent ID
     * Limit
     * Offset
     **/

    @Test(priority = 4)
    public void verify_Get_Content_by_containerId_for_Structure_Array() {

        // Get content for "Platform" as this has Structure of type Array. This needs to be hard-coded here. Hence this testcase has be run only for DignityHealth.

        if (containerName.equals("DignityHealth")) {

            log.info("structureID: " + structureMap.get("Venues"));

            JSONObject jo = new JSONObject();
            jo.put("orgId", orgId);
            jo.put("containerId", containerId);
            jo.put("structureId", structureMap.get("Venues"));
            jo.put("limit", 2);
            jo.put("offset", 1);


            log.info("Get Content: By Container ID:  " + contentRequestUrl);
            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", jwt)
                            .queryParam(jo.toString())
                            .log().all().request()
                            .get(contentRequestUrl)
                            .then()
                            .extract()
                            .response();
            log.info("Response value: " + response.asString());

            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertNotNull(response.body(),"Response Body is null");
        }
    }


    /**
     * Verify GET Content with below fields. This option is when the structure is of type Object.
     * Structure ID
     * Parent ID
     * Limit
     * Offset
     **/

    @Test(priority = 5)
    public void verify_Get_Content_by_containerId_and_field() {

        //This testcase is dependent on Data, hence field values are hard coded.

        if (containerName.equals("DignityHealth")) {

            log.info("structureID: " + structureMap.get("Venues"));

            JSONObject jo = new JSONObject();
            jo.put("orgId", orgId);
            jo.put("containerId", containerId);
            jo.put("structureId", structureMap.get("Platform"));
            jo.put("limit", 2);
            jo.put("offset", 1);


            log.info("Get Content: By Container ID:  " + contentRequestUrl);
            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", jwt)
                            .queryParam(jo.toString())
                            .log().all().request()
                            .get(contentRequestUrl + "Settings")
                            .then()
                            .extract()
                            .response();
            log.info("Response value: " + response.asString());

            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertNotNull(response.body(),"Response Body is null");
        }
    }





    /**
     * Deleting Container. This will delete content and structure contained it."
     **/
    @Test(priority = 8)
    public void delete_Container() {

        log.info("REQUEST: DELETE-" + deleteContainerRequestUrl + containerId);

        // Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", jwt)
                        .delete(deleteContainerRequestUrl + containerId)
                        .then()
                        .extract()
                        .response();

        log.info("Response value: " + response.asString());

        Assert.assertEquals(response.statusCode(), 200);
    }

    /**
     * Schema can be deleted after the parent Container has been deleted.
     **/
    @Test(priority = 9)
    public void delete_Schema() {
        // logging Request Details
        log.info("Schema Map" + schemaMap);
        log.info("Deleting the Schema");
        log.info("REQUEST: DELETE-" + deleteSchemaRequestUrl);

        for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
            log.info("Deleting " + deleteSchemaRequestUrl + entry.getValue().toString());
            Response response =
                    given()
                            .header("Content-Type", "application/json")
                            .header("Authorization", jwt)
                            .delete(deleteSchemaRequestUrl + entry.getValue().toString())
                            .then()
                            .extract()
                            .response();

            log.info("Response value: " + response.asString());

            Assert.assertEquals(response.statusCode(), 200);
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
                        {context.getCurrentXmlTest().getParameter("postContentVenueArizona"), structureMap.get("Venue"), null},
                        {context.getCurrentXmlTest().getParameter("postContentVenueCalifornia"), structureMap.get("Venue"), null},
                        {context.getCurrentXmlTest().getParameter("postContentVenueHawaii"), structureMap.get("Venue"), null},
                        {context.getCurrentXmlTest().getParameter("postContentVenueColorado"), structureMap.get("Venue"), null},
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


    @AfterClass
    public void teardown() {
        contentMap.clear();
        structureMap.clear();
        schemaMap.clear();
    }
}
