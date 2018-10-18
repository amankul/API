package com.phunware.m3.api.tests;

import com.phunware.me_api.constants.MeAPI_Constants;
import com.phunware.utility.AuthHeader;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** Created by VinayKarumuri on 5/24/17. */
public class DeviceAPI {

  private static String serviceEndPoint = null;
  private static String deviceId = HelperMethods.getUUIDAsString();
  private static String deviceRegKey =
      "d6BZSAdhNd8:APA91bHpVrhxUjCOHm-Tzf6HOZjtpsSUn3vgaxgMNiCdv7FjUds-OiyhjCKSFKMgHSAVNcwM5Eld8ZZz0QQzrzTRbjR7IdvzNcmiJeWu8uEi5Qr9oIPtL07vWFGLIAu2-X_FjQafXRHM_"
          + deviceId;
  private static String clientId_android_access_key =null;
  private static String clientId_android_signature_key =null;
  private static String orgId =null;
  private static String clientId =null;
  private static String postDeviceRegistrationBodyPath = null;
  private static String postDownloadGeofenceBodyPath = null;
  private static String postGeofenceEntryBodyPath = null;
  private static String postDeviceAttributesBodyPath =null;
  private static String deviceAttributeName = null;
  private static String deviceAttributeValue = null;
  private static String postGeofenceExitBodyPath = null;
  private static String postStaticIdSetBodyPath = null;

  private static Logger log = Logger.getLogger(DeviceAPI.class);
  private String xAuth = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();

  @BeforeClass
  @Parameters({"env",
              "clientId_android_access_key",
              "clientId_android_signature_key",
              "orgId",
              "clientId",
              "postDeviceRegistrationBodyPath",
              "postDownloadGeofenceBodyPath",
              "postGeofenceEntryBodyPath",
              "postDeviceAttributesBodyPath",
              "deviceAttributeName",
              "deviceAttributeValue",
              "postGeofenceExitBodyPath",
              "postStaticIdSetBodyPath"})
  public void preTestSteps(String env,
                           String clientId_android_access_key,
                           String clientId_android_signature_key,
                           String orgId,
                           String clientId,
                           String postDeviceRegistrationBodyPath,
                           String postDownloadGeofenceBodyPath,
                           String postGeofenceEntryBodyPath,
                           String postDeviceAttributesBodyPath,
                           String deviceAttributeName,
                           String deviceAttributeValue,
                           String postGeofenceExitBodyPath,
                           String postStaticIdSetBodyPath) {

    this.clientId_android_access_key = clientId_android_access_key;
    this.clientId_android_signature_key = clientId_android_signature_key;
    this.clientId = clientId;
    this.orgId = orgId;
    this.postDeviceRegistrationBodyPath = postDeviceRegistrationBodyPath;
    this.postDownloadGeofenceBodyPath = postDownloadGeofenceBodyPath;
    this.postGeofenceEntryBodyPath = postGeofenceEntryBodyPath;
    this.postDeviceAttributesBodyPath = postDeviceAttributesBodyPath;
    this.deviceAttributeName = deviceAttributeName;
    this.deviceAttributeValue = deviceAttributeValue;
    this.postGeofenceExitBodyPath = postGeofenceExitBodyPath;
    this.postStaticIdSetBodyPath = postStaticIdSetBodyPath;
    this.postDeviceRegistrationBodyPath = postDeviceRegistrationBodyPath;


    if ("PROD".equalsIgnoreCase(env)) {
      serviceEndPoint = null;
      Assert.fail("Environment is PROD. Device API tests should not be run in PROD");
    } else if ("STAGE".equalsIgnoreCase(env)) {
      serviceEndPoint = MeAPI_Constants.DEVICE_API_SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
      Assert.fail("Environment is not set properly. Please check your testng xml file");
    }
  }

  @Test(priority = 1)
  public void Verify_Device_Registration()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postDeviceRegistrationBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);
    requestBodyJSONObject.put("deviceRegKey", deviceRegKey);

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
    response.then().body(("any { it.key == 'geoFences'}"), is(true));
    response.then().body("geoFences.plus.size()", is(greaterThan(0)));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('tags')}"), is(true));
    response
        .then()
        .body(("geoFences.plus.flatten().any { it.containsKey('locationCode')}"), is(true));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('id')}"), is(true));
    response
        .then()
        .body(("geoFences.plus.flatten().any { it.containsKey('validEventTypes')}"), is(true));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('geoShape')}"), is(true));
    response
        .then()
        .body(("geoFences.plus.flatten().any { it.containsKey('allowsCheckin')}"), is(true));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('name')}"), is(true));
    response.then().body(("geoFences"), hasKey("in"));
    response.then().body(("geoFences"), hasKey("messages"));
    response.then().body(("geoFences"), hasKey("plus"));
    response.then().body(("geoFences"), hasKey("minus"));
  }

  @Test(priority = 2)
  public void Verify_Download_Geofence()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postDownloadGeofenceBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);

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
    response.then().body(("any { it.key == 'geoFences'}"), is(true));
    response.then().body("geoFences.plus.size()", is(greaterThan(0)));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('tags')}"), is(true));
    response
        .then()
        .body(("geoFences.plus.flatten().any { it.containsKey('locationCode')}"), is(true));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('id')}"), is(true));
    response
        .then()
        .body(("geoFences.plus.flatten().any { it.containsKey('validEventTypes')}"), is(true));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('geoShape')}"), is(true));
    response
        .then()
        .body(("geoFences.plus.flatten().any { it.containsKey('allowsCheckin')}"), is(true));
    response.then().body(("geoFences.plus.flatten().any { it.containsKey('name')}"), is(true));
    response.then().body(("geoFences"), hasKey("in"));
    response.then().body(("geoFences"), hasKey("messages"));
    response.then().body(("geoFences"), hasKey("plus"));
    response.then().body(("geoFences"), hasKey("minus"));
  }

  @Test(priority = 3)
  public void Verify_Geofence_Entry()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postGeofenceEntryBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);

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
    response.then().body(("any { it.key == 'geoFences'}"), is(true));
    response.then().body(("geoFences"), hasKey("in"));
    response.then().body(("any { it.key == 'messages'}"), is(true));
    response.then().body(("geoFences"), hasKey("plus"));
    response.then().body(("geoFences"), hasKey("minus"));
  }

  @Test(priority = 4)
  public void Verify_Get_Attribute_Metadata()
      throws IOException {
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
    response.then().body("size()", is(greaterThan(0)));
    response.then().body("flatten().any {it.containsKey('name') }", is(true));
    response.then().body("flatten().any {it.containsKey('allowedValues') }", is(true));
    response.then().body("flatten().any {it.containsKey('enabled') }", is(true));
    response.then().body("attributeType", everyItem(is("ENUM")));
  }

  @Test(priority = 5)
  public void Verify_Create_Device_Attributes()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_ATTRIBUTE_API_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postDeviceAttributesBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyProfileAttributes =
        requestBodyJSONObject.getJSONObject("profileAttributes");
    requestBodyProfileAttributes.put(deviceAttributeName, deviceAttributeValue);
    requestBodyJSONObject.put("deviceId", deviceId);

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST-" + requestBodyJSONObject.toString());

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
  }

  @Test(priority = 6)
  public void Verify_Get_Device_Attributes()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_ATTRIBUTE_API_END_POINT;
    String queryParameters = "deviceId=" + deviceId;

    // Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

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
            .queryParam("deviceId", deviceId)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validation
    response.then().body(("any { it.key == 'deviceId'}"), is(true));
    response.then().body(("any { it.key == 'profileAttributes'}"), is(true));
  }

  @Test(priority = 7)
  public void Verify_Geofence_Exit()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postGeofenceExitBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);

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
    response.then().body(("any { it.key == 'geoFences'}"), is(true));
    response.then().body(("geoFences"), hasKey("in"));
    response.then().body(("any { it.key == 'messages'}"), is(true));
    response.then().body(("geoFences"), hasKey("plus"));
    response.then().body(("geoFences"), hasKey("minus"));
  }


  @Test(priority = 8)
  public void Verify_Set_StaticId()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postStaticIdSetBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);
    requestBodyJSONObject.put("staticId", HelperMethods.generateSHA256Hash(deviceId));

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
            .statusCode(204)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());
  }

  @Test(priority = 9)
  public void Verify_Device_Unregistration()
      throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postDeviceRegistrationBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);
    requestBodyJSONObject.put("deviceRegKey", deviceRegKey);
    requestBodyJSONObject.put("sendable", 0);
    requestBodyJSONObject.put("eventType", "DEVICE_UNREGISTRATION");

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
    response.then().body(("any { it.key == 'geoFences'}"), is(true));
    response.then().body(("geoFences"), hasKey("in"));
    response.then().body(("geoFences"), hasKey("messages"));
    response.then().body(("geoFences"), hasKey("plus"));
    response.then().body(("geoFences"), hasKey("minus"));
  }

  //Verify_Device_Registration_Key_Update
  @Test(priority = 10)
  public void Verify_Device_Registration_Key_Update()
          throws IOException {
    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.DEVICE_API_EVENTS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(postDeviceRegistrationBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("deviceId", deviceId);
    requestBodyJSONObject.put("deviceRegKey", deviceRegKey);
    requestBodyJSONObject.remove("apiVersion");
    requestBodyJSONObject.put("eventType", "DEVICE_REGKEY_UPDATE");

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

  }
}
