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
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** Created by VinayKarumuri on 5/22/17. */
public class Campaigns {

  private static String serviceEndPoint = null;
  private static String capturedCampaignId;
  private static String capturedCampaignId1;
  private static Logger log = Logger.getLogger(Campaigns.class);
  private String xAuth = null;
  FileUtils fileUtils = new FileUtils();
  AuthHeader auth = new AuthHeader();

  @BeforeClass
  @Parameters({"env"})
  public void preTestSteps(String env) {
    if ("PROD".equalsIgnoreCase(env)) {
      serviceEndPoint = MeAPI_Constants.SERVICE_ENT_POINT_PROD;
    } else if ("STAGE".equalsIgnoreCase(env)) {
      serviceEndPoint = MeAPI_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
      Assert.fail("Environment is not set properly. Please check your testng xml file");
    }
  }

  @Parameters({
    "campaignId",
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test(priority = 1)
  public void Verify_Get_Campaign_By_Id(
      String campaignId,
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId) {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT + campaignId;

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
    response.then().body("id", is(Integer.parseInt(campaignId)));
    response.then().body(("any { it.key == 'targetTimeZone'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'createdById'}"), is(true));
    response.then().body(("any { it.key == 'notificationTitle'}"), is(true));
    response.then().body(("any { it.key == 'actionHTML'}"), is(true));
    response.then().body(("any { it.key == 'targetedEntryLocationTags'}"), is(true));
    response.then().body(("any { it.key == 'targetedEntryEndDate'}"), is(true));
    response.then().body(("any { it.key == 'actionType'}"), is(true));
    response.then().body(("any { it.key == 'actionTitle'}"), is(true));
    response.then().body(("any { it.key == 'repeatFrequency'}"), is(true));
    response.then().body(("any { it.key == 'status'}"), is(true));
    response.then().body(("any { it.key == 'campaignName'}"), is(true));
    response.then().body(("any { it.key == 'notificationMessage'}"), is(true));
    response.then().body(("any { it.key == 'locationTags'}"), is(true));
    response.then().body(("any { it.key == 'appId'}"), is(true));
    response.then().body(("any { it.key == 'startDate'}"), is(true));
    response.then().body(("any { it.key == 'campaignType'}"), is(true));
    response.then().body(("any { it.key == 'messageMetadata'}"), is(true));
    response.then().body(("any { it.key == 'profiles'}"), is(true));
  }

  @Parameters({
    "campaignId",
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test(priority = 2)
  public void Verify_Get_Campaign_By_InvalidId(
      String campaignId,
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId) {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT + "000";

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

  @Parameters({
    "campaignType",
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test(priority = 3)
  public void Verify_Collection_Of_Campaigns_By_CampaignType(
      String campaignType,
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId) {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;
    String queryParameters = "campaignType=" + campaignType;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

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
            .queryParam("campaignType", campaignType)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON response Pay load validations
    response.then().body(("any { it.key == 'totalCount'}"), is(true));
    response.then().body(("any { it.key == 'resultCount'}"), is(true));
    response.then().body(("any { it.key == 'offset'}"), is(true));
    response.then().body("items.size()", is(greaterThan(0)));
    response.then().body("items.campaignType", everyItem(is("BROADCAST")));
  }

  @Parameters({
    "status",
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test(priority = 4)
  public void Verify_Collection_Of_Campaigns_By_Status(
      String status,
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId) {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;
    String queryParameters = "status=" + status;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

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
            .queryParam("status", status)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON response Pay load validations
    response.then().body(("any { it.key == 'totalCount'}"), is(true));
    response.then().body(("any { it.key == 'resultCount'}"), is(true));
    response.then().body(("any { it.key == 'offset'}"), is(true));
    response.then().body("items.size()", is(greaterThan(0)));
    response.then().body("items.status", everyItem(is("SCHEDULED")));
  }

  @Parameters({
    "sortBy",
    "sortOrder",
    "limit",
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test(priority = 5)
  public void Verify_Collection_Of_Campaigns_SortByStartDate(
      String sortBy,
      String sortOrder,
      String limit,
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId) {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;
    String queryParameters = "sortBy=" + sortBy + "&sortOrder=" + sortOrder + "&limit=" + limit;

    // Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETERS: GET-" + queryParameters);

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
            .queryParams("sortBy", sortBy, "sortOrder", sortOrder, "limit", limit)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON response Pay load validations
    response.then().body("resultCount", is(Integer.parseInt(limit)));
    response.then().body(("any { it.key == 'totalCount'}"), is(true));
    response.then().body(("any { it.key == 'resultCount'}"), is(true));
    response.then().body(("any { it.key == 'offset'}"), is(true));
    response.then().body("items.size()", is(greaterThan(0)));
    List<Integer> CampaignsList = response.then().extract().jsonPath().getList("items.startDate");
    Assert.assertFalse(
        CampaignsList.stream().sorted().collect(Collectors.toList()).equals(CampaignsList));
  }

  @Parameters({
    "campaignId",
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId"
  })
  @Test(priority = 6)
  public void Verify_Get_Campaign_Status(
      String campaignId,
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId) {

    // Request Details
    String requestURL =
        serviceEndPoint + MeAPI_Constants.CAMPAIGN_STATUS_END_POINT.replace("<id>", campaignId);

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
    response.then().body("status", isOneOf("SCHEDULED", "ACTIVE", "COMPLETED"));
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 7)
  public void verify_Create_Campaign(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String campaignName = "CampaignName" + HelperMethods.getDateAsString();
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("campaignName", campaignName);

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
            // .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    response.then().body(("any { it.key == 'id'}"), is(true));
    response.then().body(("any { it.key == 'targetTimeZone'}"), is(true));
    response.then().body(("any { it.key == 'createdById'}"), is(true));
    response.then().body(("any { it.key == 'notificationTitle'}"), is(true));
    response.then().body(("any { it.key == 'endDate'}"), is(true));
    response.then().body(("any { it.key == 'actionHTML'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'modifiedById'}"), is(true));
    response.then().body(("any { it.key == 'targetedEntryLocationTags'}"), is(true));
    response.then().body(("any { it.key == 'targetedEntryEndDate'}"), is(true));
    response.then().body(("any { it.key == 'actionType'}"), is(true));
    response.then().body(("any { it.key == 'actionTitle'}"), is(true));
    response.then().body(("any { it.key == 'repeatFrequency'}"), is(true));
    response.then().body(("any { it.key == 'status'}"), is(true));
    response.then().body(("any { it.key == 'campaignName'}"), is(true));
    response.then().body(("any { it.key == 'notificationMessage'}"), is(true));
    response.then().body(("any { it.key == 'locationTags'}"), is(true));
    response.then().body(("any { it.key == 'appId'}"), is(true));
    response.then().body(("any { it.key == 'startDate'}"), is(true));
    response.then().body(("any { it.key == 'campaignType'}"), is(true));
    response.then().body(("any { it.key == 'messageMetadata'}"), is(true));
    response.then().body(("any { it.key == 'profiles'}"), is(true));
    response.then().body("campaignName", is(campaignName));
    response.then().body("startDate", is(startDate));
    response.then().body("endDate", is(endDate));
    response.then().body("targetedEntryEndDate", is(targetedEntryEndDate));

    capturedCampaignId = response.then().extract().path("id").toString();
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 8)
  public void verify_Create_Campaign_WithNoNameInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("campaignName");

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
            + "Required field [campaignName] is missing; please provide field and value in your request.");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 9)
  public void verify_Create_Campaign_WithNoStartDateInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("startDate");

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
            + "Required field [startDate] is missing; please provide field and value in your request.");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 10)
  public void verify_Create_Campaign_WithNoEndDateInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("endDate");

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
            + "Required field [endDate] is missing; please provide field and value in your request.");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 11)
  public void verify_Create_Campaign_WithNoDescriptionInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
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
            .statusCode(200)
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    capturedCampaignId1 = response.then().extract().path("id").toString();
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 12)
  public void verify_Create_Campaign_WithNoCampaignTypeInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("campaignType");

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
            + "Required field [campaignType] is missing; please provide field and value in your request.");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 13)
  public void verify_Create_Campaign_WithNoRepeatFrequencyInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("repeatFrequency");

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
        response.asString(), "requirement failed: repeatFrequency required in the request");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 14)
  public void verify_Create_Campaign_WithNoTargetTimeZoneInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("targetTimeZone");

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
        response.asString(), "requirement failed: targetTimeZone not found in the request");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 15)
  public void verify_Create_Campaign_WithNoNotificationTitleInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("notificationTitle");

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
        response.asString(), "requirement failed: notificationTitle not found in the request");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 16)
  public void verify_Create_Campaign_WithNoNotificationMessageInBody(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String startDate = HelperMethods.addDaystoCurrentDate(365);
    String endDate = HelperMethods.addDaystoCurrentDate(369);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(366);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.remove("notificationMessage");

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
        response.asString(), "requirement failed: notificationMessage not found in the request");
  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 17)
  public void verify_Update_Campaign(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT + capturedCampaignId;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String campaignName = "CampaignName" + HelperMethods.getDateAsString() + "Updated";
    String startDate = HelperMethods.addDaystoCurrentDate(366);
    String endDate = HelperMethods.addDaystoCurrentDate(370);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(367);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("campaignName", campaignName);
    requestBodyJSONObject.put("id", Integer.parseInt(capturedCampaignId));

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
    response.then().body(("any { it.key == 'id'}"), is(true));
    response.then().body(("any { it.key == 'targetTimeZone'}"), is(true));
    response.then().body(("any { it.key == 'createdById'}"), is(true));
    response.then().body(("any { it.key == 'notificationTitle'}"), is(true));
    response.then().body(("any { it.key == 'endDate'}"), is(true));
    response.then().body(("any { it.key == 'actionHTML'}"), is(true));
    response.then().body(("any { it.key == 'description'}"), is(true));
    response.then().body(("any { it.key == 'enabled'}"), is(true));
    response.then().body(("any { it.key == 'modifiedById'}"), is(true));
    response.then().body(("any { it.key == 'targetedEntryLocationTags'}"), is(true));
    response.then().body(("any { it.key == 'targetedEntryEndDate'}"), is(true));
    response.then().body(("any { it.key == 'actionType'}"), is(true));
    response.then().body(("any { it.key == 'actionTitle'}"), is(true));
    response.then().body(("any { it.key == 'repeatFrequency'}"), is(true));
    response.then().body(("any { it.key == 'status'}"), is(true));
    response.then().body(("any { it.key == 'campaignName'}"), is(true));
    response.then().body(("any { it.key == 'notificationMessage'}"), is(true));
    response.then().body(("any { it.key == 'locationTags'}"), is(true));
    response.then().body(("any { it.key == 'appId'}"), is(true));
    response.then().body(("any { it.key == 'startDate'}"), is(true));
    response.then().body(("any { it.key == 'campaignType'}"), is(true));
    response.then().body(("any { it.key == 'messageMetadata'}"), is(true));
    response.then().body(("any { it.key == 'profiles'}"), is(true));
    response.then().body("campaignName", is(campaignName));
    response.then().body("startDate", is(startDate));
    response.then().body("endDate", is(endDate));
    response.then().body("targetedEntryEndDate", is(targetedEntryEndDate));

    // capturedCampaignId = response.then().extract().path("id").toString();

  }

  @Parameters({
    "clientId_android_access_key",
    "clientId_android_signature_key",
    "orgId",
    "clientId",
    "postCampaignRequestBodyPath"
  })
  @Test(priority = 18)
  public void verify_Delete_Campaign(
      String clientId_android_access_key,
      String clientId_android_signature_key,
      String orgId,
      String clientId,
      String postCampaignRequestBodyPath)
      throws IOException, NullPointerException {

    // Request Details
    String requestURL = serviceEndPoint + MeAPI_Constants.CAMPAIGNS_END_POINT + capturedCampaignId;

    String requestBody = fileUtils.getJsonTextFromFile(postCampaignRequestBodyPath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    String campaignName = "CampaignName" + HelperMethods.getDateAsString() + "Updated";
    String startDate = HelperMethods.addDaystoCurrentDate(366);
    String endDate = HelperMethods.addDaystoCurrentDate(370);
    String targetedEntryEndDate = HelperMethods.addDaystoCurrentDate(367);
    requestBodyJSONObject.put("startDate", startDate);
    requestBodyJSONObject.put("endDate", endDate);
    requestBodyJSONObject.put("targetedEntryEndDate", targetedEntryEndDate);
    requestBodyJSONObject.put("campaignName", campaignName);
    requestBodyJSONObject.put("id", Integer.parseInt(capturedCampaignId));
    requestBodyJSONObject.put("enabled", false);

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

    log.info(response.statusCode());

    // printing response
    log.info("RESPONSE:" + response.asString());

    // JSON Response Validations
    response.then().body("status", is("STOPPED"));
    response.then().body("enabled", is(false));
  }
}
