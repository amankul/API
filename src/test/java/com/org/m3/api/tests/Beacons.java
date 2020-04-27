package com.org.m3.api.tests;

import com.org.me_api.constants.MeAPI_Constants;
import com.org.utility.AuthHeader;
import com.org.utility.FileUtils;
import com.org.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class Beacons {

  private static String capturedNewBeaaconId;
  private static String uuid = null;
  private static String serviceEndPoint = null;
  private static Logger log = Logger.getLogger(Beacons.class);
  private static String clientId_android_access_key = null;
  private static String clientId_android_signature_key = null;
  private static String orgId = null;
  private static String clientId = null;
  private static String beaconId = null;
  private static String beaconUuid = null;
  private static String beaconMajor = null;
  private static String beaconMinor = null;
  private static String beaconTags = null;
  private static String beaconUuidAlias = null;
  private static String postBeaconRequestBodyPath = null;
  private static String postUuidAliasRequestBodyPath = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();
  private String xAuth = null;

  @BeforeClass
  @Parameters({"env", "beaconId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "beaconUuid", "beaconMajor", "beaconMinor", "beaconTags", "beaconUuidAlias", "postBeaconRequestBodyPath", "postUuidAliasRequestBodyPath"})
  public void preTestSteps(String env,
                           String beaconId,
                           String clientId_android_access_key,
                           String clientId_android_signature_key,
                           String orgId,
                           String clientId,
                           String beaconUuid,
                           String beaconMajor,
                           String beaconMinor,
                           String beaconTags,
                           String beaconUuidAlias,
                           String postBeaconRequestBodyPath,
                           String postUuidAliasRequestBodyPath) {

    this.beaconId = beaconId;
    this.clientId_android_access_key = clientId_android_access_key;
    this.clientId_android_signature_key = clientId_android_signature_key;
    this.orgId = orgId;
    this.clientId = clientId;
    this.beaconUuid = beaconUuid;
    this.beaconMajor = beaconMajor;
    this.beaconMinor = beaconMinor;
    this.beaconTags = beaconTags;
    this.beaconUuidAlias = beaconUuidAlias;
    this.postBeaconRequestBodyPath = postBeaconRequestBodyPath;
    this.postUuidAliasRequestBodyPath = postUuidAliasRequestBodyPath;


    if ("PROD".equalsIgnoreCase(env)) {
      serviceEndPoint = MeAPI_Constants.SERVICE_ENT_POINT_PROD;
    } else if ("STAGE".equalsIgnoreCase(env)) {
      serviceEndPoint = MeAPI_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
      Assert.fail("Environment is not set properly. Please check your testng xml file");
    }
  }


  @Test(priority = 1)
  public void verify_Get_Beacon() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + beaconId;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON response Pay load validations
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


  @Test(priority = 2)
  public void verify_Get_Beacon_InvalidId() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + "000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());
  }


  @Test(priority = 3)
  public void verify_Get_Beacons_By_Uuid() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + beaconUuid;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParam("uuid", beaconUuid)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body("uuid", everyItem(is(beaconUuid)));
  }


  @Test(priority = 4)
  public void verify_Get_Beacons_By_Invalid_Uuid() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + "00000000-0000-0000-0000-0000000000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParam("uuid", "00000000-0000-0000-0000-0000000000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }


  @Test(priority = 5)
  public void verify_Get_Beacons_By_Uuid_And_Major() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + beaconMajor;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", beaconMajor)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body("uuid", everyItem(is(beaconUuid)));
    response.then().body("major", everyItem(is(Integer.parseInt(beaconMajor))));
  }

  @Test(priority = 6)
  public void verify_Get_Beacons_By_Uuid_And_InvalidMajor() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + "0000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", "0000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }


  @Test(priority = 7)
  public void verify_Get_Beacons_By_Uuid_Major_And_Minor() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters =
        "uuid=" + beaconUuid + "&major=" + beaconMajor + "&minor=" + beaconMinor;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", beaconMajor, "minor", beaconMinor)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body("uuid", is(beaconUuid));
    response.then().body("major", is(Integer.parseInt(beaconMajor)));
    response.then().body("major", is(Integer.parseInt(beaconMajor)));
  }


  @Test(priority = 8)
  public void verify_Get_Beacons_By_Uuid_Major_And_InvalidMinor() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&major=" + beaconMajor + "&minor=" + "0000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "major", beaconMajor, "minor", "0000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }

  @Test(priority = 9)
  public void verify_Get_Beacons_By_Uuid_And_Tags() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&tags=" + beaconTags;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "tags", beaconTags)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body("uuid", everyItem(is(beaconUuid)));
    response.then().body("tags", everyItem(hasItem(beaconTags)));
  }


  @Test(priority = 10)
  public void verify_Get_Beacons_By_Uuid_And_InvalidTags() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "uuid=" + beaconUuid + "&tags=" + "000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuid", beaconUuid, "tags", "000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }


  @Test(priority = 11)
  public void verify_Get_UUID_By_Alias() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;
    // QueryParameters
    String queryParameters = "uuidalias=" + beaconUuidAlias;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuidalias", beaconUuidAlias)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body(("any { it.key == 'uuid'}"), is(true));
    response.then().body("uuidAlias", is(beaconUuidAlias));
  }


  @Test(priority = 12)
  public void verify_Get_UUID_By_InvalidAlias() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;
    // QueryParameters
    String queryParameters = "uuidalias=" + "0000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("uuidalias", "0000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }


  @Test(priority = 13)
  public void verify_Get_Beacons_By_Tags() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "tags=" + beaconTags;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("tags", beaconTags)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body("tags", everyItem(hasItem(beaconTags)));
  }

  @Test(priority = 14)
  public void verify_Get_Beacons_By_InvalidTags() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;
    // QueryParameters
    String queryParameters = "tags=" + "000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMETERS" + queryParameters);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              queryParameters);
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .queryParams("tags", "000")
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }

  @Test(priority = 15)
  public void verify_Get_Beacon_Tags() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_TAGS_END_POINT;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body("size()", is(greaterThan(0)));
  }

  @Test(priority = 16)
  public void verify_Get_Uuids_By_Org() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());
    response.then().body("size()", is(greaterThan(0)));

    // JSON Response Validation
    response.then().body("any { it.key = 'uuid'}", is(true));
    response.then().body("any { it.key = 'uuidAlias'}", is(true));
    response.then().body("size()", is(greaterThan(0)));
  }


  @Test(priority = 17)
  public void verify_Create_Beacon()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
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


  @Test(priority = 18)
  public void verify_Create_Uuid_Alias()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postUuidAliasRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    log.info("REQUEST-BODY -2:" + requestBodyJSONObject.toString());
    log.info("UUID" + uuid);
    requestBodyJSONObject.put("uuid", uuid);
    log.info("REQUEST-BODY -1:" + requestBodyJSONObject.toString());

    String uuidAlias = "qa" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("uuidAlias", uuidAlias);

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    response.then().body("uuid", is(uuid));
    response.then().body("uuidAlias", is(uuidAlias));
  }

  // here Invalid Means there is no beacon exist with this UUID.

  @Test(priority = 19)
  public void verify_Create_Invalid_Uuid_Alias()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postUuidAliasRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("uuid", HelperMethods.getUUIDAsString());

    String uuidAlias = "qa" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("uuidAlias", uuidAlias);

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(), "requirement failed: A beacon with this UUID doesn't exist yet");
  }


  @Test(priority = 20)
  public void verify_Update_Uuid_Alias()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_UUID_ALIAS_END_POINT_1 + uuid;

    String requestBody = fileUtils.getJsonTextFromFile(postUuidAliasRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String uuidAlias = "qa" + HelperMethods.getDateAsString() + "Updated";
    requestBodyJSONObject.put("uuidAlias", uuidAlias);
    requestBodyJSONObject.put("uuid", uuid);

    // Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "PUT",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    response.then().body("uuid", is(uuid));
    response.then().body("uuidAlias", is(uuidAlias));
  }


  @Test(priority = 21)
  public void verify_Update_Beacon()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + capturedNewBeaaconId;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "PUT",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
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

  @Test(priority = 22)
  public void verify_Create_Beacon_No_Major()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [major] is missing; please provide field and value in your request");
  }

  @Test(priority = 23)
  public void verify_Create_Beacon_No_Minor()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [minor] is missing; please provide field and value in your request");
  }


  @Test(priority = 24)
  public void verify_Create_Beacon_No_MajorMinorDisplayType()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [majorMinorDisplayType] is missing; please provide field and value in your request");
  }

  @Test(priority = 25)
  public void verify_Create_Beacon_No_Tags()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.remove("tags");
    JSONArray tags = new JSONArray();
    requestBodyJSONObject.put("tags", tags);
    uuid = HelperMethods.getUUIDAsString();
    requestBodyJSONObject.put("major", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("minor", HelperMethods.generateRandomNumber(9999));
    requestBodyJSONObject.put("uuid", uuid);

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    response.then().body("id", is(greaterThan(0)));
  }


  @Test(priority = 26)
  public void verify_Create_Beacon_No_Interval()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [interval] is missing; please provide field and value in your request");
  }


  @Test(priority = 27)
  public void verify_Create_Beacon_No_TxPower()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postBeaconRequestBodyPath);
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

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "POST",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.error("Error generating auth header" + e);
    }

    // Printing xAuth
    log.info("X-AUTH " + xAuth);

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("x-org-id", orgId)
            .header("x-client-id", clientId)
            .header("X-Auth", xAuth)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [txPower] is missing; please provide field and value in your request");
  }


  @Test(priority = 28)
  public void verify_Delete_Beacon()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.BEACON_END_POINT + capturedNewBeaaconId;

    // Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "DELETE",
              clientId_android_access_key,
              clientId_android_signature_key,
              requestURL,
              "");

      // Printing xAuth
      log.info("X-AUTH " + xAuth);

      // Extracting response after status code validation
      Response response =
          given()
              .header("Content-Type", "application/json")
              .header("x-org-id", orgId)
              .header("x-client-id", clientId)
              .header("X-Auth", xAuth)
              .delete(requestURL)
              .then()
              .statusCode(200)
              .extract()
              .response();

      log.info("RESPONSE " + response.asString());

    } catch (Exception e) {
      log.info("SKipping HttpResponseException");
    }
  }
}
