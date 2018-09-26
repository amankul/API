package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import com.phunware.utility.JWTUtils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;

import static com.phunware.cme.v2.api.tests.Schema.schemaMap;
import static com.phunware.cme.v2.api.tests.Schema.containerName;
import static io.restassured.RestAssured.given;


/**
 * Created by VinayKarumuri on 7/20/18.
 */

public class Structure {

    private static Logger log;
    private static String serviceEndPoint = null;
    private static String jwt = null;
    private static String postStructureRequestURL = null;
    private static String postContainerRequestURL = null;
    private static String dateTime = null;
    private static String structureRequestBody = null;
    private static final String structureTypeOBJECT = "object";
    private static final String structureTypeARRAY = "array";
    private static String applicationsStructureName = "Applications";
    private static String applicationStructureName = "Application";
    private static String platformsStructureName = "Platforms";
    private static String platformStructureName = "Platform";
    private static String appVersionStructureName = "AppVersion";
    private static String cachingSettingsStructureName = "CachingSettings";
    private static String advertisementSettingsStructureName = "AdvertisementSettings";
    private static String settingsStructureName = "Settings";
    private static String platformVenuesStructureName = "Platform_Venues";
    private static String platformVenueStructureName = "Platform_Venue";
    private static String platformDatabasesStructureName = "Platform_Databases";
    private static String platformDatabaseStructureName = "Platform_Database";
    private static String venueDatabasesStructureName = "Venue_Databases";
    private static String venueDatabaseStructureName = "Venue_Database";
    private static String items_StructureName = "Items";
    private static String item_StructureName = "Item";
    private static String venuesStructureName = "Venues";
    private static String venueStructureName = "Venue";
    private static String campusesStructureName = "Campuses";
    private static String campusStructureName = "Campus";
    private static String buildingsStructureName = "Buildings";
    private static String buildingStructureName = "Building";
    private static String floorsStructureName = "Floors";
    private static String floorStructureName = "Floor";
    private static String mapSettingsStructureName = "MapSettings";
    private static String geoSettingsStructureName = "GeoSettings";
    private static String beaconsStructureName = "Beacons";
    private static String beaconStructureName = "Beacon";
    private static String alertsStructureName = "Alerts";
    private static String alertStructureName = "Alert";
    private int structureId;
    protected static String containerId = null;

    protected static HashMap<String, Integer> structureMap = new HashMap<String, Integer>();


    @BeforeClass
    @Parameters({"env", "orgId", "postStructureFilePath"})
    private void setEnv(String env, int orgId, String postStructureFilePath) throws IOException {

        log = Logger.getLogger(Structure.class);
        jwt = JWTUtils.getJWTForAdmin(env, orgId);


        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
        postStructureRequestURL = serviceEndPoint + CmeV2_API_Constants.STRUCTURE_END_POINT;
        postContainerRequestURL = serviceEndPoint + CmeV2_API_Constants.CONTAINERS_END_POINT;
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


        String requestBody = FileUtils.getJsonTextFromFile(postContainerFilePath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
        requestBodyData.put("name", Schema.containerName + dateTime);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + postContainerRequestURL);
        log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("Authorization", jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(postContainerRequestURL)
                        .then()
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);

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
                        .header("Authorization", jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(postStructureRequestURL)
                        .then()
                        .extract()
                        .response();
        log.info("RESPONSE: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 200);
            structureId = response.getBody().jsonPath().get("id");
            log.info("NAME: " + name);
            log.info("structureId: " + structureId);
            structureMap.put(name, structureId);
            log.info("STRUCTURE MAP: " + structureMap);
            log.info("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }


    @DataProvider(name = "dignityStructure")
    public Object[][] dignityStructureDataSet() {

        switch (Schema.containerName) {
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
                        {containerId, venueDatabasesStructureName, structureTypeOBJECT, venueDatabasesStructureName, "", platformVenuesStructureName},
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

}

