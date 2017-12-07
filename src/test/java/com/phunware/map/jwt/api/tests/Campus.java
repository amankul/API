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

public class Campus {
    //public String dynamicValue;;
    private static String capturedCampusId;
    private static Integer resultcount;
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Campus.class);
    private static  String campusURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {
        PropertyConfigurator.configure("/Users/sidvitahegde/IdeaProjects/qa-mass-automation/src/main/resources/log4j.properties");


        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            campusURL = serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            campusURL = serviceEndPoint + MapAPI_Constants.CAMPUS_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }


    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Live_Campuses_By_Venue(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "Live";


        //Printing Request Details
        log.info("REQUEST-URL:GET-" + campusURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"venueGuid\":\"8d6abf08-263b-46ba-8efd-8e004229aee3\"}", "")
                        .get(campusURL)
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
    public void verify_Get_Draft_Campuses_By_Venue(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + campusURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"venueGuid\":\"fceb0175-d585-45b4-a984-4d79aec9b9ef\",\"draftStatus\":\"DRAFT\"}", "")
                        .get(campusURL)
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

    @Parameters({"jwt", "CreateCampusRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Campus(String jwt, String CreateCampusRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(CreateCampusRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "automation_Test" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:POST-" + campusURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(campusURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.any { it.key == 'id'}",is(true));
        capturedCampusId = response.then().extract().path("data.id").toString();
        
    }

    @Parameters({"jwt"})
    @Test(priority = 4)
    public void verify_Get_Campus_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedCampusId);

        //Request Details
        String campusURL1 = campusURL + "/" + capturedCampusId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + campusURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(campusURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("any { it.key == 'id'}",is(true));
        response.then().body("any { it.key == 'venueGuid'}",is(true));
        
    }

    @Parameters({"jwt", "UpdateCampusRequestBodyPath"})
    @Test(priority = 5)
    public void verify_Update_Campus_By_Id(String jwt, String UpdateCampusRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedCampusId);

        //Request Details
        String campusURL1 = campusURL + "/" + capturedCampusId;

        String requestBody = fileUtils.getJsonTextFromFile(UpdateCampusRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "PhunwareAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("name", name);

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + campusURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(campusURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        
    }

    @Parameters({"jwt"})
    @Test(priority = 6)
    public void verify_Delete_Campus_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedCampusId);

        //Request Details
        String campusURL1 = campusURL + "/" + capturedCampusId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + campusURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(campusURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        

    }


}
