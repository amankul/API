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

public class Building {

    private static String capturedBuildingId;
    private static Integer resultcount;
    private static  String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static final Logger log = Logger.getLogger(Building.class);
    private static  String buildingURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            buildingURL = serviceEndPoint + MapAPI_Constants.BUILDING_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            buildingURL = serviceEndPoint + MapAPI_Constants.BUILDING_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }




    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Live_Buildings_By_Campus(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "Live";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + buildingURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"campusId\":\"4203\"}", "")
                        .get(buildingURL)
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
    public void verify_Get_Draft_Buildings_By_Campus(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + buildingURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"campusId\":\"4203\",\"draftStatus\":\"DRAFT\"}", "")
                        .get(buildingURL)
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

    @Parameters({"jwt", "CreateBuildingRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Building(String jwt, String CreateBuildingRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(CreateBuildingRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "automation_Test" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + buildingURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(buildingURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.any { it.key == 'id'}",is(true));
        capturedBuildingId = response.then().extract().path("data.id").toString();
        
    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Building_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedBuildingId);

        //Request Details
        String buildingURL1 = buildingURL + "/" + capturedBuildingId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + buildingURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(buildingURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("any { it.key == 'id'}",is(true));
        response.then().body("any { it.key == 'campusId'}",is(true));
        
    }

    @Parameters({"jwt", "UpdateBuildingRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Building_By_Id(String jwt, String UpdateBuildingRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedBuildingId);

        //Request Details
        String buildingURL1 = buildingURL + "/" + capturedBuildingId;
        String requestBody = fileUtils.getJsonTextFromFile(UpdateBuildingRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "orgAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + buildingURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(buildingURL1)
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
    public void verify_Delete_Building_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedBuildingId);

        //Request Details
        String buildingURL1 = buildingURL + "/" + capturedBuildingId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + buildingURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(buildingURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        
    }


}
