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

public class Campus {

    private String xAuth = null;
    private static String capturedCampusId;
    private static String serviceEndPoint = null;
    AuthHeader auth = new AuthHeader();
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Campus.class);


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


    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postCampusRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Campus(String clientId_android_access_key, String clientId_android_signature_key, String postCampusRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1_1;

        String requestBody = fileUtils.getJsonTextFromFile(postCampusRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Campus" + HelperMethods.getDateAsString();
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
        capturedCampusId = response.then().extract().path("data.id").toString();
        //log.info("RESPONSE:" + capturedCampusId);

        //JSON Response Validations
    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postCampusRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Campus_WithoutVenueId(String clientId_android_access_key, String clientId_android_signature_key, String postCampusRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1_1;

        String requestBody = fileUtils.getJsonTextFromFile(postCampusRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "Campus" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("venueGuid", "");


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


    @Parameters({"campusId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 2)
    public void verify_Get_Campus(String campusId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1 + campusId;


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
        response.then().body("id", is(Integer.parseInt(campusId)));
        response.then().body(("any { it.key == 'name'}"), is(true));
        response.then().body(("any { it.key == 'venueGuid'}"), is(true));

    }

    @Parameters({"campusId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 2)
    public void verify_Get_Campus_InvalidId(String campusId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1 + 0;


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

        //JSON response Pay load validations

    }

    //Test not working. Linked to PLAT-7589.
    @Parameters({"venueId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 2)
    public void verify_Get_Campuses_ByVenueId(String venueId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1;


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
                        .queryParam("venueGuid", venueId).urlEncodingEnabled(true)
                        .get(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());

        //JSON response Pay load validations

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putCampusRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Campus(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putCampusRequestBodyPath) throws IOException {

        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1 + capturedCampusId;

        String requestBody = fileUtils.getJsonTextFromFile(putCampusRequestBodyPath);
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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putCampusRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Campus_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putCampusRequestBodyPath) throws IOException {
        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1 + 0;

        String requestBody = fileUtils.getJsonTextFromFile(putCampusRequestBodyPath);
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

    @Parameters({"campusId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 3)
    public void verify_Delete_Campus(String campusId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1 + capturedCampusId;


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

    @Parameters({"campusId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 3)
    public void verify_Delete_Campus_InvalidId(String campusId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT_v1 + 0;


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
