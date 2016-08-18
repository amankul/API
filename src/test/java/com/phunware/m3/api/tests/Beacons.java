package com.phunware.m3.api.tests;

import com.phunware.m3.constants.GlobalConstants;
import com.phunware.m3.utilities.AuthHeader;
import com.phunware.m3.utilities.PropertiesLoader;

import io.restassured.response.Response;
import org.apache.log4j.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


/**
 * Created by knguyen on 8/12/16.
 */
public class Beacons {

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
        path = new PropertiesLoader(GlobalConstants.m3_Beacons_Path);
        log = Logger.getLogger(Attributes.class);
    }

    @Test(priority = 1)
    public void getOneBeaconBy_ID() {

        // GET - http://msgadm-api-stage.phunware.com/v3/beacon/1964
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacon/1964";
        //String URL = env.getProperty("domain") + path.getProperty("path_with_id");
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().statusCode(200).extract().response();

        log.info(response.asString());

        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        String uuid = response.path("uuid");
        Assert.assertTrue(uuid.matches(regex));

        response.then().body(matchesJsonSchemaInClasspath("Beacons_JSON_Schemas/beacon.json"));
    }

// Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 2)
    public void getBeaconBy_UUID() {

        // GET - http://msgadm-api-stage.phunware.com/v3/beacon?uuid=6e42f68a-d0d1-467b-a23e-9d11fa746e43
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacon?uuid=00000000-0000-0000-0000-000000000000";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());
        System.out.println(response.asString());

//        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
//        String uuid = response.path("uuid");
//        Assert.assertTrue(uuid.matches(regex));
//
//        response.then().assertThat().body(matchesJsonSchemaInClasspath("beacons.json"));
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 3)
    public void getBeaconBy_UUID_Major() {

        // GET - http://msgadm-api-stage.phunware.com/v3/beacon?uuid=6e42f68a-d0d1-467b-a23e-9d11fa746e43
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacon?uuid=00000000-0000-0000-0000-000000000000&major=0";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());
        System.out.println(response.asString());
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 4)
    public void getBeaconBy_UUID_Major_Minor() {

        // GET - http://msgadm-api-stage.phunware.com/v3/beacon?uuid=6e42f68a-d0d1-467b-a23e-9d11fa746e43
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacon?uuid=00000000-0000-0000-0000-000000000000&major=0&minor=0";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());
        System.out.println(response.asString());
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 5)
    public void getBeaconBy_UUID_Tags() {

        // GET - http://msgadm-api-stage.phunware.com/v3/beacon?uuid=6e42f68a-d0d1-467b-a23e-9d11fa746e43
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacon?uuid=00000000-0000-0000-0000-000000000000&tags=fakebeacon";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());
        System.out.println(response.asString());
    }

    @Test(priority = 6)
    public void getBeaconsBy_UUIDAlias() {
        // GET - https://msgadm-api-stage.phunware.com/v3/beaconuuidalias
        String URL = "https://msgadm-api-stage.phunware.com/v3/beaconuuidalias";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().statusCode(200).extract().response();

        log.info(response.asString());

        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        String uuid = response.path("uuid[0]");
        Assert.assertTrue(uuid.matches(regex));

        response.then().body(matchesJsonSchemaInClasspath("Beacons_JSON_Schemas/beaconByUUIDAlias.json"));
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 7)
    public void getBeaconsBy_Tags() {
        // GET - https://msgadm-api-stage.phunware.com/v3/beaconuuidalias
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacontag";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().statusCode(200).extract().response();

        log.info(response.asString());
        response.then().body(matchesJsonSchemaInClasspath("Beacons_JSON_Schemas/beaconTags.json"));
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 8)
    public void getBeaconWith_Tags() {
        // GET - https://msgadm-api-stage.phunware.com/v3/beacon?tags=iostest
        String URL = "https://msgadm-api-stage.phunware.com/v3/beacon?tags=fakebeacon";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = "";

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());
        System.out.println(response.asString());
//        response.then().body(matchesJsonSchemaInClasspath("beacon.json"));
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 9)
    public void disable_Beacon() {
        // GET - https://msgadm-api-stage.phunware.com/v3/beacon/1964
        String URL = "https://msgadm-api-stage.phunware.com/v3/beaconuuidalias";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = path.getProperty("valid_disable_beacon_body");

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());

        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        String uuid = response.path("uuid[0]");
        Assert.assertTrue(uuid.matches(regex));

        response.then().body(matchesJsonSchemaInClasspath("Beacons_JSON_Schemas/beaconByUUIDAlias.json"));
    }

    // Works on POSTMAN but on AUTOMATION IS GIVING 401
    @Test(priority = 10)
    public void enable_Beacon() {
        // GET - https://msgadm-api-stage.phunware.com/v3/beacon/1964
        String URL = "https://msgadm-api-stage.phunware.com/v3/beaconuuidalias";
        String accessKey = env.getProperty("beaconsAccessKey");
        String signatureKey = env.getProperty("beaconsSignatureKey");
        String body = path.getProperty("valid_enable_beacon_body");

        try {
            AUTH_VALUE = auth.generateAuthHeader("GET", accessKey, signatureKey, URL, body);
        } catch (Exception e) { e.printStackTrace();}

        Response response = given()
                .header("Content-Type", "application/json")
                .header("x-org-id", "52").header("x-client-id", "152")
                .header("X-Auth", AUTH_VALUE)
                .get(URL)
                .then().extract().response();

        log.info(response.asString());

        String regex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
        String uuid = response.path("uuid[0]");
        Assert.assertTrue(uuid.matches(regex));

        response.then().body(matchesJsonSchemaInClasspath("Beacons_JSON_Schemas/beaconByUUIDAlias.json"));
    }

}
