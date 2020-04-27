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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;


public class Profiles {

  private static String serviceEndPoint = null;
  private static String newProfileId;
  private static String profileId;
  private static String clientId_android_access_key = null;
  private static String clientId_android_signature_key = null;
  private static String orgId = null;
  private static String clientId = null;
  private static String postProfileRequestBodyPath = null;


  private static Logger log = Logger.getLogger(Profiles.class);
  private String xAuth = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();

  @BeforeClass
  @Parameters({"env", "profileId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  public void preTestSteps(String env, String profileId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) {

    this.profileId = profileId;
    this.clientId_android_access_key = clientId_android_access_key;
    this.clientId_android_signature_key = clientId_android_signature_key;
    this.clientId = clientId;
    this.orgId = orgId;
    this.postProfileRequestBodyPath = postProfileRequestBodyPath;

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
  public void verify_Get_Profile() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT + profileId;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating Auth header" + e);
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
    response.then().body("id", is(Integer.parseInt(profileId)));
    response.then().body(("any { it.key == 'name'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'date_created'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("groups.size()"), is(greaterThan(0)));
  }

  @Test(priority = 2)
  public void verify_Get_InvalidProfileId() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT + "000";

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating Auth header" + e);
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

    // JSON response Pay load validations
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }

  @Test(priority = 3)
  public void verify_Get_Collection_Of_Profiles_By_Org() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    // Auth Generation
    try {
      xAuth =
          auth.generateAuthHeader(
              "GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.error("Error generating Auth header" + e);
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
    response.then().body("size()", greaterThan(0));
    response.then().body("items.flatten().any { it.containsKey('date_created') }", is(true));
    response.then().body("items.flatten().any { it.containsKey('name') }", is(true));
    response.then().body("items.flatten().any { it.containsKey('description') }", is(true));
    response.then().body("items.flatten().any { it.containsKey('enabled') }", is(true));
    response.then().body("items.flatten().any { it.containsKey('id') }", is(true));
    response.then().body("items.flatten().any { it.containsKey('groups') }", is(true));
    response
        .then()
        .body(
            "items.groups.attributes.flatten().any { it.containsKey('attributeMetadataName') }",
            is(true));
    response
        .then()
        .body("items.groups.attributes.flatten().any { it.containsKey('operator') }", is(true));
    response
        .then()
        .body("items.groups.attributes.flatten().any { it.containsKey('value') }", is(true));
  }

  @Test(priority = 4)
  public void verify_Create_Profile()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postProfileRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray attribute = new JSONArray();
    JSONObject attribute1 = new JSONObject();
    String car[] = {"honda", "tesla", "camry"};
    String member[] = {"silver", "gold", "platinum"};
    String operator[] = {"Equal", "NotEqual"};
    attribute1.put("attributeMetadataName", "car");
    attribute1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject attribute2 = new JSONObject();
    attribute2.put("attributeMetadataName", "member");
    attribute2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    attribute.put(attribute1);
    attribute.put(attribute2);
    requestBodyJSONObject.getJSONArray("groups").getJSONObject(0).put("attributes", attribute);
    requestBodyJSONObject.put("description", profileName);

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
      log.error("Error generating Auth header" + e);
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
    response.then().body(("any { it.key == 'name'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'date_created'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("groups.size()"), is(greaterThan(0)));

    newProfileId = response.then().extract().path("id").toString();
  }

  @Test(priority = 5)
  public void verify_Create_Profile_WithoutEnabled()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postProfileRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray attribute = new JSONArray();
    JSONObject attribute1 = new JSONObject();
    String car[] = {"honda", "tesla", "camry"};
    String member[] = {"silver", "gold", "platinum"};
    String operator[] = {"Equal", "NotEqual"};
    attribute1.put("attributeMetadataName", "car");
    attribute1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject attribute2 = new JSONObject();
    attribute2.put("attributeMetadataName", "member");
    attribute2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    attribute.put(attribute1);
    attribute.put(attribute2);
    requestBodyJSONObject.getJSONArray("groups").getJSONObject(0).put("attributes", attribute);
    requestBodyJSONObject.put("description", profileName);
    requestBodyJSONObject.remove("enabled");

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
      log.error("Error generating Auth header" + e);
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
    log.info("RESPONSE:" + response.statusCode());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [enabled] is missing; please provide field and value in your request");
  }

  @Test(priority = 6)
  public void verify_Create_Profile_Without_Description()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postProfileRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray attribute = new JSONArray();
    JSONObject attribute1 = new JSONObject();
    String car[] = {"honda", "tesla", "camry"};
    String member[] = {"silver", "gold", "platinum"};
    String operator[] = {"Equal", "NotEqual"};
    attribute1.put("attributeMetadataName", "car");
    attribute1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject attribute2 = new JSONObject();
    attribute2.put("attributeMetadataName", "member");
    attribute2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    attribute.put(attribute1);
    attribute.put(attribute2);
    requestBodyJSONObject.getJSONArray("groups").getJSONObject(0).put("attributes", attribute);
    requestBodyJSONObject.put("description", profileName);
    requestBodyJSONObject.remove("description");

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
      log.error("Error generating Auth header" + e);
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
            + "Required field [description] for a profile is missing; please provide field and value in your request.");
  }

  @Test(priority = 7)
  public void Create_Profile_WithOut_Attributes()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postProfileRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.getJSONArray("groups").getJSONObject(0).remove("attributes");

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
      log.error("Error generating Auth header" + e);
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
    log.info("RESPONSE:" + response.statusCode());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(),
        "The request content was malformed:\n"
            + "Required field [attributes] is missing in a profile group ; please provide field and value in your request.");
  }

  @Test(priority = 8)
  public void Create_Profile_WithOut_Name()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postProfileRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray attribute = new JSONArray();
    JSONObject attribute1 = new JSONObject();
    String car[] = {"honda", "tesla", "camry"};
    String member[] = {"silver", "gold", "platinum"};
    String operator[] = {"Equal", "NotEqual"};
    attribute1.put("attributeMetadataName", "car");
    attribute1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject attribute2 = new JSONObject();
    attribute2.put("attributeMetadataName", "member");
    attribute2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    attribute.put(attribute1);
    attribute.put(attribute2);
    requestBodyJSONObject.getJSONArray("groups").getJSONObject(0).put("attributes", attribute);
    requestBodyJSONObject.put("description", profileName);
    requestBodyJSONObject.remove("name");

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
      log.error("Error generating Auth header" + e);
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
            + "Required field [name] for a profile is missing; please provide field and value in your request.");
  }

  @Test(priority = 9)
  public void Disable_Profile()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT + newProfileId;

    String requestBody = fileUtils.getJsonTextFromFile(postProfileRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray attribute = new JSONArray();
    JSONObject attribute1 = new JSONObject();
    String car[] = {"honda", "tesla", "camry"};
    String member[] = {"silver", "gold", "platinum"};
    String operator[] = {"Equal", "NotEqual"};
    attribute1.put("attributeMetadataName", "car");
    attribute1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject attribute2 = new JSONObject();
    attribute2.put("attributeMetadataName", "member");
    attribute2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    attribute2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    attribute.put(attribute1);
    attribute.put(attribute2);
    requestBodyJSONObject.getJSONArray("groups").getJSONObject(0).put("attributes", attribute);
    requestBodyJSONObject.put("description", profileName);
    requestBodyJSONObject.put("enabled", false);
    requestBodyJSONObject.put("id", Integer.parseInt(newProfileId));

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
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
      log.error("Error generating Auth header" + e);
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

    response.then().body("enabled", is(false));
    response.then().body(("groups.attributes.size()"), is(greaterThan(0)));
  }
}
