package com.phunware.map.jwt.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class Segment {

    //public String dynamicValue;
    private static String capturedSegmentId;
    private static Integer resultcount;
    private static String serviceEndPoint = null;
    private static  String segmentURL;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Segment.class);


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {
        PropertyConfigurator.configure("/Users/sidvitahegde/IdeaProjects/qa-mass-automation/src/main/resources/log4j.properties");


        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            segmentURL = serviceEndPoint + MapAPI_Constants.SEGMENT_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            segmentURL = serviceEndPoint + MapAPI_Constants.SEGMENT_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }


    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Segments_By_Floor(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + segmentURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"floorId\":15621, \"draftStatus\":\"DRAFT\"}", "")
                        .get(segmentURL)
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

    @Parameters({"jwt", "CreateSegmentRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Segment(String jwt, String CreateSegmentRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(CreateSegmentRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + segmentURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(segmentURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.any { it.key == 'id'}",is(true));
        capturedSegmentId = response.then().extract().path("data.id").toString();
        Assert.assertNotNull(capturedSegmentId);
        log.info(capturedSegmentId);
        
    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Segment_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedSegmentId);

        //Request Details
        String segmentURL1 = segmentURL + capturedSegmentId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + segmentURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(segmentURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("any { it.key == 'floorId'}",is(true));
        
    }

    @Parameters({"jwt", "UpdateSegmentRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Segment_By_Id(String jwt, String UpdateSegmentRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedSegmentId);

        //Request Details
        String segmentURL1 = segmentURL + capturedSegmentId;
        String requestBody = fileUtils.getJsonTextFromFile(UpdateSegmentRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "PhunwareAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + segmentURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(segmentURL1)
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
    public void verify_Delete_Segment_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedSegmentId);

        //Request Details
        String segmentURL1 = segmentURL + capturedSegmentId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + segmentURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(segmentURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");

    }


}
