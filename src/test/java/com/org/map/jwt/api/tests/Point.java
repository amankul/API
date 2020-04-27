package com.org.map.jwt.api.tests;

import com.org.map_api.constants.MapAPI_Constants;
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

public class Point {

    private static String capturedPointId;
    private static Integer resultcount;
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Point.class);

    private static  String pointURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            pointURL = serviceEndPoint + MapAPI_Constants.POINT_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            pointURL = serviceEndPoint + MapAPI_Constants.POINT_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Live_Points_By_Floor(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "Live";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + pointURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"floorId\":15621,\"buildingId\": 5881, \"draftStatus\":\"LIVE\"}", "")
                        .get(pointURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());

        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        resultcount = response.then().extract().path("resultCount");
        response.then().body(("any { it.key == 'offset'}"), is(true));
        response.then().body("items.size", is(resultcount));

    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Draft_Points_By_Floor(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + pointURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"floorId\":15621,\"buildingId\": 5881,\"draftStatus\":\"DRAFT\"}", "")
                        .get(pointURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        resultcount = response.then().extract().path("resultCount");
        response.then().body(("any { it.key == 'offset'}"), is(true));
        response.then().body("items.size", is(resultcount));

    }

    @Parameters({"jwt", "CreatePointRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Point(String jwt, String CreatePointRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(CreatePointRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "automation_Test" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + pointURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(pointURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.any { it.key == 'id'}",is(true));
        capturedPointId = response.then().extract().path("data.id").toString();
        log.info(capturedPointId);
        
    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Point_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedPointId);

        //Request Details
        String pointURL1 = pointURL + "/" + capturedPointId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + pointURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(pointURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("any { it.key == 'id'}",is(true));
        response.then().body("any { it.key == 'poiType'}",is(true));
        
    }

    @Parameters({"jwt", "UpdatePointRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Point_By_Id(String jwt, String UpdatePointRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedPointId);

        //Request Details
        String pointURL1 = pointURL+ "/" + capturedPointId;
        String requestBody = fileUtils.getJsonTextFromFile(UpdatePointRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "orgAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + pointURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(pointURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        
    }

    @Parameters({"jwt"})
    @Test(priority = 3)
    public void verify_Delete_Point_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedPointId);

        //Request Details
        String pointURL1 = pointURL + "/" + capturedPointId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + pointURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(pointURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        
    }


}
    

