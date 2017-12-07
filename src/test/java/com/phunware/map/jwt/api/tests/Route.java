package com.phunware.map.jwt.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.FileUtils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class Route {

    private static String capturedRouteId;
    private static String capturedRouteId2;
    private static String capturedRouteLat;
    private static String capturedRouteLon;
    private static String routeURL;
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Point.class);


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            routeURL = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            routeURL = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }


    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Draft_Routes_By_Floor(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + routeURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"floorId\":15621,\"startPointId\":4286412,\"endPointId\":4286406,\"isAccessible\":true,\"draftStatus\":\"DRAFT\"}", "")
                        .get(routeURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        capturedRouteId = response.then().extract().path("data.id[0]").toString();
        capturedRouteLat = response.then().extract().path("data.data[0].location.latitude[0]").toString();
        capturedRouteLon = response.then().extract().path("data.data[0].location.longitude[0]").toString();
        log.info(capturedRouteId);
        log.info(capturedRouteLat);
        log.info(capturedRouteLon);

    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Routes_By_Location(String jwt) {
        Assert.assertNotNull(capturedRouteLat);
        Assert.assertNotNull(capturedRouteLon);

        //Request Details
        String queryParameters = "{\"destinationPOI\":4286412,\"isAccessible\":true,\"location\":{\"longitude\":" + capturedRouteLon + ",\"latitude\":" + capturedRouteLat + "},\"floorId\":15621}";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + routeURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"destinationPOI\":4286398,\"isAccessible\":true,\"location\":{\"longitude\":" + capturedRouteLon + ",\"latitude\":" + capturedRouteLat + "},\"floorId\":15621}", "")
                        .get(routeURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Route_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedRouteId);

        //Request Details
        String routeURL1 = routeURL+ "/" + capturedRouteId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + routeURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(routeURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }


    @Parameters({"jwt", "CreateRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Route(String jwt, String CreateRouteRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(CreateRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + routeURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(routeURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();


        //printing response
        log.info("RESPONSE:" + response.asString());
        capturedRouteId2 = response.then().extract().path("data.id").toString();
        Assert.assertNotNull(capturedRouteId2);
        log.info(capturedRouteId2);

    }


    @Parameters({"jwt", "UpdateRouteRequestBodyPath"})
    @Test(priority = 5)
    public void verify_Update_Route_By_Id(String jwt, String UpdateRouteRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedRouteId2);

        //Request Details
        String routeURL1 = routeURL + "/" + capturedRouteId2;
        String requestBody = fileUtils.getJsonTextFromFile(UpdateRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + routeURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(routeURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        
    }

    @Parameters({"jwt", "resetRouteRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Reset_Route(String jwt, String resetRouteRequestBodyPath) throws IOException {
        //Request Details
        String routeURL2 = serviceEndPoint + MapAPI_Constants.ROUTE_END_POINT_2;
        String requestBody = fileUtils.getJsonTextFromFile(resetRouteRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        //Printing Request Details
        log.info("REQUEST-URL:RESET-" + routeURL2);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(routeURL2)
                        .then()
                        .statusCode(202)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        
    }


    @Parameters({"jwt"})
    @Test(priority = 3)
    public void verify_Delete_Route_By_Id( String jwt) throws IOException {
        //Request Details
        String routeURL1 = routeURL +"/"+capturedRouteId2;

        //Printing Request Details
        log.info("REQUEST-URL:DELETE-" + routeURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(routeURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        
    }


}
