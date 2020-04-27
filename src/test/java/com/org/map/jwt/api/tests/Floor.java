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

public class Floor {

    private static String capturedFloorId;
    private static Integer resultcount;
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Floor.class);
    private static  String floorURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            floorURL = serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            floorURL = serviceEndPoint + MapAPI_Constants.FLOOR_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }


    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Live_Floors_By_Building(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "Live";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + floorURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"buildingId\":\"5881\"}", "")
                        .get(floorURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        response.then().body(("any { it.key == 'offset'}"), is(true));
        resultcount = response.then().extract().path("resultCount");
        response.then().body("items.size", is(resultcount));

    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Draft_Floors_By_Building(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + floorURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"buildingId\":\"5881\",\"draftStatus\":\"DRAFT\"}", "")
                        .get(floorURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        response.then().body(("any { it.key == 'offset'}"), is(true));
        resultcount = response.then().extract().path("resultCount");
        response.then().body("items.size", is(resultcount));

    }

    @Parameters({"jwt", "CreateFloorRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Floor(String jwt, String postFloorRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(postFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "automation_Test" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + floorURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(floorURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        capturedFloorId = response.then().extract().path("data.id").toString();
        capturedFloorId = response.then().extract().path("data.id").toString();
        
    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Floor_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedFloorId);

        //Request Details
        String floorURL1 = floorURL + "/" + capturedFloorId;
        
        //Printing Request Details
        log.info("REQUEST-URL:GET-" + floorURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(floorURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("any { it.key == 'id'}",is(true));
        response.then().body("any { it.key == 'buildingId'}",is(true));
        
    }

    @Parameters({"jwt", "UpdateFloorRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Floor_By_Id(String jwt, String UpdateFloorRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedFloorId);

        //Request Details
        String floorURL1 = floorURL + "/" + capturedFloorId;
        String requestBody = fileUtils.getJsonTextFromFile(UpdateFloorRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "orgAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + floorURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(floorURL1)
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
    public void verify_Delete_Floor_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedFloorId);

        //Request Details
        String floorURL1 = floorURL + "/" + capturedFloorId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + floorURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(floorURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        
    }


}
