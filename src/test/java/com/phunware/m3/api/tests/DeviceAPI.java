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
  private static Logger log = Logger.getLogger(DeviceAPI.class);
  private String xAuth = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();

  @BeforeClass
  @Parameters({"env"})
  public void preTestSteps(String env) {
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postDeviceRegistrationBodyPath"
  })
  @Test(priority = 1)
  public void Verify_Device_Registration(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postDeviceRegistrationBodyPath)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postDownloadGeofenceBodyPath"
  })
  @Test
  public void Verify_Download_Geofence(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postDownloadGeofenceBodyPath)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postGeofenceEntryBodyPath"
  })
  @Test(priority = 2)
  public void Verify_Geofence_Entry(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postGeofenceEntryBodyPath)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test
  public void Verify_Get_Attribute_Metadata(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postDeviceAttributesBodyPath",
    "deviceAttributeName",
    "deviceAttributeValue"
  })
  @Test
  public void Verify_Create_Device_Attributes(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postDeviceAttributesBodyPath,
      String deviceAttributeName,
      String deviceAttributeValue)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "deviceAttributeName",
    "deviceAttributeValue"
  })
  @Test
  public void Verify_Get_Device_Attributes(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String deviceAttributeName,
      String deviceAttributeValue)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postGeofenceExitBodyPath"
  })
  @Test
  public void Verify_Geofence_Exit(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postGeofenceExitBodyPath)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postStaticIdSetBodyPath"
  })
  @Test
  public void Verify_Set_StaticId(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postStaticIdSetBodyPath)
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

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postDeviceRegistrationBodyPath"
  })
  @Test
  public void Verify_Device_Unregistration(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postDeviceRegistrationBodyPath)
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
}
