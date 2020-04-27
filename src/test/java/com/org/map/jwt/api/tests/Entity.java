package com.org.map.jwt.api.tests;

import com.org.map_api.constants.MapAPI_Constants;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;


public class Entity {

    private static String serviceEndPoint = null;
    private static Integer resultcount;
    private static Logger log = Logger.getLogger(Entity.class);
    private static  String entityURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            entityURL = serviceEndPoint + MapAPI_Constants.ENTITY_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            entityURL = serviceEndPoint + MapAPI_Constants.ENTITY_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }


    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Entity_Draft(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus = DRAFT, name= test, offset=2, limit=11";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + entityURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"name\":\"test\",\"offset\":2,\"limit\":11,\"draftStatus\":\"DRAFT\"}", "")
                        .get(entityURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        
        response.then().body("totalCount", is(greaterThan(0)));
        response.then().body("resultCount", is(greaterThan(0)));
        response.then().body(("any { it.key == 'offset'}"), is(true));
        response.then().body("items.size", is(greaterThan(0)));

    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_Entity_Live(String jwt) {
        //Request Details
        String queryParameters = "DraftStatus = LIVE, name= test, offset=0, limit=11";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + entityURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"name\":\"test\",\"offset\":0,\"limit\":11,\"draftStatus\":\"DRAFT\"}", "")
                        .get(entityURL)
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

}
