package com.phunware.map.jwt.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import com.phunware.utility.JWTUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class MappingV3 {
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Venue.class);
    private static  String venueUrl,jwt,venueUrlById;
    private String capturedVenueId;


    @BeforeClass
    @Parameters({"env","orgId"})
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

        venueUrl = serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_V3;

        log.info("URL - " + venueUrl);

    }


    @Parameters({"CreateVenueRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Venue(String createVenueRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(createVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "QA-VENUE-" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:POST- " + venueUrl);

        //Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(venueUrl)
                        .then()
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.any { it.key == 'guid'}", is(true));
        response.then().statusCode(HttpStatus.SC_OK);
        capturedVenueId = response.then().extract().path("data.guid").toString();
        Assert.assertNotNull(capturedVenueId);

    }


    @Parameters()
    @Test(priority = 2)
    public void verify_Get_Draft_Venue_By_Id() throws IOException {
        Assert.assertNotNull(capturedVenueId);

        //Request Details
        venueUrlById = venueUrl + "/" + capturedVenueId;

        //Printing Request Details
        log.info("REQUEST-URL:GET- " + venueUrlById);

        //Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth().oauth2(jwt)
                        .queryParam("draftStatus","DRAFT")
                        .queryParam("includeBuildings","true")
                        .get(venueUrlById)
                        .then()
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("guid" ,is(capturedVenueId));
        response.then().body("draftStatus" ,is("DRAFT"));
        response.then().body("any { it.key == 'buildings'}", is(true));


    }

    @Parameters({"UpdateVenueRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Venue_By_Id(String updateVenueRequestBodyPath) throws IOException {

        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(updateVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        //Printing Request Details
        log.info("REQUEST-URL:PUT- " + venueUrlById);

        //Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(venueUrlById)
                        .then()
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_NO_CONTENT);

    }



/*    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Live_Venues_By_Org(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "Live";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + venueURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .get(venueURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

        resultcount = response.then().extract().path("resultCount");
        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        response.then().body(("any { it.key == 'offset'}"), is(true));
        response.then().body("items.size", is(resultcount));

    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Draft_Venues_By_Org(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + venueURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(venueURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

        resultcount = response.then().extract().path("resultCount");
        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        response.then().body(("any { it.key == 'offset'}"), is(true));
        response.then().body("items.size", is(resultcount));

    }





    @Parameters({"jwt", "UpdateBuildingRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Venue_By_Id(String jwt, String UpdateBuildingRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedVenueId);

        //Request Details
        String venueURL1 = venueURL + "/" + capturedVenueId;
        String requestBody = fileUtils.getJsonTextFromFile(UpdateBuildingRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "PhunwareAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + venueURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(venueURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"jwt"})
    @Test(priority = 3)
    public void verify_Delete_Venue_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedVenueId);

        //Request Details
        String venueURL1 = venueURL + "/" + capturedVenueId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + venueURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(venueURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");

    }
    */

}
