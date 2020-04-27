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


public class Route {

    private String xAuth = null;
    private static String capturedRouteId;
    private static String serviceEndPoint = null;
    AuthHeader auth = new AuthHeader();
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Point.class);


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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Route(String clientId_android_access_key, String clientId_android_signature_key, String postRouteRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        //String name = "Route" + HelperMethods.getDateAsString();
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
        capturedRouteId = response.then().extract().path("data.id").toString();
        //log.info("RESPONSE:" + capturedCampusId);


        //JSON Response Validations
        response.then().body(("data.any { it.key == 'id'}"), is(true));

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "postRouteRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Create_Route_InvalidStartAndEndId(String clientId_android_access_key, String clientId_android_signature_key, String postRouteRequestBodyPath) throws IOException {    //Request Details


        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1;

        String requestBody = fileUtils.getJsonTextFromFile(postRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        //String name = "Route" + HelperMethods.getDateAsString();
        //requestBodyJSONObject.put("name", name);
        requestBodyJSONObject.put("startPointId", 123);
        requestBodyJSONObject.put("endPointId", 1234);


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

    @Parameters({"routeId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 1)
    public void verify_Get_Route(String routeId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + capturedRouteId;


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
        response.then().body("id", is(Integer.parseInt(capturedRouteId)));
        response.then().body(("any { it.key == 'isAccessible'}"), is(true));
        response.then().body(("any { it.key == 'startPointId'}"), is(true));

    }

    @Parameters({"routeId", "clientId_android_access_key", "clientId_android_signature_key"})
    @Test(priority = 1)
    public void verify_Get_Route_ByInvalidId(String routeId, String clientId_android_access_key, String clientId_android_signature_key) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + 00;


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

    //Test not working.Linked to PLAT-7589.
    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "routeId", "clientId", "orgId", "floorId"})
    @Test(priority = 1)
    public void verify_Get_Routes_By_FloorId(String clientId_android_access_key, String clientId_android_signature_key, String floorId, String clientId, String orgId) {


        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1;


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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "routeId", "clientId", "orgId", "floorId"})
    @Test(priority = 1)
    public void verify_Get_Location_Routes(String clientId_android_access_key, String clientId_android_signature_key, String floorId, String clientId, String orgId) {


        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1_2;


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

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Update_Route(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putRouteRequestBodyPath) throws IOException {

        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + 6768796;

        String requestBody = fileUtils.getJsonTextFromFile(putRouteRequestBodyPath);
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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putRouteRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Route_By_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putRouteRequestBodyPath) throws IOException {
        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + 0;

        String requestBody = fileUtils.getJsonTextFromFile(putRouteRequestBodyPath);
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

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "resetRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Update_BuildingRoute(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String resetRouteRequestBodyPath) throws IOException {

        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + "update";

        String requestBody = fileUtils.getJsonTextFromFile(resetRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);


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

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "resetRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Update_BuildingRoute_ByInvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String resetRouteRequestBodyPath) throws IOException {

        //Request Details
        String requestURL = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + "update";

        String requestBody = fileUtils.getJsonTextFromFile(resetRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        requestBodyJSONObject.put("buildingId", 1234000000);


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
                        .statusCode(404)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }


    @Parameters({"routeId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "resetRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Reset_Route(String routeId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String resetRouteRequestBodyPath) throws IOException {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1_1;


        String requestBody = fileUtils.getJsonTextFromFile(resetRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);


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
                        .statusCode(202)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"routeId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Delete_Route(String routeId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putRouteRequestBodyPath) throws IOException {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + 6768796;

        String requestBody = fileUtils.getJsonTextFromFile(putRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

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
                        .body(requestBodyJSONObject.toString())
                        .delete(requestURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "OK");


    }

    @Parameters({"routeId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 2)
    public void verify_Delete_Route_By_InvalidId(String routeId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_v1 + 0;


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
