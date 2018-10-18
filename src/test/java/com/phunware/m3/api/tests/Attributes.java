package com.phunware.m3.api.tests;

import com.phunware.me_api.constants.MeAPI_Constants;
import com.phunware.utility.AuthHeader;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
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

/** Created by VinayKarumuri on 5/17/17. */
public class Attributes {

  private static String serviceEndPoint = null;
  private static String attributeName;
  private static Logger log = Logger.getLogger(Attributes.class);
  private String xAuth = null;
  private static String clientId_android_access_key =null;
  private static String clientId_android_signature_key =null;
  private static String postAttributeRequestBodyPath = null;
  private static String orgId =null;
  private static String clientId =null;


  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();

  @BeforeClass
  @Parameters({"env",  "attributeName",  "clientId_android_access_key",  "clientId_android_signature_key", "orgId", "clientId", "postAttributeRequestBodyPath"})
  public void preTestSteps(String env,
                           String attributeName,
                           String clientId_android_access_key,
                           String clientId_android_signature_key,
                           String orgId,
                           String clientId,
                           String postAttributeRequestBodyPath
                           ) {

    this.attributeName = attributeName;
    this.clientId_android_access_key = clientId_android_access_key;
    this.clientId_android_signature_key = clientId_android_signature_key;
    this.clientId = clientId;
    this.orgId = orgId;
    this.postAttributeRequestBodyPath = postAttributeRequestBodyPath;
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
  public void verify_Get_AttributeMetadata() {

    // Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT + attributeName;

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
    response.then().body("name", is(attributeName));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("attributeType"), is("ENUM"));
    response.then().body(("any { it.key == 'lastUpdated'}"), is(true));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'attributeType'}"), is(true));
    response.then().body(("any { it.key == 'dateCreated'}"), is(true));
    response.then().body("allowedValues.size()", greaterThan(0));
  }

  @Test(priority = 2)
  public void verify_Get_Invalid_AttributeMetadata() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT + "000";

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

    // JSON response Pay load validations
    Assert.assertEquals(
        response.asString(),
        "The requested resource could not be found but may be available again in the future.");
  }


  @Test(priority = 3)
  public void verify_Get_Collection_Of_AttributeMetadata() {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT_1;

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
    response.then().body("size()", is(greaterThan(0)));
    response.then().body("items.flatten().any {it.containsKey('name') }", is(true));
    response.then().body("items.flatten().any {it.containsKey('lastUpdated') }", is(true));
    response.then().body("items.flatten().any {it.containsKey('allowedValues') }", is(true));
    response.then().body("items.flatten().any {it.containsKey('enabled') }", is(true));
    response.then().body("items.flatten().any {it.containsKey('dateCreated') }", is(true));
    response.then().body("items.attributeType", everyItem(is("ENUM")));
  }


  @Test(priority = 4)
  public void verify_Create_Attribute()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postAttributeRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    attributeName = "testingtypes" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", attributeName);
    requestBodyJSONObject.remove("allowedValues");
    JSONArray allowedValues = new JSONArray();
    allowedValues.put("apitesting");
    allowedValues.put("loadtesting");
    allowedValues.put("automationtesting");
    allowedValues.put("securitytesting");
    requestBodyJSONObject.put("allowedValues", allowedValues);

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
    response.then().body("name", is(attributeName));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("attributeType"), is("ENUM"));
    response.then().body(("any { it.key == 'lastUpdated'}"), is(true));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'attributeType'}"), is(true));
    response.then().body(("any { it.key == 'dateCreated'}"), is(true));
    response.then().body("allowedValues.size()", greaterThan(0));
  }


  @Test(priority = 5)
  public void verify_Create_Attribute_WithOut_Name()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postAttributeRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("name", "");
    requestBodyJSONObject.remove("allowedValues");
    JSONArray allowedValues = new JSONArray();
    allowedValues.put("apitesting");
    allowedValues.put("loadtesting");
    allowedValues.put("automationtesting");
    allowedValues.put("securitytesting");
    requestBodyJSONObject.put("allowedValues", allowedValues);

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
            + "requirement failed: allowed characters in name are: a-z, _ and 0-9");
  }


  @Test(priority = 6)
  public void verify_Create_Attribute_WithOut_AllowedValues()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postAttributeRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String name = "testingtypes" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", name);
    requestBodyJSONObject.remove("allowedValues");
    JSONArray allowedValues = new JSONArray();
    requestBodyJSONObject.put("allowedValues", allowedValues);

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
            + "requirement failed: ENUM must have allowedValues");
  }


  @Test(priority = 7)
  public void verify_Update_Attribute_AddValues()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT + attributeName;

    String requestBody = fileUtils.getJsonTextFromFile(postAttributeRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("name", attributeName);
    requestBodyJSONObject.remove("allowedValues");
    JSONArray allowedValues = new JSONArray();
    allowedValues.put("apitesting");
    allowedValues.put("loadtesting");
    allowedValues.put("automationtesting");
    allowedValues.put("securitytesting");
    allowedValues.put("uitesting");
    requestBodyJSONObject.put("allowedValues", allowedValues);

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
    response.then().body("name", is(attributeName));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("attributeType"), is("ENUM"));
    response.then().body(("any { it.key == 'lastUpdated'}"), is(true));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'attributeType'}"), is(true));
    response.then().body(("any { it.key == 'dateCreated'}"), is(true));
    response.then().body("allowedValues.size()", greaterThan(0));
  }


  @Test(priority = 7)
  public void verify_Update_Attribute_EditValues()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT + attributeName;

    String requestBody = fileUtils.getJsonTextFromFile(postAttributeRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("name", attributeName);
    requestBodyJSONObject.remove("allowedValues");
    JSONArray allowedValues = new JSONArray();
    allowedValues.put("apitesting_updated");
    allowedValues.put("loadtesting_updated");
    allowedValues.put("automationtesting_updated");
    allowedValues.put("securitytesting_updated");
    allowedValues.put("uitesting_updated");
    requestBodyJSONObject.put("allowedValues", allowedValues);

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
            .statusCode(400)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    Assert.assertEquals(
        response.asString(), "requirement failed: Cannot remove from allowedValues");
  }


  @Test(priority = 9)
  public void verify_Disable_Attribute()
      throws IOException, NullPointerException {

    // Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.ATTRIBUTE_METADATA_END_POINT + attributeName;

    String requestBody = fileUtils.getJsonTextFromFile(postAttributeRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("name", attributeName);
    requestBodyJSONObject.remove("allowedValues");
    JSONArray allowedValues = new JSONArray();
    allowedValues.put("apitesting");
    allowedValues.put("loadtesting");
    allowedValues.put("automationtesting");
    allowedValues.put("securitytesting");
    allowedValues.put("uitesting");
    requestBodyJSONObject.put("allowedValues", allowedValues);
    requestBodyJSONObject.put("enabled", false);

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
    response.then().body("name", is(attributeName));
    response.then().body("enabled", is(false));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("attributeType"), is("ENUM"));
    response.then().body(("any { it.key == 'lastUpdated'}"), is(true));
    response.then().body(("any { it.key == 'allowedValues'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'attributeType'}"), is(true));
    response.then().body(("any { it.key == 'dateCreated'}"), is(true));
    response.then().body("allowedValues.size()", greaterThan(0));
  }
}
