package com.phunware.core.api.tests;

import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/**
 * Created by pkovurru on 11/14/16.
 */
public class Client {

    static Logger log;

    @BeforeClass
    public void preTestSteps(){
        log = Logger.getLogger(Client.class);
    }


    @Test(priority=1)
    public void verify_Get_All_Calendars_Events_ForAnEntity(){

//        //Request Details
//        String requestURL= DataConstants.SERVICE_END_POINT+DataConstants.CALENDARS_END_POINT+DataConstants.ENTITY_ID+"/events";
//
//        //Printing Request Details
//        log.debug("REQUEST-URL:GET-"+requestURL);
//
//        //Extracting response after status code validation
//        Response response = given().header("Content-Type", "application/json").header("Token",token).get(requestURL).then().statusCode(200).extract().response();
//
//        No_Of_Calendars=response.then().extract().jsonPath().getList("$").size();
//        log.info("Number of calendars-"+No_Of_Calendars);
//
//        //printing response
//        log.info("RESPONSE:"+response.asString());
//
//        //capturing existing calendar ID
//        calendarID_Existing=response.then().extract().path("eventType.id[0]").toString();
//        log.info("Existing Calendar ID Captured-"+calendarID_Existing);
//
//        if (No_Of_Calendars!=0){
//
//            //JSON response Pay load validations
//            response.then().body("flatten().any{it.containsKey('id')}",is(true));
//            response.then().body("flatten().any{it.containsKey('title')}",is(true));
//            response.then().body("flatten().any{it.containsKey('description')}",is(true));
//            response.then().body("flatten().any{it.containsKey('starts')}",is(true));
//            response.then().body("flatten().any{it.containsKey('ends')}",is(true));
//            response.then().body("flatten().any{it.containsKey('eventType')}",is(true));
//            response.then().body("flatten().any{it.containsKey('calendarId')}",is(true));
//        }


    }
}


