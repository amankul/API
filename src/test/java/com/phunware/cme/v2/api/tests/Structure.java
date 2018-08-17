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
import java.util.HashMap;

import static com.phunware.cme.v2.api.tests.Schema.schemaMap;
import static io.restassured.RestAssured.given;



/**
 * Created by VinayKarumuri on 7/20/18.
 */

public class Structure {

  static Logger log;
  //public String dynamicValue;
  FileUtils fileUtils = new FileUtils();
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
  public static String platformvenuesStructureName = "Platform_Venues";
  public static String platformvenueStructureName = "Platform_Venue";
  public static String platformdatabasesStructureName = "Platform_Databases";
  public static String platformdatabaseStructureName = "Platform_Database";
  public static String venuedatabasesStructureName = "Venue_Databases";
  public static String venuedatabaseStructureName = "Venue_Database";

  public static String venuesStructureName = "Venues";
  public static String venueStructureName = "Venue";
  public static String campusesStructureName = "Campuses";
  public static String campusStructureName = "Campus";
  public static String buildingsStructureName = "Buildings";
  public static String buildingStructureName = "Building";
  public static String floorsStructureName = "Floors";
  public static String floorStructureName = "Floors";
  public static String mapSettingsStructureName = "MapSettings";
  public static String geoSettingsStructureName = "GeoSettings";
  public static String beaconsStructureName = "Beacons";
  public static String beaconStructureName = "Beacon";
  public static String alertsStructureName = "Alerts";
  public static String alertStructureName = "Alert";


  public static int applicationstructureId=0;


  public static HashMap<String,Integer> structureMap = new HashMap<String,Integer>();


  @BeforeClass
  @Parameters({"env","jwt","orgId","postStructureFilePath"})
  public void setEnv(String env, String jwt, String orgId, String postStructureFilePath) throws IOException{
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
    log.info("Container " + postContainerRequestURL);
    dateTime = HelperMethods.getDateAsString();
    structureRequestBody = fileUtils.getJsonTextFromFile(postStructureFilePath);
  }


 /** Creating container for Dignity Health **/
  @Parameters("postContainerFilePath")
  @Test(priority = 1)
  public void  verify_Post_Container(String postContainerFilePath) throws IOException {

    //Request Details
    //log.info(postContainerFilePath);
    String requestBody = fileUtils.getJsonTextFromFile(postContainerFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", "Dignity Health "+dateTime);

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


/** Creating Structures to the above container **/
  @Test(dataProvider = "dignityStructure", priority = 2)
  public void verify_Post_Structure(String containerId, String name ,String type,String field ,String schemaId ,String parent) throws IOException {

    //Request Details
    JSONObject requestBodyJSONObject = new JSONObject(structureRequestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("containerId" , containerId );
    requestBodyData.put("name", name + dateTime);
    requestBodyData.put("type" , type );
    requestBodyData.put("field", field);
    requestBodyData.put("schemaId" , schemaId);
    log.info("parentId " + structureMap.get(parent));
    requestBodyData.put("parentId", structureMap.get(parent));


    //Printing Request Details
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

    //printing response
    log.info("RESPONSE: " + response.asString());

    //JSON response Pay load validations
    int StructureId  = response.getBody().jsonPath().get("id");
    log.info("NAME: " + name);
    log.info("structureId = " + StructureId );
    structureMap.put(name,StructureId);
    log.info("STRUCTURE MAP: " + structureMap);
    log.info("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

  }


  @DataProvider(name = "dignityStructure")
  public Object[][] dignityStructureDataSet() {
    return new Object[][]{

            {containerId,applicationsStructureName, structureTypeARRAY,applicationsStructureName, "", ""},
            {containerId,applicationStructureName, structureTypeOBJECT,applicationStructureName, schemaMap.get("VscApp"), applicationsStructureName},
            {containerId,platformsStructureName, structureTypeARRAY,platformsStructureName, "", applicationStructureName},
            {containerId,platformStructureName, structureTypeOBJECT,platformStructureName, schemaMap.get("VscPlatform"), platformsStructureName},
            {containerId,appVersionStructureName, structureTypeOBJECT,appVersionStructureName, schemaMap.get("VscAppVersion"), platformStructureName},
            {containerId,cachingSettingsStructureName, structureTypeOBJECT,cachingSettingsStructureName, schemaMap.get("VscPreCachingConfiguration"), platformStructureName},
            {containerId,advertisementSettingsStructureName, structureTypeOBJECT,advertisementSettingsStructureName, schemaMap.get("VscAdvertisingSetting"), platformStructureName},
            {containerId,settingsStructureName, structureTypeOBJECT,settingsStructureName, schemaMap.get("VscSettings"), platformStructureName},
            {containerId,platformvenuesStructureName, structureTypeARRAY,platformvenuesStructureName, "", platformStructureName},
            {containerId,platformvenueStructureName, structureTypeOBJECT,platformvenueStructureName, schemaMap.get("VscVenue"), platformvenuesStructureName},
            {containerId,platformdatabasesStructureName, structureTypeARRAY,platformdatabasesStructureName, "", platformStructureName},
            {containerId,platformdatabaseStructureName, structureTypeOBJECT,platformdatabaseStructureName, schemaMap.get("VscDatabaseVersion"), platformdatabasesStructureName},
            {containerId,venuedatabasesStructureName, structureTypeARRAY,venuedatabasesStructureName, "", platformvenueStructureName},
            {containerId,venuedatabaseStructureName, structureTypeOBJECT,venuedatabaseStructureName, schemaMap.get("VscDatabaseVersion"), venuedatabasesStructureName},

            {containerId,venuesStructureName, structureTypeARRAY,venuesStructureName, "", ""},
            {containerId,venueStructureName, structureTypeOBJECT,venueStructureName, schemaMap.get("VscVenue"), venuesStructureName},
            {containerId,campusesStructureName, structureTypeARRAY,campusesStructureName, "", venueStructureName},
            {containerId,campusStructureName, structureTypeOBJECT,campusStructureName, schemaMap.get("VscCampus"), campusesStructureName},
            {containerId,buildingsStructureName, structureTypeARRAY,buildingsStructureName, "", campusStructureName},
            {containerId,buildingStructureName, structureTypeOBJECT,buildingStructureName, schemaMap.get("VscBuilding"), buildingsStructureName},
            {containerId,floorsStructureName, structureTypeARRAY,floorsStructureName, "", buildingStructureName},
            {containerId,mapSettingsStructureName, structureTypeOBJECT,mapSettingsStructureName, schemaMap.get("VscMapSettings"), buildingStructureName},
            {containerId,geoSettingsStructureName, structureTypeOBJECT,geoSettingsStructureName, schemaMap.get("VscGeoSettings"), buildingStructureName},
            {containerId,floorStructureName, structureTypeOBJECT,floorStructureName, schemaMap.get("VscFloor"), floorsStructureName},
            {containerId,beaconsStructureName, structureTypeARRAY,beaconsStructureName, "", floorStructureName},
            {containerId,beaconStructureName, structureTypeOBJECT,beaconStructureName, schemaMap.get("VscBeacon"), beaconsStructureName},
            {containerId,alertsStructureName, structureTypeARRAY,alertsStructureName, "", beaconStructureName},
            {containerId,alertStructureName, structureTypeOBJECT,alertStructureName, schemaMap.get("VscProximityAlert"), alertsStructureName}




    };
  }

}
