package com.phunware.map.jwt.api.tests;

import com.phunware.map_api.constants.MapAPI_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.JWTUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/* Created by Amit Kulkarni on 02/08/2018 */

public class Venues {
    private static String serviceEndPoint = null;
    private static Integer resultcount,clientIds;
    private static Logger log = Logger.getLogger(MappingV3.class);
    private static String venueUrl, jwt;

    FileUtils fileUtils = new FileUtils();


    @BeforeClass
    @Parameters({"env", "orgId"})
    private void preTestSteps(String env, int orgId) {

        if ("PROD".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_ENT_POINT_PROD;

        } else if ("STAGE".equalsIgnoreCase(env)) {
            serviceEndPoint = MapAPI_Constants.SERVICE_END_POINT_STAGE;
        } else {
            log.error("Environment is not set properly. Please check your testng xml file");
            Assert.fail("Environment is not set properly. Please check your testng xml file");
        }

        jwt = JWTUtils.getJWTForAdmin(env, orgId);
        Assert.assertNotNull(jwt);

        venueUrl = serviceEndPoint + MapAPI_Constants.VENUE_END_POINT_V3;
    }

    /* 1
     * Verify GET venue collection without any params yeilds LIVE venues
     */

@Test()
    public void verify_Get_Venue_Collection_Live() throws IOException {


        // Printing Request Details
        log.info("REQUEST-URL:GET- " + venueUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                 //       .queryParam("draftStatus", "DRAFT")
                 //       .queryParam("isActive", "false")
                 //       .queryParam("clientId", "112")
                        .get(venueUrl)
                        .then()
                        .extract()
                        .response();

 //   String sqlQuery = "select count(*) from venues where name=" + "\"" + randomvalue + "\"";
 //   dbResult = MySql.query_Post_connection_To_MySQL_Via_JumpServer(sqlQuery, serviceEndPoint);

        // printing response

    resultcount = response.then().extract().path("resultCount");
    log.info("RESPONSE:" + response.asString());
    response.then().statusCode(HttpStatus.SC_OK);
    response.then().body("totalCount", Matchers.is(greaterThan(0)));
    response.then().body("resultCount", Matchers.is(greaterThan(0)));
    response.then().body(("any { it.key == 'offset'}"), Matchers.is(true));
    response.then().body("items.size", Matchers.is(resultcount));
log.info("TOTAL VENUE COUNT :" + resultcount );
    response.then().body("items[0].draftStatus", Matchers.equalTo("LIVE"));
    response.then().body("items.flatten().any {it.containsKey('orgIds') }", Matchers.equalTo(true));



}

    @Test()
    public void verify_Get_Venue_Collection_Params1() throws IOException {


        // Printing Request Details
        log.info("REQUEST-URL:GET- " + venueUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                               .queryParam("draftStatus", "DRAFT")
                               .queryParam("isActive", "true")
                               .queryParam("clientId", "685")
                        .queryParam("filter", "draftStatus,mseUdi,isActive")

                        .get(venueUrl)
                        .then()
                        .extract()
                        .response();


        // printing response

        resultcount = response.then().extract().path("resultCount");
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("totalCount", Matchers.is(greaterThan(0)));
        response.then().body("resultCount", Matchers.is(greaterThan(0)));
        response.then().body("items.size", Matchers.is(resultcount));
        log.info("TOTAL VENUE COUNT :" + resultcount );
        response.then().body("items[0].isActive", Matchers.equalTo(true));
        response.then().body("items[0].draftStatus", Matchers.equalTo("DRAFT"));
        response.then().body("items.flatten().any {it.containsKey('mseUdi') }", Matchers.equalTo(true));
        response.then().body("items.flatten().any {it.containsKey('supportsGeographicCoordinates') }", Matchers.equalTo(false));



    }


    @Test()
    @Parameters({"orgId"})
    public void verify_Get_Venue_Collection_Params2(String orgId) throws IOException {


        // Printing Request Details
        log.info("REQUEST-URL:GET- " + venueUrl);

        // Extracting response after status code validation
        Response response =
                given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .auth()
                        .oauth2(jwt)
                        .queryParam("draftStatus", "LIVE")
                        .queryParam("isActive", "false")
                        .queryParam("clientId", "327")
                        .queryParam("filter", "name,orgIds,appId,createdAt,isActive")
                        .queryParam("limit", "5")
                        .queryParam("offset", "2")
                        .get(venueUrl)
                        .then()
                        .extract()
                        .response();


        // printing response

        resultcount = response.then().extract().path("resultCount");
        log.info("RESPONSE:" + response.asString());
        response.then().statusCode(HttpStatus.SC_OK);
        response.then().body("totalCount", Matchers.is(greaterThan(0)));
        response.then().body("resultCount", Matchers.is(greaterThan(0)));
        response.then().body("resultCount", Matchers.is(lessThanOrEqualTo(5)));
        response.then().body("offset", Matchers.equalTo(2));

        response.then().body("items.size", Matchers.is(resultcount));
        log.info("TOTAL VENUE COUNT :" + resultcount );
        response.then().body("items[0].isActive", Matchers.equalTo(false));
        response.then().body("items[0].orgIds", Matchers.equalTo(orgId));
        response.then().body("items.flatten().any {it.containsKey('name') }", Matchers.equalTo(true));
        response.then().body("items.flatten().any {it.containsKey('mseUdi') }", Matchers.equalTo(false));



    }

}