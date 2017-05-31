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

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Created by VinayKarumuri on 5/11/17.
 */
public class Location {

  static Logger log;

  //public String dynamicValue;
  public static String capturedLocationId;
  FileUtils fileUtils = new FileUtils();
  public static String serviceEndPoint = null;
  AuthHeader auth = new AuthHeader();
  public String xAuth = null;


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
    log = Logger.getLogger(Location.class);
  }

  @Parameters({"locationId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 1)
  public void verify_Get_Location(String locationId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT + locationId;


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
    response.then().body("id", is(Integer.parseInt(locationId)));
    response.then().body("orgId", is(Integer.parseInt(orgId)));
    response.then().body(("any { it.key == 'locationName'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("geoFence.any { it.key == 'radius' }"), is(true));
    response.then().body(("geoFence.any { it.key == 'center' }"), is(true));
    response.then().body(("geoFence.center.any { it.key == 'latitude' }"), is(true));
    response.then().body(("geoFence.center.any { it.key == 'longitude' }"), is(true));
    response.then().body(("any { it.key == 'city'}"), is(true));
    response.then().body(("any { it.key == 'zip'}"), is(true));
    response.then().body(("any { it.key == 'tags'}"), is(true));
    response.then().body(("any { it.key == 'country'}"), is(true));
    response.then().body(("any { it.key == 'address1'}"), is(true));
    response.then().body(("any { it.key == 'address2'}"), is(true));
    response.then().body(("any { it.key == 'timeZone'}"), is(true));

  }

  @Parameters({"locationId", "clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 2)
  public void verify_Get_Invalid_Location(String locationId, String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT + "000";


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


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 3)
  public void verify_Get_Locations_By_Org(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Query Parameters
    String queryParameters = "orgId=" + orgId;
    log.info("QUERY-PARAMETERS " + queryParameters);


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
            .queryParam("orgId", orgId)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response validations
    response.then().body("totalCount", is(greaterThan(0)));
    response.then().body("resultCount", is(greaterThan(0)));
    response.then().body(("any { it.key == 'offset'}"), is(true));
    response.then().body("items.size", is(greaterThan(0)));
    response.then().body("items.any { it.containsKey('locationName') }", is(true));

  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 4)
  public void verify_Get_Locations_By_InvalidOrg(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Query Parameters
    String queryParameters = "orgId=12c";
    log.info(queryParameters);


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
            .queryParam("orgId", "12c")
            .get(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId"})
  @Test(priority = 5)
  public void verify_Get_Tags_By_Org(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.TAG_END_POINT;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Query Parameters
    String queryParameters = "orgId=" + orgId;
    log.info(queryParameters);


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
            .queryParam("orgId", orgId)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response validations

    response.then().body("size()", is(greaterThan(0)));

  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "tags"})
  @Test(priority = 6)
  public void verify_Get_Locations_By_Tags(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String tags) {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;


    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Query Parameters
    String queryParameters = "tags=" + tags;
    log.info(queryParameters);


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
            .queryParam("tags", tags)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    response.then().body("items.tags", everyItem(hasItem("qa")));
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 7)
  public void verify_Create_Location(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);


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
    response.then().body(("any { it.key == 'locationName'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("geoFence.any { it.key == 'radius' }"), is(true));
    response.then().body(("geoFence.any { it.key == 'center' }"), is(true));
    response.then().body(("geoFence.center.any { it.key == 'latitude' }"), is(true));
    response.then().body(("geoFence.center.any { it.key == 'longitude' }"), is(true));
    response.then().body(("any { it.key == 'city'}"), is(true));
    response.then().body(("any { it.key == 'zip'}"), is(true));
    response.then().body(("any { it.key == 'tags'}"), is(true));
    response.then().body(("any { it.key == 'country'}"), is(true));
    response.then().body(("any { it.key == 'address1'}"), is(true));
    response.then().body(("any { it.key == 'address2'}"), is(true));
    response.then().body(("any { it.key == 'timeZone'}"), is(true));

    capturedLocationId = response.then().extract().path("id").toString();
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 8)
  public void verify_Create_Location_InvalidState(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("state", "OO");


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


    //JSON response validations
    Assert.assertEquals(response.asString(), "Address not found");
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 9)
  public void verify_Create_Location_NoTimezone(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString() + "no time zone" + HelperMethods.generateRandomNumber(100);
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("timeZone", "");


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


    //JSON response validations
    Assert.assertEquals(response.asString(), "requirement failed: Value required for [timeZone]; please provide [locationName: " + locationName + "]");
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 10)
  public void verify_Create_Location_NoLocationName(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", "");

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


    //JSON response validations
    Assert.assertEquals(response.asString(), "requirement failed: Value required for [locationName]; please provide");
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 11)
  public void verify_Create_Location_NoCountry(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("country", "");


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


    //JSON response validations
    Assert.assertEquals(response.asString(), "requirement failed: Value required for [country]; please provide [locationName: " + locationName + "]");
  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 12)
  public void verify_Create_Location_NoZip(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("zip", "");


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


    //JSON response validations
    Assert.assertEquals(response.asString(), "requirement failed: Value required for [zip]; please provide [locationName: " + locationName + "]");
  }

  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 13)
  public void verify_Create_Location_NoCity(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("city", "");


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


    //JSON response validations
    Assert.assertEquals(response.asString(), "requirement failed: Value required for [city]; please provide [locationName: " + locationName + "]");
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "postLocationRequestBodyPath"})
  @Test(priority = 14)
  public void verify_Create_Location_NoAddress1(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String postLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT_1;

    File file = new File(postLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("address1", "");


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


    //JSON response validations
    Assert.assertEquals(response.asString(), "requirement failed: missing address1 [locationName: " + locationName + "]");
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putLocationRequestBodyPath"})
  @Test(priority = 15)
  public void verify_Update_Location(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT + capturedLocationId;

    File file = new File(putLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    JSONArray requestBodyTags = requestBodyJSONObject.getJSONArray("tags");
    requestBodyTags.put(HelperMethods.getDateAsString());
    requestBodyJSONObject.put("id", Integer.parseInt(capturedLocationId));
    requestBodyJSONObject.put("locationName", locationName);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
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

    //JSON response validations
    response.then().body("id", is(Integer.parseInt(capturedLocationId)));
    response.then().body("orgId", is(Integer.parseInt(orgId)));
    response.then().body(("any { it.key == 'locationName'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("geoFence.any { it.key == 'radius' }"), is(true));
    response.then().body(("geoFence.any { it.key == 'center' }"), is(true));
    response.then().body(("geoFence.center.any { it.key == 'latitude' }"), is(true));
    response.then().body(("geoFence.center.any { it.key == 'longitude' }"), is(true));
    response.then().body(("any { it.key == 'city'}"), is(true));
    response.then().body(("any { it.key == 'zip'}"), is(true));
    response.then().body(("any { it.key == 'tags'}"), is(true));
    response.then().body(("any { it.key == 'country'}"), is(true));
    response.then().body(("any { it.key == 'address1'}"), is(true));
    response.then().body(("any { it.key == 'address2'}"), is(true));
    response.then().body(("any { it.key == 'timeZone'}"), is(true));
  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putLocationRequestBodyPath"})
  @Test(priority = 16)
  public void verify_Update_Location_InvalidId(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT + 0;

    File file = new File(putLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    //  log.info("REQUEST BODY"  + requestBody);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    JSONArray requestBodyTags = requestBodyJSONObject.getJSONArray("tags");
    log.info(requestBodyTags.toString());
    requestBodyTags.put(HelperMethods.getDateAsString());
    requestBodyJSONObject.put("id", 0);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
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
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response validations
    Assert.assertEquals(response.asString(), "The requested resource could not be found but may be available again in the future.");

  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "putLocationRequestBodyPath"})
  @Test(priority = 17)
  public void verify_Disable_Location(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String putLocationRequestBodyPath) throws IOException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.LOCATION_END_POINT + capturedLocationId;


    File file = new File(putLocationRequestBodyPath);
    String requestBody = fileUtils.getJsonText(file);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String locationName = "PhunwareAustin" + HelperMethods.getDateAsString();
    requestBodyJSONObject.put("locationName", locationName);
    requestBodyJSONObject.put("enabled", false);
    requestBodyJSONObject.put("id", Integer.parseInt(capturedLocationId));


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
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
//            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response validations
    response.then().body("enabled", is(false));


  }


  @Parameters({"clientId_android_access_key", "clientId_android_signature_key", "orgId", "clientId", "tags"})
  @Test(priority = 18)
  public void verify_Download_Locations(String clientId_android_access_key, String clientId_android_signature_key, String orgId, String clientId, String tags) throws IOException {

    //Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.LOCATIONS_DOWNLOAD_END_POINT;

    //Query Parameters
    String queryParameters = "tags=" + tags;


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
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
            .queryParam("tags", tags)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response validations
    response.then().body(("any { it.key == 'filePathInS3'}"), is(true));
    response.then().body("errorMessage", isEmptyString());

  }


}