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
import static org.hamcrest.Matchers.is;

public class Floor {

    private String xAuth = null;
    private static String capturedFloorId;
    private static String serviceEndPoint = null;
    AuthHeader auth = new AuthHeader();
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Building.class);


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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postFloorRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Floor(String clientId_android_access_key, String clientId_android_signature_key, String postFloorRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Floor" + HelperMethods.getDateAsString();
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
                        .header("X-Auth", xAuth)
                        .body(requestBodyJSONObject.toString())
                        .post(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        capturedFloorId = response.then().extract().path("data.id").toString();
        //log.info("RESPONSE:" + capturedCampusId);


        //JSON Response Validations
        response.then().body(("data.any { it.key == 'id'}"), is(true));

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postFloorRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Floor_InvalidVenueId(String clientId_android_access_key, String clientId_android_signature_key, String postFloorRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Floor" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("venueGuid", 0);


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
                        .header("X-Auth", xAuth)
                        .body(requestBodyJSONObject.toString())
                        .post(requestURL)
                        .then()
                        .statusCode(404)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());


    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postFloorRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Floor_InvalidBuildingId(String clientId_android_access_key, String clientId_android_signature_key, String postFloorRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Floor" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("buildingId", 0);


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
                        .header("X-Auth", xAuth)
                        .body(requestBodyJSONObject.toString())
                        .post(requestURL)
                        .then()
                        .statusCode(404)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"floorId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 1)
    public void verify_Get_Floor(String floorId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + floorId;


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
                        .header("X-Auth", xAuth)
                        .get(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());

        //JSON response Pay load validations
        response.then().body("id", is(Integer.parseInt(floorId)));
        response.then().body(("any { it.key == 'name'}"), is(true));
        response.then().body(("any { it.key == 'id'}"), is(true));

    }

    @Parameters({"floorId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 2)
    public void verify_Get_Floor_InvalidId(String floorId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + 00;


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
                        .header("X-Auth", xAuth)
                        .get(requestURL)
                        .then()
                        .statusCode(404)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    //Test not working. Linked to PLAT-7589.
    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "floorId", "clientId", "orgId"})
    @Test(priority = 1)
    public void verify_Get_Floors_By_BuildingId(String clientId_android_access_key, String clientId_android_signature_key, String floorId, String clientId, String orgId) {


        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + floorId;


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
                        .queryParam("orgId", orgId)
                        .get(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putFloorRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Update_Floor(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putFloorRequestBodyPath) throws IOException {

        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + capturedFloorId;

        String requestBody = fileUtils.getJsonTextFromFile(putFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Austin" + HelperMethods.getDateAsString();
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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putFloorRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Floor_By_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putFloorRequestBodyPath) throws IOException {
        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + 0;

        String requestBody = fileUtils.getJsonTextFromFile(putFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "orgAustin" + HelperMethods.getDateAsString();
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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 1)
    public void verify_Delete_Floor(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + capturedFloorId;


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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 2)
    public void verify_Delete_Floor_By_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT_v1 + 0;


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
