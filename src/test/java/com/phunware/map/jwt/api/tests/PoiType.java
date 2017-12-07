package com.phunware.map.jwt.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.FileUtils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PoiType {

    //public String dynamicValue;
    private static String serviceEndPoint = null;
    FileUtils fileUtils = new FileUtils();
    private static Logger log = Logger.getLogger(PoiType.class);
    private static  String poiURL;


    @BeforeClass
    @Parameters("env")
    public void preTestSteps(String env) {
        PropertyConfigurator.configure("/Users/sidvitahegde/IdeaProjects/qa-mass-automation/src/main/resources/log4j.properties");


        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;
            poiURL = serviceEndPoint + MapAPI_Constants.POITYPE_END_POINT;
        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
            poiURL = serviceEndPoint + MapAPI_Constants.POITYPE_END_POINT;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }
    }

    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_PoiTypes(String jwt) {
        //Printing Request Details
        log.info("REQUEST-URL:GET-" + poiURL);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .get(poiURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        

    }


    @Parameters({"jwt"})
    @Test(priority = 1)
    public void verify_Get_PoiTypes_By_Value(String jwt) {
        //Request Details
        String queryParameters = "value=" + "3";

        //Printing Request Details
        log.info("REQUEST-URL:GET-" + poiURL);
        log.info("QUERY PARAMETERS: GET-" + queryParameters);

        //Extracting response after status code validation
        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .auth().oauth2(jwt)
                        .queryParams("{\"value\":\"3\"}", "")
                        .get(poiURL)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        //printing response
        log.info("RESPONSE:" + response.asString());
        

    }


}
