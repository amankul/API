package com.org.map.sp.api.tests;


import com.org.map_api.constants.MapAPI_Constants;
import com.org.utility.AuthHeader;
import com.org.utility.FileUtils;
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

public class Resource {

    private String xAuth = null;
    private static String capturedResourceId;
    private static String serviceEndPoint = null;
    AuthHeader auth = new AuthHeader();
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Resource.class);


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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postResourceRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Resource(String clientId_android_access_key, String clientId_android_signature_key, String postResourceRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postResourceRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        //String name = "Resource" + HelperMethods.getDateAsString();
        //requestBodyJSONObject.put("name", name);


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
        capturedResourceId = response.then().extract().path("data.id").toString();
        //log.info("RESPONSE:" + capturedCampusId);


        //JSON Response Validations
        response.then().body(("data.any { it.key == 'id'}"), is(true));

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postResourceRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Create_Resource_InvalidFloorId(String clientId_android_access_key, String clientId_android_signature_key, String postResourceRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postResourceRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        requestBodyJSONObject.put("floorId", 00);


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


    @Parameters({"resourceId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 1)
    public void verify_Get_Resource(String resourceId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1 + resourceId;


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
        response.then().body("id", is(Integer.parseInt(resourceId)));
        response.then().body(("any { it.key == 'floorId'}"), is(true));
        response.then().body(("any { it.key == 'pdfUrl'}"), is(true));
        response.then().body(("any { it.key == 'svgUrl'}"), is(true));


    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 2)
    public void verify_Get_Resource_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1 + "000";


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

    //Test not working.Linked to PLAT-7589.
    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "clientId", "orgId", "floorId"})
    @Test(priority = 1)
    public void verify_Get_Resources_By_FloorId(String clientId_android_access_key, String clientId_android_signature_key, String floorId, String clientId, String orgId) {


        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1;


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
                        .queryParam("floorId", floorId)
                        .get(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putResourceRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Update_Resource(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putResourceRequestBodyPath) throws IOException {

        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1 + capturedResourceId;

        String requestBody = fileUtils.getJsonTextFromFile(putResourceRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);


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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putResourceRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Resource_By_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putResourceRequestBodyPath) throws IOException {
        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1 + 0;

        String requestBody = fileUtils.getJsonTextFromFile(putResourceRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);


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
    public void verify_Delete_Resource(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1 + capturedResourceId;


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
    public void verify_Delete_Resource_By_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT_v1 + 0;


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
