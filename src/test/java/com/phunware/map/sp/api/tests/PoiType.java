package com.phunware.map.sp.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.AuthHeader;
import com.phunware.utility.FileUtils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class PoiType {

    private String xAuth = null;
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

    @Parameters({"poitypeId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 1)
    public void verify_Get_PoiType(String poitypeId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.POITYPE_END_POINT_v1 + poitypeId;


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

        //JSON response Pay load validations
        response.then().body("id", is(Integer.parseInt(poitypeId)));
        response.then().body(("any { it.key == 'description'}"), is(true));

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 2)
    public void verify_Get_PoiType_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.POITYPE_END_POINT_v1 + "100";


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
                        .statusCode(404)
                        .extract()
                        .response();
        //printing response
        log.info("RESPONSE:" + response.asString());

    }

    @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
    @Test(priority = 1)
    public void verify_Get_PoiType_ByValue(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

        //Request Details
        String requestURL =
                serviceEndPoint + MapAPI_Constants.POITYPE_END_POINT_v1_2;


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

}
