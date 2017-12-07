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

public class Resource {

    private static String capturedResourceId;
    private static Integer resultcount;
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(Resource.class);
    private static  String resourceURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {
        PropertyConfigurator.configure("/Users/sidvitahegde/IdeaProjects/qa-mass-automation/src/main/resources/log4j.properties");

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            resourceURL = serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            resourceURL = serviceEndPoint + MapAPI_Constants.RESOURCE_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Live_Resources_By_Floor(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "Live";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + resourceURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"floorId\":\"15621\"}", "")
                        .get(resourceURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        log.info("RESPONSE CODE:" + response.then().statusCode(200));

    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Draft_Resources_By_Floor(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus=" + "DRAFT";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + resourceURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"floorId\":\"15621\",\"draftStatus\":\"DRAFT\"}", "")
                        .get(resourceURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"jwt", "CreateResourceRequestBodyPath"})
    @Test(priority = 1)
    public void verify_Create_Resource(String jwt, String CreateResourceRequestBodyPath) throws IOException {
        //Request Details
        String requestBody = fileUtils.getJsonTextFromFile(CreateResourceRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "automation_Test" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("title", name);
        
        //Printing Request Details
        log.info("REQUEST-URL:POST-" + resourceURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .post(resourceURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("data.any { it.key == 'id'}",is(true));
        capturedResourceId = response.then().extract().path("data.id").toString();
        
    }

    @Parameters({"jwt"})
    @Test(priority = 2)
    public void verify_Get_Resource_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedResourceId);

        //Request Details
        String resourceURL1 = resourceURL + "/" + capturedResourceId;

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + resourceURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"draftStatus\":\"DRAFT\"}", "")
                        .get(resourceURL1)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        response.then().body("any { it.key == 'id'}",is(true));
        response.then().body("any { it.key == 'floorId'}",is(true));
        
    }

    @Parameters({"jwt", "UpdateResourceRequestBodyPath"})
    @Test(priority = 2)
    public void verify_Update_Resource_By_Id(String jwt, String UpdateResourceRequestBodyPath) throws IOException {
        Assert.assertNotNull(capturedResourceId);

        //Request Details
        String resourceURL1 = resourceURL + "/" + capturedResourceId;
        String requestBody = fileUtils.getJsonTextFromFile(UpdateResourceRequestBodyPath);
        JSONObject requestBodyJSONObject = new JSONObject(requestBody);
        String name = "PhunwareAustin" + HelperMethods.getDateAsString();
        requestBodyJSONObject.put("title", name);

        //Printing Request Details
        log.info("REQUEST-URL:PUT-" + resourceURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .body(requestBodyJSONObject.toString())
                        .put(resourceURL1)
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
    public void verify_Delete_Resource_By_Id(String jwt) throws IOException {
        Assert.assertNotNull(capturedResourceId);

        //Request Details
        String resourceURL1 = resourceURL + "/" + capturedResourceId;


        //Printing Request Details
        log.info("REQUEST-URL:GET-" + resourceURL1);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .delete(resourceURL1)
                        .then()
                        .statusCode(204)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        Assert.assertEquals(response.asString(), "");
        
    }


}
