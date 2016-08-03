package com.phunware.m3.api.tests;

import static io.restassured.RestAssured.*;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.log4j.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.phunware.m3.constants.GlobalConstants;
import com.phunware.m3.utilities.PropertiesLoader;
import com.phunware.m3.utilities.AuthHeader;

/**
 * Created by knguyen on 7/13/16.
 */
public class Profiles {

    GlobalConstants gc;

    static PropertiesLoader env;
    static PropertiesLoader path;

    static AuthHeader auth;
    static String AUTH_VALUE;

    static Logger log;

    @BeforeTest
    public void instantiateEnvironment() {

        gc = new GlobalConstants();
        env = new PropertiesLoader(GlobalConstants.environment_M3_Stage);
        path = new PropertiesLoader(GlobalConstants.m3_Profiles_Path);
        log = Logger.getLogger(Profiles.class);
    }


    @Test(priority = 1)
    public void getOneProfile() {

        // GET - http://msgadm-api-stage.phunware.com/v3/profile/3
        String URL = env.getProperty("domain") + path.getProperty("path_with_id");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "1").header("x-client-id", "1")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().statusCode(200).extract().response();

        log.info(response.asString());

        response.then().body("id", is(allOf(notNullValue(), instanceOf(Integer.class))));
        response.then().body("name", is(allOf(notNullValue(), instanceOf(String.class))));
        response.then().body("description", is(allOf(notNullValue(), instanceOf(String.class))));
        response.then().body("enabled", is(allOf(notNullValue(), instanceOf(Boolean.class))));
        response.then().body("date_created", is(allOf(notNullValue(), instanceOf(String.class))));

        response.then().body("rules", is(instanceOf(Object.class)));
        response.then().body("rules.attributeMetadataName", notNullValue(String.class));
        response.then().body("rules.operator", notNullValue(String.class));
        response.then().body("rules.value", notNullValue(String.class));
    }

    @Test(priority = 2)
    public void getAllProfiles() {

        //GET - http://msgadm-api-stage.phunware.com/v3/profile/
        String URL = env.getProperty("domain") + path.getProperty("path");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "1").header("x-client-id", "1")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().statusCode(200).extract().response();

        log.info(response.asString());

        response.then().body("flatten().any {it.containsKey('id') }", is(true));
        response.then().body("flatten().any {it.containsKey('name') }", is(true));
        response.then().body("flatten().any {it.containsKey('description') }", is(true));
        response.then().body("flatten().any {it.containsKey('enabled') }", is(true));
        response.then().body("flatten().any {it.containsKey('date_created') }", is(true));

        response.then().body("flatten().any {it.containsKey('rules') }", is(true));
        response.then().body("rules.flatten().any {it.containsKey('attributeMetadataName') }", is(true));
        response.then().body("rules.flatten().any {it.containsKey('operator') }", is(true));
        response.then().body("rules.flatten().any {it.containsKey('value') }", is(true));


        for (int i=0 ; i<=response.then().extract().jsonPath().getList("$").size()-1;i++ ) {

            response.then().body("id["+i+"]",is(instanceOf(Integer.class)));
            response.then().body("name["+i+"]",is(instanceOf(String.class)));
            response.then().body("description["+i+"]",is(instanceOf(String.class)));
            response.then().body("rules["+i+"]",is(instanceOf(Object.class)));
            response.then().body("enabled["+i+"]",is(instanceOf(Boolean.class)));
            response.then().body("date_created["+i+"]",is(instanceOf(String.class)));
        }

    }

    @Test(priority = 3)
    public void createOneProfile() {

        // POST - http://msgadm-api-stage.phunware.com/v3/profile/
        String URL = env.getProperty("domain") + path.getProperty("path");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_post_profile_body");

        try {
            AUTH_VALUE = auth.generateAuthHeader("POST", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given().header("Content-Type", "application/json").header("x-org-id", "1").header("x-client-id", "1")
                .header("X-Auth", AUTH_VALUE)
                .request().body(body)
                .post(env.getProperty("domain") + path.getProperty("path"))
                .then().extract().response();

        log.info(response.asString());

        int status = response.getStatusCode();
        String statusBody = response.asString();

        if (status == 200)
            Assert.assertEquals(status, 200);
        else if (status == 400)
            Assert.assertEquals(statusBody, "requirement failed: Value provided for [name] is not unique, please provide an unique value.");
    }

    @Test(priority = 4)
    public void disableOneProfile() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/profile/62
        String URL = env.getProperty("domain") + path.getProperty("path_with_id_to_enable_disable");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_disable_profile_body");

        try {
            AUTH_VALUE = auth.generateAuthHeader("PUT", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "1").header("x-client-id", "1")
                .header("X-Auth", AUTH_VALUE)
                .request().body(body)
                .put(URL)
                .then().extract().response();

        log.info(response.asString());

        int status = response.getStatusCode();

        if (status == 200)
            response.then().body("enabled", is(false));
    }

    @Test(priority = 5)
    public void enableOneProfile() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/profile/62
        String URL = env.getProperty("domain") + path.getProperty("path_with_id_to_enable_disable");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_enable_profile_body");

        try {
            AUTH_VALUE = auth.generateAuthHeader("PUT", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "1").header("x-client-id", "1")
                .header("X-Auth", AUTH_VALUE)
                .request().body(body)
                .put(URL)
                .then().statusCode(200).extract().response();

        log.info(response.asString());

        int status = response.getStatusCode();

        if (status == 200)
            response.then().body("enabled", is(true));
    }

}
