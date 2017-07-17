package com.phunware.m3.api.tests;

import com.phunware.me_api.constants.MeAPI_Constants;
import com.phunware.utility.AuthHeader;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.pattern.IntegerPatternConverter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Created by VinayKarumuri on 5/18/17.
 */
public class Profiles {



  private static String serviceEndPoint = null;
  private static String newProfileId;
  static Logger log = Logger.getLogger(Profiles.class);
  private String xAuth = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();


  @BeforeClass
  @Parameters("env")  public void preTestSteps(String env) {

    if (env.equalsIgnoreCase("PROD")) {
      serviceEndPoint = MeAPI_Constants.SERVICE_ENT_POINT_PROD;
    } else if (env.equalsIgnoreCase("STAGE")) {
      serviceEndPoint = MeAPI_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
    }
  }

  @Parameters({"profileId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 1)
  public void verify_Get_Profile(String profileId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT + profileId;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
    response.then().body("id", is(Integer.parseInt(profileId)));
    response.then().body(("any { it.key == 'name'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'date_created'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("rules.size()"), is(greaterThan(0)));
  }

  @Parameters({"profileId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 2)
  public void verify_Get_InvalidProfileId(String profileId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT + "000";

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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

    //JSON response Pay load validations
    Assert.assertEquals( response.asString(), "The requested resource could not be found but may be available again in the future.");

  }


  @Parameters({"profileId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 3)
  public void verify_Get_Collection_Of_Profiles_By_Org(String profileId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("GET", clientId_android_access_key, clientId_android_signature_key, requestURL, "");
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
    response.then().body("size()" , greaterThan(0));
    response.then().body("flatten().any { it.containsKey('date_created') }" , is(true));
    response.then().body("flatten().any { it.containsKey('name') }" , is(true));
    response.then().body("flatten().any { it.containsKey('description') }" , is(true));
    response.then().body("flatten().any { it.containsKey('enabled') }" , is(true));
    response.then().body("flatten().any { it.containsKey('id') }" , is(true));
    response.then().body("flatten().any { it.containsKey('rules') }" , is(true));
    response.then().body("rules.flatten().any { it.containsKey('attributeMetadataName') }" , is(true));
    response.then().body("rules.flatten().any { it.containsKey('operator') }" , is(true));
    response.then().body("rules.flatten().any { it.containsKey('value') }" , is(true));
  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  @Test(priority = 4)
  public void verify_Create_Profile(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    File file = new File(postProfileRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray rules = new JSONArray();
    JSONObject rule1 = new JSONObject();
    String car[] = {"honda", "tesla" , "camry"};
    String member[] = {"silver","gold","platinum"};
    String operator[] = {"Equal", "NotEqual"};
    rule1.put("attributeMetadataName", "car");
    rule1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject rule2 = new JSONObject();
    rule2.put("attributeMetadataName", "member");
    rule2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    rules.put(rule1);
    rules.put(rule2);
    requestBodyJSONObject.put("rules" , rules);
    requestBodyJSONObject.put("description", profileName);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
    response.then().body(("any { it.key == 'name'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'date_created'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("rules.size()"), is(greaterThan(0)));

    newProfileId = response.then().extract().path("id").toString();

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  @Test(priority = 4)
  public void verify_Create_Profile_WithoutEnabled(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    File file = new File(postProfileRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray rules = new JSONArray();
    JSONObject rule1 = new JSONObject();
    String car[] = {"honda", "tesla" , "camry"};
    String member[] = {"silver","gold","platinum"};
    String operator[] = {"Equal", "NotEqual"};
    rule1.put("attributeMetadataName", "car");
    rule1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject rule2 = new JSONObject();
    rule2.put("attributeMetadataName", "member");
    rule2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    rules.put(rule1);
    rules.put(rule2);
    requestBodyJSONObject.put("rules" , rules);
    requestBodyJSONObject.put("description", profileName);
    requestBodyJSONObject.remove("enabled");


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
        "key not found: enabled");

  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  @Test(priority = 5)
  public void verify_Create_Profile_Without_Description(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    File file = new File(postProfileRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray rules = new JSONArray();
    JSONObject rule1 = new JSONObject();
    String car[] = {"honda", "tesla" , "camry"};
    String member[] = {"silver","gold","platinum"};
    String operator[] = {"Equal", "NotEqual"};
    rule1.put("attributeMetadataName", "car");
    rule1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject rule2 = new JSONObject();
    rule2.put("attributeMetadataName", "member");
    rule2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    rules.put(rule1);
    rules.put(rule2);
    requestBodyJSONObject.put("rules" , rules);
    requestBodyJSONObject.remove("description");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());


    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
    response.then().body(("any { it.key == 'name'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'date_created'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("rules.size()"), is(greaterThan(0)));

  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  @Test(priority = 6)
  public void Create_Profile_WithOut_Rules(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    File file = new File(postProfileRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
        "requirement failed: rule(s) not found for profile Id: None");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  @Test(priority = 7)
  public void Create_Profile_WithOut_Name(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT_1;

    File file = new File(postProfileRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray rules = new JSONArray();
    JSONObject rule1 = new JSONObject();
    String car[] = {"honda", "tesla" , "camry"};
    String member[] = {"silver","gold","platinum"};
    String operator[] = {"Equal", "NotEqual"};
    rule1.put("attributeMetadataName", "car");
    rule1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject rule2 = new JSONObject();
    rule2.put("attributeMetadataName", "member");
    rule2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    rules.put(rule1);
    rules.put(rule2);
    requestBodyJSONObject.put("rules" , rules);
    requestBodyJSONObject.remove("name");


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("POST", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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
        "key not found: name");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postProfileRequestBodyPath"})
  @Test(priority = 8)
  public void Disable_Profile(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postProfileRequestBodyPath) throws IOException, NullPointerException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.PROFILES_END_POINT + newProfileId;

    File file = new File(postProfileRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String profileName = "apitesting" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("name", profileName);
    requestBodyJSONObject.remove("rules");
    JSONArray rules = new JSONArray();
    JSONObject rule1 = new JSONObject();
    String car[] = {"honda", "tesla" , "camry"};
    String member[] = {"silver","gold","platinum"};
    String operator[] = {"Equal", "NotEqual"};
    rule1.put("attributeMetadataName", "car");
    rule1.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule1.put("value", car[HelperMethods.generateRandomNumber(2)]);
    JSONObject rule2 = new JSONObject();
    rule2.put("attributeMetadataName", "member");
    rule2.put("operator", operator[HelperMethods.generateRandomNumber(1)]);
    rule2.put("value", member[HelperMethods.generateRandomNumber(2)]);
    rules.put(rule1);
    rules.put(rule2);
    requestBodyJSONObject.put("rules" , rules);
    requestBodyJSONObject.put("enabled", false);
    requestBodyJSONObject.put("id", Integer.parseInt(newProfileId));


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:" + requestBodyJSONObject.toString());

    //Auth Generation
    try {
      xAuth = auth.generateAuthHeader("PUT", clientId_android_access_key, clientId_android_signature_key, requestURL, requestBodyJSONObject.toString());
    } catch (Exception e) {
      log.debug( "Error generating Auth header" + e);
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

    response.then().body("enabled", is(false));
    response.then().body(("rules.size()"), is(greaterThan(0)));


  }


}
