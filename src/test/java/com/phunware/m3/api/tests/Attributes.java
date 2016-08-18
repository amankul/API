package com.phunware.m3.api.tests;

import static io.restassured.RestAssured.*;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.log4j.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.phunware.m3.constants.GlobalConstants;
import com.phunware.m3.utilities.PropertiesLoader;
import com.phunware.m3.utilities.AuthHeader;

/**
 * Created by knguyen on 7/21/16.
 */
public class Attributes {

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
        path = new PropertiesLoader(GlobalConstants.m3_Attributes_Path);
        log = Logger.getLogger(Attributes.class);
    }

    @Test(priority = 1)
    public void getOneAttribute() {

        // GET - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/member_status
        String URL = env.getProperty("domain") + path.getProperty("path_with_name");
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

        response.then().body("name", is(allOf(notNullValue(), instanceOf(String.class))));
        response.then().body("allowedValues", is(allOf(notNullValue(), instanceOf(Object.class))));
        response.then().body("enabled", is(allOf(notNullValue(), instanceOf(Boolean.class))));
        response.then().body("dateCreated", is(allOf(notNullValue(), instanceOf(String.class))));
        response.then().body("attributeType", is(allOf(notNullValue(), instanceOf(String.class))));

        response.then().body("name", equalTo("member_status"));
        response.then().body("allowedValues", hasSize(4));
    }

    @Test(priority = 2)
    public void getAllAttributes() {

        // GET - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/
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

        for (int i=0 ; i<=response.then().extract().jsonPath().getList("$").size()-1;i++ ) {
            response.then().body("name["+i+"]",is(instanceOf(String.class)));
            response.then().body("allowedValues["+i+"]",is(instanceOf(Object.class)));
            response.then().body("dateCreated["+i+"]",is(instanceOf(String.class)));
            response.then().body("enabled["+i+"]",is(instanceOf(Boolean.class)));
            response.then().body("attributeType["+i+"]",is(instanceOf(String.class)));
        }
    }

    @Test(priority = 3)
    public void enableOneAttribute() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/member_status
        String URL = env.getProperty("domain") + path.getProperty("path_with_name");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_enable_attribute_body");

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
            response.then().body("enabled", is(true));
    }

    @Test(priority = 4)
    public void disableOneAttribute() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/member_status
        String URL = env.getProperty("domain") + path.getProperty("path_with_name");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_disable_attribute_body");

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
        String statusBody = response.asString();

        if (status == 200)
            response.then().body("enabled", is(false));
        else if (status == 400)
            Assert.assertEquals(statusBody, "requirement failed: Cannot disable attribute 'member_status' on an active profile");
    }

    @Test(priority = 5)
    public void postOneAttribute() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/
        String URL = env.getProperty("domain") + path.getProperty("path");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_post_attribute_body");

        try {
            AUTH_VALUE = auth.generateAuthHeader("POST", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "1").header("x-client-id", "1")
                .header("X-Auth", AUTH_VALUE)
                .request().body(body)
                .post(URL)
                .then().extract().response();

        log.info(response.asString());

        int status = response.getStatusCode();
        String statusBody = response.asString();

        if (status == 200)
            Assert.assertEquals(status, 200);
        else if (status == 400)
            assertThat(statusBody, containsString("Duplicate entry"));
    }

    @Test(priority = 6)
    public void addOneValueToAttribute() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/dang
        String URL = env.getProperty("domain") + path.getProperty("path_with_name_to_add_values_to_attribute_body");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_add_one_value_to_attribute_body");

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
        String statusBody = response.asString();

        if (status == 200)
            response.then().body("allowedValues", hasItem("alot"));
        else if (status == 400)
            assertThat(statusBody, containsString("Cannot remove from allowedValues"));
    }

    @Test(priority = 7)
    public void addMultipleValuesToAttribute() {

        // PUT - http://msgadm-api-stage.phunware.com/v3/attribute-metadata/dang
        String URL = env.getProperty("domain") + path.getProperty("path_with_name_to_add_values_to_attribute_body");
        String accessKey = env.getProperty("accessKey");
        String signatureKey = env.getProperty("signatureKey");
        String body = path.getProperty("valid_add_values_to_attribute_body");

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
        String statusBody = response.asString();

        if (status == 200)
            response.then().body("allowedValues", hasItems("alot", "bro"));
        else if (status == 400)
            assertThat(statusBody, containsString("Cannot remove from allowedValues"));
    }

//    @Test(priority = 10)
//    public void getCMS() {
//
//        String URL = "http://cms-api.phunware.com/v1.0/content?%7B%22containerId%22%3A%2256d9c8db7baa1b1564001fa1%22%2C%22structureId%22%3A%2215551%22%2C%22depth%22%3A2%2C%22limit%22%3A100%7D";
//        //String URL = "http://cms-api.phunware.com/v1.0/content?{"containerId":"56d9c8db7baa1b1564001fa1","structureId":"15551","depth":2,"limit":100}";
//        //String URL = "http://cms-api.phunware.com/v1.0/content?{containerId:56d9c8db7baa1b1564001fa1,structureId:15551,depth:2,limit:100}";
//
//        String accessKey = env.getProperty("accessKey");
//        String signatureKey = env.getProperty("signatureKey");
//        String body = "";
//
//        try {
//            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
//            System.out.println(AUTH_VALUE);
//        } catch (Exception e) { e.printStackTrace();}
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                //.header("x-org-id", "1").header("x-client-id", "1")
//                .header("X-Auth", AUTH_VALUE)
//                .get(URL)
//                .then().statusCode(200).extract().response();
//
//        log.info(response.asString());
//        System.out.println(response);
//    }

}

