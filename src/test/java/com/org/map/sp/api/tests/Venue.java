package com.org.map.sp.api.tests;

import com.org.map_api.constants.MapAPI_Constants;
import com.org.utility.AuthHeader;
import com.org.utility.FileUtils;
import com.org.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;


public class Venue {

    private String xAuth = null;
    private static String capturedVenueId;
    private static String serviceEndPoint = null;
    AuthHeader auth = new AuthHeader();
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Venue.class);


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }

    @Parameters({"venueId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 1)
    public void verify_Get_Venue(String venueId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1 + venueId;


        //Printing Request Details
        log.info("REQUEST-URL:GET-" + requestURL);


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .get(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());

        //JSON response Pay load validations
        response.then().body("guid", is(String.valueOf(venueId)));
        response.then().body("orgIds", is(String.valueOf(orgId)));
        response.then().body(("any { it.key == 'name'}"), is(true));
        response.then().body(("any { it.key == 'clientIds'}"), is(true));
        response.then().body(("any { it.key == 'createdAt'}"), is(true));
        response.then().body(("any { it.key == 'updatedAt'}"), is(true));
        response.then().body(("any { it.key == 'externalGuid'}"), is(true));


    }

    @Parameters({"venueId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 2)
    public void verify_Get_Invalid_Venue(String venueId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1 + "000";


        //Printing Request Details
        log.info("REQUEST-URL:GET-" + requestURL);


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .get(requestURL)
                        .then()
                        .statusCode(404)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());
    }

    //Test not working . Linked to PLAT-7589.
    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 3)
    public void verify_Get_Venues_By_Org(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1_1;


        //Printing Request Details
        log.info("REQUEST-URL:GET-" + requestURL);

        //Query Parameters
        String queryParameters = "orgIds=" + orgId;
        log.info("QUERY-PARAMETERS " + queryParameters);


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .queryParam("orgIds", orgId)
                        .get(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.size", is(greaterThan(0)));
        response.then().body("data.any { it.containsKey('name')}", is(true));


    }


    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "clientId"})
    @Test(priority = 4)
    public void verify_Get_Venues_By_InvalidOrg(String clientId_android_access_key, String clientId_android_signature_key, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1_1;


        //Printing Request Details
        log.info("REQUEST-URL:GET-" + requestURL);

        //Query Parameters
        String queryParameters = null;
        log.info(queryParameters);


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("X-Auth", xAuth)
                        .header("x-org-id", "12c").header("x-client-id", clientId)
                        .get(requestURL)
                        .then()
                        .statusCode(400)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        ;


    }


    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postVenueRequestBodyPath"})
    @Test(priority = 5)
    public void verify_Create_Venue(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postVenueRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1_1;

        String requestBody = fileUtils.getJsonTextFromFile(postVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Sidvita_Test_4" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);


        //Printing Request Details
        log.info("REQUEST-URL:POST-" + requestURL);
        log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .body(requestBodyJSONObject.toString())
                        .post(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        capturedVenueId = response.then().extract().path("data.guid").toString();


    }


    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putVenueRequestBodyPath"})
    @Test(priority = 6)
    public void verify_Update_Venue(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putVenueRequestBodyPath) throws IOException {
        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1 + capturedVenueId;

        String requestBody = fileUtils.getJsonTextFromFile(putVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "orgAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("guid", is(String.valueOf(capturedVenueId)));
        requestBodyJSONObject.put("name", name);


        //Printing Request Details
        log.info("REQUEST-URL:POST-" + requestURL);
        log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("PUT", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .body(requestBodyJSONObject.toString())
                        .put(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());


    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putVenueRequestBodyPath"})
    @Test(priority = 7)
    public void verify_Update_Venue_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putVenueRequestBodyPath) throws IOException {
        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1 + 0;

        String requestBody = fileUtils.getJsonTextFromFile(putVenueRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "orgAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("guid", "0");
        requestBodyJSONObject.put("name", name);


        //Printing Request Details
        log.info("REQUEST-URL:POST-" + requestURL);
        log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("PUT", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .body(requestBodyJSONObject.toString())
                        .put(requestURL)
                        .then()
                        .statusCode(404)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());


    }


    @Parameters({"venueId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 8)
    public void verify_Delete_Venue(String venueId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1 + capturedVenueId;


        //Printing Request Details
        log.info("REQUEST-URL:DELETE-" + requestURL);


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("DELETE", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .delete(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "OK");


    }

    @Parameters({"venueId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 9)
    public void verify_Delete_Venue_InvalidId(String venueId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_v1 + 0;


        //Printing Request Details
        log.info("REQUEST-URL:DELETE-" + requestURL);


        //Auth Generation
        try {
            xAuth = auth.generateAuthHeader("DELETE", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
        } catch (Exception e) {
            log.error("Error generating Auth header" + e);
        }

        //Printing xAuth
        log.info("X-AUTH " + xAuth);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .header("x-org-id", orgId).header("x-client-id", clientId)
                        .header("X-Auth", xAuth)
                        .delete(requestURL)
                        .then()
                        .statusCode(404)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "{ \"data\": {\"message\": \"Resource not found\"} }");


    }


}