package com.phunware.map.jwt.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import com.phunware.utility.JWTUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

/* Created by Amit Kulkarni on 11/14/2018 */

public class MappingV3 {
    private static String serviceEndPoint = null;
    private static Logger log = Logger.getLogger(MappingV3.class);
    private static String venueUrl,
            jwt,
            venueUrlById,
            campusUrl,
            campusUrlById,
            buildingUrl,
            buildingUrlById,
            floorUrl,
            publishUrl,
            floorUrlById;
    FileUtils fileUtils = new FileUtils();
    private String capturedVenueId, capturedLiveVenueId;
    private int capturedCampusId, capturedBuildingId, capturedFloorId;

    @BeforeClass
    @Parameters({"env", "orgId"})
    public void preTestSteps(String env, int orgId) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;

        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }

        jwt = JWTUtils.getJWTForAdmin(env, orgId);
        Assert.assertNotNull(jwt);

        venueUrl = serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_V3;
        campusUrl = serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_V3;
        buildingUrl = serviceEndPoint + MapAPI_Constants.BUILDING_END_POINT_V3;
        floorUrl = serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_V3;
        publishUrl = serviceEndPoint + MapAPI_Constants.PUBLISH_END_POINT_V3;
    }

    /* This method creates a brand new Venue in QA Test org. All subsequent methods depend on successful creation of venue */

    @Parameters({"CreateVenueRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Venue(String createVenueRequestBodyPath) throws IOException {
        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(createVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "QA-VENUE-" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        // Printing Request Details
        log.info("REQUEST-URL:POST- " + venueUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(venueUrl)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("data.any { it.key == 'guid'}", is(true));

        capturedVenueId = response.then().extract().path("data.guid").toString();
        Assert.assertNotNull(capturedVenueId);
    }

    /* This method retrieves the venue created above which will be in DRAFT status */


    @Test(priority = 2)
    public void verify_Get_Draft_Venue_By_Id() throws IOException {
        Assert.assertNotNull(capturedVenueId);

        // Request Details
        venueUrlById = venueUrl + "/" + capturedVenueId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + venueUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .queryParam("draftStatus", "DRAFT")
                        .queryParam("includeBuildings", "true")
                        .get(venueUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("guid", is(capturedVenueId));
        response.then().body("draftStatus", is("DRAFT"));
        response.then().body("any { it.key == 'orgIds'}", is(true));

        // Buildings fields is returned in response if includeBuildings param is set to true
        response.then().body("any { it.key == 'buildings'}", is(true));
    }

    /* This method updates the venue created  */

    @Parameters({"UpdateVenueRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Venue_By_Id(String updateVenueRequestBodyPath) throws IOException {

        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(updateVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        // Printing Request Details
        log.info("REQUEST-URL:PUT- " + venueUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(venueUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method creates a new campus under the venue created earlier */

    @Parameters({"CreateCampusRequestBodyPath"})
    @Test(priority = 3)
    public void verify_Create_Campus(String createCampusRequestBodyPath) throws IOException {

        Assert.assertNotNull(capturedVenueId);
        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(createCampusRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "QA-CAMPUS-" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("venueGuid", capturedVenueId);

        // Printing Request Details
        log.info("REQUEST-URL:POST- " + campusUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(campusUrl)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("data.any { it.key == 'id'}", Matchers.is(true));
        capturedCampusId = response.then().extract().path("data.id");
        Assert.assertNotNull(capturedCampusId);
    }

    /* This method retrieves the campus created above which will be in DRAFT status */


    @Test(priority = 4)
    public void verify_Get_Draft_Campus_By_Id() throws IOException {

        Assert.assertNotNull(capturedCampusId);
        // Request Details
        campusUrlById = campusUrl + "/" + capturedCampusId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + campusUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .queryParam("draftStatus", "DRAFT")
                        .get(campusUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("venueGuid", is(capturedVenueId));
        response.then().body("id", is(capturedCampusId));
    }

    /* This method updates the campus created  */

    @Parameters({"UpdateCampusRequestBodyPath"})
    @Test(priority = 4)
    public void verify_Update_Campus_By_Id(String updateCampusRequestBodyPath) throws IOException {

        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(updateCampusRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        // Printing Request Details
        log.info("REQUEST-URL:PUT- " + campusUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(campusUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method creates a new building under the campus created earlier */

    @Parameters({"CreateBuildingRequestBodyPath"})
    @Test(priority = 5)
    public void verify_Create_Building(String createBuildingRequestBodyPath) throws IOException {

        Assert.assertNotNull(capturedCampusId);
        Assert.assertNotNull(capturedVenueId);
        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(createBuildingRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "QA-BUILDING-" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("campusId", capturedCampusId);
        requestBodyJSONObject.put("venueGuid", capturedVenueId);

        // Printing Request Details
        log.info("REQUEST-URL:POST-" + buildingUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(buildingUrl)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("data.any { it.key == 'id'}", Matchers.is(true));
        capturedBuildingId = response.then().extract().path("data.id");
        Assert.assertNotNull(capturedBuildingId);
    }

    /* This method retrieves the building created above which will be in DRAFT status */


    @Test(priority = 6)
    public void verify_Get_Draft_Building_By_Id() throws IOException {

        Assert.assertNotNull(capturedBuildingId);
        // Request Details
        buildingUrlById = buildingUrl + "/" + capturedBuildingId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + buildingUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .queryParam("draftStatus", "DRAFT")
                        .queryParam("deep", "true")
                        .get(buildingUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("venueGuid", is(capturedVenueId));
        response.then().body("campusId", is(capturedCampusId));
        response.then().body("id", is(capturedBuildingId));
        response.then().body("any { it.key == 'name'}", is(true));
        response.then().body("any { it.key == 'floors'}", is(true));
    }

    /* This method updates the building created  */

    @Parameters({"UpdateBuildingRequestBodyPath"})
    @Test(priority = 6)
    public void verify_Update_Building_By_Id(String updateBuildingRequestBodyPath)
            throws IOException {

        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(updateBuildingRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        // Printing Request Details
        log.info("REQUEST-URL:PUT- " + buildingUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(buildingUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method creates a new floor under the building created earlier */

    @Parameters({"CreateFloorRequestBodyPath"})
    @Test(priority = 7)
    public void verify_Create_Floor(String createFloorRequestBodyPath) throws IOException {

        Assert.assertNotNull(capturedBuildingId);
        Assert.assertNotNull(capturedVenueId);
        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(createFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "QA-FLOOR-" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("buildingId", capturedBuildingId);
        requestBodyJSONObject.put("venueGuid", capturedVenueId);

        // Printing Request Details
        log.info("REQUEST-URL:POST-" + floorUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(floorUrl)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("data.any { it.key == 'id'}", Matchers.is(true));
        capturedFloorId = response.then().extract().path("data.id");
        Assert.assertNotNull(capturedFloorId);
    }

    /* This method retrieves the floor created above which will be in DRAFT status */


    @Test(priority = 8)
    public void verify_Get_Draft_Floor_By_Id() throws IOException {

        Assert.assertNotNull(capturedFloorId);
        // Request Details
        floorUrlById = floorUrl + "/" + capturedFloorId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + floorUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .queryParam("draftStatus", "DRAFT")
                        .get(floorUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("venueGuid", is(capturedVenueId));
        response.then().body("buildingId", is(capturedBuildingId));
        response.then().body("id", is(capturedFloorId));
        response.then().body("any { it.key == 'originalMapUrl'}", is(true));
        response.then().body("any { it.key == 'isOutdoor'}", is(true));
        response.then().body("any { it.key == 'level'}", is(true));
    }

    /* This method updates the floor created  */

    @Parameters({"UpdateFloorRequestBodyPath"})
    @Test(priority = 8)
    public void verify_Update_Floor_By_Id(String updateFloorRequestBodyPath) throws IOException {

        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(updateFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        // Printing Request Details
        log.info("REQUEST-URL:PUT- " + floorUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(floorUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method publishes the venue so all entities under it become LIVE  */

    @Parameters({"PublishRequestBodyPath"})
    @Test(priority = 9)
    public void verify_Publish_Venue(String publishVenueRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedVenueId);
        // Request Details
        String requestBody = fileUtils.getJsonTextFromFile(publishVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        requestBodyJSONObject.put("sourceGuid", capturedVenueId);

        // Printing Request Details
        log.info("REQUEST-URL:PUT- " + publishUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(publishUrl)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_ACCEPTED);
        response.then().body("data.any { it.key == 'guid'}", is(true));
        response.then().body("data.message", is("Accepted"));

        capturedLiveVenueId = response.then().extract().path("data.guid").toString();
        Assert.assertNotNull(capturedLiveVenueId);
    }

    /* This method retrieves the LIVE venue created above after publish. We also verify fields modified in update tests earlier */


    @Test(priority = 10)
    public void verify_Get_Live_Venue_By_Id() throws IOException {
        Assert.assertNotNull(capturedLiveVenueId);

        // Request Details
        venueUrlById = venueUrl + "/" + capturedLiveVenueId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + venueUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .get(venueUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("guid", is(capturedLiveVenueId));
        response.then().body("draftStatus", is("LIVE"));
        response.then().body("isActive", is(false));
    }

    /* This method retrieves the LIVE campus created above after publish. We also verify fields modified in update tests earlier */


    @Test(priority = 10)
    public void verify_Get_Live_Campus_By_Id() throws IOException {

        Assert.assertNotNull(capturedCampusId);
        // Request Details
        campusUrlById = campusUrl + "/" + capturedCampusId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + campusUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .get(campusUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("venueGuid", is(capturedLiveVenueId));
        response.then().body("name", is("Automation_Update_Campus"));
    }

    /* This method retrieves the LIVE building created above after publish. We also verify fields modified in update tests earlier */


    @Test(priority = 10)
    public void verify_Get_Live_Building_By_Id() throws IOException {

        Assert.assertNotNull(capturedBuildingId);
        // Request Details
        buildingUrlById = buildingUrl + "/" + capturedBuildingId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + buildingUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .get(buildingUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("venueGuid", is(capturedLiveVenueId));
        response.then().body("campusId", is(capturedCampusId));
        response.then().body("streetAddress", is("123 Majora"));
    }

    /* This method retrieves the LIVE floor created above after publish. We also verify fields modified in update tests earlier */


    @Test(priority = 10)
    public void verify_Get_Live_Floor_By_Id() throws IOException {

        Assert.assertNotNull(capturedFloorId);
        // Request Details
        floorUrlById = floorUrl + "/" + capturedFloorId;

        // Printing Request Details
        log.info("REQUEST-URL:GET- " + floorUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .get(floorUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("venueGuid", is(capturedLiveVenueId));
        response.then().body("buildingId", is(capturedBuildingId));
        response.then().body("isOutdoor", is(true));
    }


    /* This method deletes the floor created earlier */


    @Test(priority = 11)
    public void verify_Delete_Floor() throws IOException {

        Assert.assertNotNull(capturedFloorId);
        // Request Details
        floorUrlById = floorUrl + "/" + capturedFloorId;

        // Printing Request Details
        log.info("REQUEST-URL:DELETE- " + floorUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .delete(floorUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method deletes the building created earlier */


    @Test(priority = 12)
    public void verify_Delete_Building() throws IOException {

        Assert.assertNotNull(capturedBuildingId);
        // Request Details
        buildingUrlById = buildingUrl + "/" + capturedBuildingId;

        // Printing Request Details
        log.info("REQUEST-URL:DELETE- " + buildingUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .delete(buildingUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method deletes the campus created earlier */


    @Test(priority = 13)
    public void verify_Delete_Campus() throws IOException {

        Assert.assertNotNull(capturedCampusId);
        // Request Details
        campusUrlById = campusUrl + "/" + capturedCampusId;

        // Printing Request Details
        log.info("REQUEST-URL:DELETE- " + campusUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .delete(campusUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }

    /* This method deletes the venue created earlier */

    
    @Test(priority = 14)
    public void verify_Delete_Venue() throws IOException {
        Assert.assertNotNull(capturedVenueId);

        // Request Details
        venueUrlById = venueUrl + "/" + capturedVenueId;

        // Printing Request Details
        log.info("REQUEST-URL:DELETE- " + venueUrlById);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .delete(venueUrlById)
                        .then()
                        .extract()
                        .response();

        // printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);
        Assert.assertEquals(response.asString(), "");
    }


}
