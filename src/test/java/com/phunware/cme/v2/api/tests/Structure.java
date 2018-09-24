package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static com.phunware.cme.v2.api.tests.Schema.schemaMap;
import static io.restassured.RestAssured.given;


/**
 * Created by VinayKarumuri on 7/20/18.
 */

public class Structure {

    static Logger log;
    public static String SERVICE_END_POINT = null;
    public static String JWT = null;
    public static String postStructureRequestURL = null;
    public static String postContainerRequestURL = null;
    public static String dateTime = null;
    public static String structureRequestBody = null;
    public static String containerId = null;
    public static final String structureTypeOBJECT = "object";
    public static final String structureTypeARRAY = "array";
    public static String applicationsStructureName = "Applications";
    public static String applicationStructureName = "Application";
    public static String platformsStructureName = "Platforms";
    public static String platformStructureName = "Platform";
    public static String appVersionStructureName = "AppVersion";
    public static String cachingSettingsStructureName = "CachingSettings";
    public static String advertisementSettingsStructureName = "AdvertisementSettings";
    public static String settingsStructureName = "Settings";
    public static String platformVenuesStructureName = "Platform_Venues";
    public static String platformVenueStructureName = "Platform_Venue";
    public static String platformDatabasesStructureName = "Platform_Databases";
    public static String platformDatabaseStructureName = "Platform_Database";
    public static String venueDatabasesStructureName = "Venue_Databases";
    public static String venueDatabaseStructureName = "Venue_Database";
    private static String items_StructureName = "Items";
    private static String item_StructureName = "Item";
    public static String venuesStructureName = "Venues";
    public static String venueStructureName = "Venue";
    public static String campusesStructureName = "Campuses";
    public static String campusStructureName = "Campus";
    public static String buildingsStructureName = "Buildings";
    public static String buildingStructureName = "Building";
    public static String floorsStructureName = "Floors";
    public static String floorStructureName = "Floor";
    public static String mapSettingsStructureName = "MapSettings";
    public static String geoSettingsStructureName = "GeoSettings";
    public static String beaconsStructureName = "Beacons";
    public static String beaconStructureName = "Beacon";
    public static String alertsStructureName = "Alerts";
    public static String alertStructureName = "Alert";


    private String containerName;
    public static HashMap<String, Integer> structureMap = new HashMap<String, Integer>();


    @BeforeClass
    @Parameters({"env", "jwt", "orgId", "postStructureFilePath", "containerName"})
    private void setEnv(String env, String jwt, String orgId, String postStructureFilePath, String containerName) throws IOException {
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
        postStructureRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.STRUCTURE_END_POINT;
        postContainerRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.CONTAINERS_END_POINT;
        log.info("Container Url: " + postContainerRequestURL);
        dateTime = HelperMethods.getDateAsString();
        structureRequestBody = FileUtils.getJsonTextFromFile(postStructureFilePath);
    }


    /**
     * Creating container
     **/
    @Parameters("postContainerFilePath")
    @Test(priority = 1)
    public void verify_Post_Container(String postContainerFilePath) throws IOException {

        //Request Details
        log.info(postContainerFilePath);
        String requestBody = FileUtils.getJsonTextFromFile(postContainerFilePath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
        requestBodyData.put("name", containerName + dateTime);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + postContainerRequestURL);
        log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", JWT)
                        .body(requestBodyJSONObject.toString())
                        .post(postContainerRequestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

        //JSON response Pay load validations
        containerId = response.getBody().jsonPath().get("id");
        log.info("ContainerId = " + containerId);
    }


    /**
     * Creating Structures for the above container
     **/
    @Test(dataProvider = "dignityStructure", priority = 2)
    public void verify_Post_Structure(String containerId, String name, String type, String field, String schemaId, String parent) throws IOException {

        //Request Details
        JSONObject requestBodyJSONObject = new JSONObject(structureRequestBody);
        JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
        requestBodyData.put("containerId", containerId);
        requestBodyData.put("name", name + dateTime);
        requestBodyData.put("type", type);
        requestBodyData.put("field", field);
        requestBodyData.put("schemaId", schemaId);
        log.info("parentId for Structure: " + structureMap.get(parent));
        requestBodyData.put("parentId", structureMap.get(parent));


        log.info("REQUEST-URL: POST- " + postStructureRequestURL);
        log.info("REQUEST-URL: BODY- " + requestBodyJSONObject.toString());

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", JWT)
                        .body(requestBodyJSONObject.toString())
                        .post(postStructureRequestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        log.info("RESPONSE: " + response.asString());

        //JSON response Pay load validations
        int StructureId = response.getBody().jsonPath().get("id");
        log.info("NAME: " + name);
        log.info("structureId = " + StructureId);
        structureMap.put(name, StructureId);
        log.info("STRUCTURE MAP: " + structureMap);
        log.info("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }


    @DataProvider(name = "dignityStructure")
    public Object[][] dignityStructureDataSet() {

        switch (containerName) {
            case "DignityHealth":
                return new Object[][]{
                        {containerId, applicationsStructureName, structureTypeARRAY, applicationsStructureName, "", ""},
                        {containerId, applicationStructureName, structureTypeOBJECT, applicationStructureName, schemaMap.get("VscApp"), applicationsStructureName},
                        {containerId, platformsStructureName, structureTypeARRAY, platformsStructureName, "", applicationStructureName},
                        {containerId, platformStructureName, structureTypeOBJECT, platformStructureName, schemaMap.get("VscPlatform"), platformsStructureName},
                        {containerId, appVersionStructureName, structureTypeOBJECT, appVersionStructureName, schemaMap.get("VscAppVersion"), platformStructureName},
                        {containerId, cachingSettingsStructureName, structureTypeOBJECT, cachingSettingsStructureName, schemaMap.get("VscPreCachingConfiguration"), platformStructureName},
                        {containerId, advertisementSettingsStructureName, structureTypeOBJECT, advertisementSettingsStructureName, schemaMap.get("VscAdvertisingSetting"), platformStructureName},
                        {containerId, settingsStructureName, structureTypeOBJECT, settingsStructureName, schemaMap.get("VscSettings"), platformStructureName},
                        {containerId, platformVenuesStructureName, structureTypeARRAY, platformVenuesStructureName, "", platformStructureName},
                        {containerId, platformVenueStructureName, structureTypeOBJECT, platformVenueStructureName, schemaMap.get("VscVenue"), platformVenuesStructureName},
                        {containerId, platformDatabasesStructureName, structureTypeARRAY, platformDatabasesStructureName, "", platformStructureName},
                        {containerId, platformDatabaseStructureName, structureTypeOBJECT, platformDatabaseStructureName, schemaMap.get("VscDatabaseVersion"), platformDatabasesStructureName},
                        {containerId, venueDatabasesStructureName, structureTypeARRAY, venueDatabasesStructureName, "", platformVenuesStructureName},
                        {containerId, venueDatabaseStructureName, structureTypeOBJECT, venueDatabaseStructureName, schemaMap.get("VscDatabaseVersion"), venueDatabasesStructureName},
                        {containerId, venuesStructureName, structureTypeARRAY, venuesStructureName, "", ""},
                        {containerId, venueStructureName, structureTypeOBJECT, venueStructureName, schemaMap.get("VscVenue"), venuesStructureName},
                        {containerId, campusesStructureName, structureTypeARRAY, campusesStructureName, "", venueStructureName},
                        {containerId, campusStructureName, structureTypeOBJECT, campusStructureName, schemaMap.get("VscCampus"), campusesStructureName},
                        {containerId, buildingsStructureName, structureTypeARRAY, buildingsStructureName, "", campusStructureName},
                        {containerId, buildingStructureName, structureTypeOBJECT, buildingStructureName, schemaMap.get("VscBuilding"), buildingsStructureName},
                        {containerId, floorsStructureName, structureTypeARRAY, floorsStructureName, "", buildingStructureName},
                        {containerId, mapSettingsStructureName, structureTypeOBJECT, mapSettingsStructureName, schemaMap.get("VscMapSettings"), buildingStructureName},
                        {containerId, geoSettingsStructureName, structureTypeOBJECT, geoSettingsStructureName, schemaMap.get("VscGeoSettings"), buildingStructureName},
                        {containerId, floorStructureName, structureTypeOBJECT, floorStructureName, schemaMap.get("VscFloor"), floorsStructureName},
                        {containerId, beaconsStructureName, structureTypeARRAY, beaconsStructureName, "", floorStructureName},
                        {containerId, beaconStructureName, structureTypeOBJECT, beaconStructureName, schemaMap.get("VscBeacon"), beaconsStructureName},
                        {containerId, alertsStructureName, structureTypeARRAY, alertsStructureName, "", beaconStructureName},
                        {containerId, alertStructureName, structureTypeOBJECT, alertStructureName, schemaMap.get("VscProximityAlert"), alertsStructureName}

                };
            case "Directory":
                return new Object[][]{
                        {containerId, items_StructureName, structureTypeARRAY, items_StructureName, "", ""},
                        {containerId, item_StructureName, structureTypeOBJECT, item_StructureName, schemaMap.get("Directory"), items_StructureName}
                };
            default:
                return null;
        }
    }



    /** debug method. Delete. **/
    @AfterClass
    public void printMap() {
        System.out.println(Arrays.asList(structureMap));
    }
}

