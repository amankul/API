package com.phunware.m3.api.tests;

import com.phunware.me_api.constants.MeAPI_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.AuthHeader;

import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.InterruptedIOException;
import java.util.UUID;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Created by VinayKarumuri on 5/15/17.
 */
public class Beacons {

  public static String capturedNewBeaaconId;
  public static String uuid = null;
  public static String serviceEndPoint = null;
  static Logger log;
  public String xAuth = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();

  @BeforeSuite
  @Parameters("env")
  public void setEnv(String env) {
    if (env.equalsIgnoreCase("PROD")) {
      serviceEndPoint = MeAPI_Constants.SERVICE_ENT_POINT_PROD;
    } else if (env.equalsIgnoreCase("STAGE")) {
      serviceEndPoint = MeAPI_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.info("Environment is not set properly. Please check your testng xml file");
    }
  }

  @BeforeClass
  @Parameters({"clientId_android_access_key", "clientId_android_signature_key"})
  public void preTestSteps() {
    log = Logger.getLogger(Beacons.class);
  }


  @Parameters({"beaconId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 1)
  public void verify_Get_Beacon(String beaconId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + beaconId;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      e.printStackTrace();
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
    response.then().body("id", is(Integer.parseInt(beaconId)));
    response.then().body("orgId", is(Integer.parseInt(orgId)));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'uuid'}"), is(true));
    response.then().body(("any { it.key == 'majorMinorDisplayType'}"), is(true));
    response.then().body(("any { it.key == 'tags'}"), is(true));
    response.then().body(("any { it.key == 'interval'}"), is(true));
    response.then().body(("any { it.key == 'txPower'}"), is(true));
    response.then().body(("any { it.key == 'major'}"), is(true));
    response.then().body(("any { it.key == 'minor'}"), is(true));
    response.then().body(("any { it.key == 'summary'}"), is(true));
    response.then().body(("any { it.key == 'modifiedById'}"), is(true));
    response.then().body(("any { it.key == 'createdById'}"), is(true));
  }

  @Parameters({"beaconId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 2)
  public void verify_Get_Beacon_InvalidId(String beaconId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + "000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      e.printStackTrace();
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


  @Parameters({"beaconUuid", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 3)
  public void verify_Get_Beacons_By_Uuid(String beaconUuid, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParam("uuid", beaconUuid)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    response.then().body("uuid", everyItem(is(beaconUuid)));
  }


  @Parameters({"beaconUuid", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 4)
  public void verify_Get_Beacons_By_Invalid_Uuid(String beaconUuid, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + "00000000-0000-0000-0000-0000000000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParam("uuid", "00000000-0000-0000-0000-0000000000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");
  }

  @Parameters({"beaconUuid", "beaconMajor", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 5)
  public void verify_Get_Beacons_By_Uuid_And_Major(String beaconUuid, String beaconMajor, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + beaconMajor;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", beaconMajor)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    response.then().body("uuid", everyItem(is(beaconUuid)));
    response.then().body("major", everyItem(is(Integer.parseInt(beaconMajor))));
  }

  @Parameters({"beaconUuid", "beaconMajor", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 6)
  public void verify_Get_Beacons_By_Uuid_And_InvalidMajor(String beaconUuid, String beaconMajor, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + "0000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", "0000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");
  }


  @Parameters({"beaconUuid", "beaconMajor", "beaconMinor", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 7)
  public void verify_Get_Beacons_By_Uuid_Major_And_Minor(String beaconUuid, String beaconMajor, String beaconMinor, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + beaconMajor + "&minor=" + beaconMinor;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", beaconMajor, "minor", beaconMinor)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    response.then().body("uuid", is(beaconUuid));
    response.then().body("major", is(Integer.parseInt(beaconMajor)));
    response.then().body("major", is(Integer.parseInt(beaconMajor)));
  }

  @Parameters({"beaconUuid", "beaconMajor", "beaconMinor", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 8)
  public void verify_Get_Beacons_By_Uuid_Major_And_InvalidMinor(String beaconUuid, String beaconMajor, String beaconMinor, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + beaconMajor + "&minor=" + "0000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", beaconMajor, "minor", "0000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");

  }

  @Parameters({"beaconUuid", "beaconTags", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 9)
  public void verify_Get_Beacons_By_Uuid_And_Tags(String beaconUuid, String beaconTags, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&tags=" + beaconTags;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "tags", beaconTags)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    response.then().body("uuid", everyItem(is(beaconUuid)));
    response.then().body("tags", everyItem(hasItem(beaconTags)));
  }

  @Parameters({"beaconUuid", "beaconTags", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 10)
  public void verify_Get_Beacons_By_Uuid_And_InvalidTags(String beaconUuid, String beaconTags, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&tags=" + "000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "tags", "000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");
  }

  @Parameters({"beaconUuidAlias", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 11)
  public void verify_Get_UUID_By_Alias(String beaconUuidAlias, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;
    //QueryParameters
    String queryParameters = "uuidalias=" + beaconUuidAlias;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuidalias", beaconUuidAlias)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    response.then().body(("any { it.key == 'uuid'}"), is(true));
    response.then().body("uuidAlias", is(beaconUuidAlias));

  }


  @Parameters({"beaconUuidAlias", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 12)
  public void verify_Get_UUID_By_InvalidAlias(String beaconUuidAlias, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;
    //QueryParameters
    String queryParameters = "uuidalias=" + "0000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuidalias", "0000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");
  }


  @Parameters({"beaconTags", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 13)
  public void verify_Get_Beacons_By_Tags(String beaconTags, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "tags=" + beaconTags;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("tags", beaconTags)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    response.then().body("tags", everyItem(hasItem(beaconTags)));
  }

  @Parameters({"beaconTags", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 14)
  public void verify_Get_Beacons_By_InvalidTags(String beaconTags, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    //QueryParameters
    String queryParameters = "tags=" + "000";


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, queryParameters);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("tags", "000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validation
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");
  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 15)
  public void verify_Get_Beacon_Tags(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_TAGS_END_POINT;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      e.printStackTrace();
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

    //JSON Response Validation
    response.then().body("size()", is(greaterThan(0)));
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 16)
  public void verify_Get_Uuids_By_Org(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      e.printStackTrace();
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
    response.then().body("size()", is(greaterThan(0)));

    //JSON Response Validation
    response.then().body("any { it.key = 'uuid'}", is(true));
    response.then().body("any { it.key = 'uuidAlias'}", is(true));
    response.then().body("size()", is(greaterThan(0)));

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 17)
  public void verify_Create_Beacon(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString());
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    response.then().body(("any { it.key == 'id'}"), is(true));
    response.then().body("orgId", is(Integer.parseInt(orgId)));
    response.then().body("uuid", is(uuid));
    response.then().body(("any { it.key == 'minor'}"), is(true));
    response.then().body(("any { it.key == 'major'}"), is(true));
    response.then().body(("any { it.key == 'txPower'}"), is(true));
    response.then().body(("any { it.key == 'interval'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'majorMinorDisplayType'}"), is(true));
    response.then().body(("any { it.key == 'uuid'}"), is(true));
    response.then().body(("any { it.key == 'tags'}"), is(true));

    capturedNewBeaaconId = response.then().extract().path("id").toString();

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postUuidAliasRequestBodyPath"})
  @Test(priority = 18)
  public void verify_Create_Uuid_Alias(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postUuidAliasRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;

    File file = new File(postUuidAliasRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    log.info("REQUEST-BODY -2:" + requestBodyJSONObject.toString());
    log.info("UUID" + uuid);
    requestBodyJSONObject.put("uuid", uuid);
    log.info("REQUEST-BODY -1:" + requestBodyJSONObject.toString());


    String uuidAlias = "qa" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("uuidAlias", uuidAlias);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    response.then().body("uuid", is(uuid));
    response.then().body("uuidAlias", is(uuidAlias));

  }


  //here Invalid Means there is no beacon exist with this UUID.
  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postUuidAliasRequestBodyPath"})
  @Test(priority = 19)
  public void verify_Create_Invalid_Uuid_Alias(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postUuidAliasRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;

    File file = new File(postUuidAliasRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("uuid", HelperMethods.getUUIDAsString());


    String uuidAlias = "qa" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("uuidAlias", uuidAlias);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    Assert.assertEquals(response.asString(), "requirement failed: A beacon with this UUID doesn't exist yet");

  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postUuidAliasRequestBodyPath"})
  @Test(priority = 20)
  public void verify_Update_Uuid_Alias(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postUuidAliasRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT_1 + uuid;

    File file = new File(postUuidAliasRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String uuidAlias = "qa" + HelperMethods.getDateAsString() + "Updated";
    requestBodyJSONObject.put("uuidAlias", uuidAlias);
    requestBodyJSONObject.put("uuid", uuid);


    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("PUT", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    response.then().body("uuid", is(uuid));
    response.then().body("uuidAlias", is(uuidAlias));
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 21)
  public void verify_Update_Beacon(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + capturedNewBeaaconId;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString() + "Updated");
    requestBodyJSONObject.put("tags", tags);
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);
    requestBodyJSONObject.put("id", Integer.parseInt(capturedNewBeaaconId));

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("PUT", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    response.then().body(("any { it.key == 'id'}"), is(true));
    response.then().body("orgId", is(Integer.parseInt(orgId)));
    response.then().body("uuid", is(uuid));
    response.then().body(("any { it.key == 'minor'}"), is(true));
    response.then().body(("any { it.key == 'major'}"), is(true));
    response.then().body(("any { it.key == 'txPower'}"), is(true));
    response.then().body(("any { it.key == 'interval'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'majorMinorDisplayType'}"), is(true));
    response.then().body(("any { it.key == 'uuid'}"), is(true));
    response.then().body(("any { it.key == 'tags'}"), is(true));

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 22)
  public void verify_Create_Beacon_No_Major(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString());
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);
    requestBodyJSONObject.remove("major");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    Assert.assertEquals(response.asString(), "The request content was malformed:\n" +
        "Required field [major] is missing; please provide field and value in your request");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 23)
  public void verify_Create_Beacon_No_Minor(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString());
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);
    requestBodyJSONObject.remove("minor");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    Assert.assertEquals(response.asString(), "The request content was malformed:\n" +
        "Required field [minor] is missing; please provide field and value in your request");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 24)
  public void verify_Create_Beacon_No_MajorMinorDisplayType(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString());
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);
    requestBodyJSONObject.remove("majorMinorDisplayType");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    Assert.assertEquals(response.asString(), "The request content was malformed:\n" +
        "Required field [majorMinorDisplayType] is missing; please provide field and value in your request");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 25)
  public void verify_Create_Beacon_No_Tags(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    response.then().body("id", is(greaterThan(0)));
  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 26)
  public void verify_Create_Beacon_No_Interval(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString());
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);
    requestBodyJSONObject.remove("interval");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    Assert.assertEquals(response.asString(), "The request content was malformed:\n" +
        "Required field [interval] is missing; please provide field and value in your request");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 27)
  public void verify_Create_Beacon_No_TxPower(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    File file = new File(postBeaconRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    tags.put("automation");
    tags.put("qa" + HelperMethods.getDateAsString());
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);
    requestBodyJSONObject.remove("txPower");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Printing xAuth
    log.info("X-AUTH " + xAuth);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId).header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON Response Validations
    Assert.assertEquals(response.asString(), "The request content was malformed:\n" +
        "Required field [txPower] is missing; please provide field and value in your request");

  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postBeaconRequestBodyPath"})
  @Test(priority = 28)
  public void verify_Delete_Beacon(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postBeaconRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + capturedNewBeaaconId;

    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("DELETE", clientId_android_access_key, clientId_android_signature_key, requestURL, "");

      //Printing xAuth
      log.info("X-AUTH " + xAuth);

      //Extracting response after status code validation
      Response response =
          given()
              .header("Content-Type", "application/json")
              .header("x-org-id", orgId).header("x-client-id", clientId)
              .header("X-Auth", xAuth)
              .delete(requestURL)
              .then()
              .statusCode(200)
              .extract()
              .response();

      log.info("RESPONSE " + response.asString());

    } catch (Exception e) {
      log.info("SKipping HttpResponseException & ");
    }
  }


}
