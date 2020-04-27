package com.org.core.api.tests;

import com.org.core_api.constants.CoreAPI_Constants;
import com.org.utility.FileUtils;
import com.org.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/** Created by pkovurru on 11/17/16. */
public class Organization {

  static Logger log;
  public static String capturedNewORGANIZATION_ID;
  FileUtils fileUtils = new FileUtils();
  public static String SERVICE_END_POINT = null;

  @BeforeSuite
  @Parameters("env")
  public void setEnv(String env){
    if ("PROD".equalsIgnoreCase(env)) {
      SERVICE_END_POINT = CoreAPI_Constants.SERVICE_ENT_POINT_PROD;
    } else if ("STAGE".equalsIgnoreCase(env)) {
      SERVICE_END_POINT = CoreAPI_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
      Assert.fail("Environment is not set properly. Please check your testng xml file");
    }
  }

  @BeforeClass
  public void preTestSteps() {
    log = Logger.getLogger(Client.class);
  }



  @Parameters({"orgId", "validAuthorization"})
  @Test(priority = 1)
  public void verify_Get_Organization_Details(String orgId, String validAuthorization ) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + orgId;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization",validAuthorization)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(orgId)));
    response.then().body("data.containsKey('name')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters("validAuthorization")
  @Test(priority = 2)
  public void verify_Get_Organization_Details_InvalidOrgID(String validAuthorization ) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATION_END_POINT + "000";

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .get(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.message", is("The specified organization does not exist."));
  }

  @Parameters({"orgId","invalidAuthorization"})
  @Test(priority = 3)
  public void verify_Get_Organization_Details_InvalidAuthorization(String invalidAuthorization, String orgId) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + orgId;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", invalidAuthorization)
            .get(requestURL)
            .then()
            .statusCode(401)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("status", is("access denied"));
    response.then().body("msg", is("invalid token"));
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 4)
  public void verify_Get_Collection_Of_Organizations(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARMETERS-" + queryParameters);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .queryParam(queryParameters)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("containsKey('offset')", is(true));
    response.then().body("containsKey('totalCount')", is(true));
    response.then().body("containsKey('resultCount')", is(true));

    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));

    //validating that all org names returned by this endpoint has the case insensitive string in query parameters.
    JSONObject queryParametersJSONObject = new JSONObject(queryParameters);
    String orgNametoSearch = (String)queryParametersJSONObject.get("name");
    String orgNameRegExText = ".*" + orgNametoSearch + ".*" + "|" + ".*" + orgNametoSearch.toLowerCase() + ".*" + "|"+ ".*" + orgNametoSearch.toUpperCase() + ".*";

    for (int i = 0; i <= response.then().extract().jsonPath().getList("data").size() - 1; i++) {
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.name[" + i + "]")
              .toString()
              .matches(orgNameRegExText));
    }
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 5)
  public void verify_Get_Collection_Of_Organizations_NameWithEmptyString(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMERTERS:" + queryParameters);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .queryParam(queryParameters)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("containsKey('offset')", is(true));
    response.then().body("containsKey('totalCount')", is(true));
    response.then().body("containsKey('resultCount')", is(true));

    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 6)
  public void verify_Get_Collection_Of_Organizations_Pagination(String validAuthorization, String queryParameters ) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY-PARAMERTERS:" + queryParameters);
    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .queryParam(queryParameters)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("offset", is("1"));
    response.then().body("containsKey('totalCount')", is(true));
    response.then().body("containsKey('resultCount')", is(true));

    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));

    //validating that all org names returned by this endpoint has the case insensitive string present in query parameters
    JSONObject queryParametersJSONObject = new JSONObject(queryParameters);
    String orgNametoSearch = (String) queryParametersJSONObject.get("name");
    String orgNameRegExText = ".*" + orgNametoSearch + ".*" + "|" + ".*" + orgNametoSearch.toLowerCase() + ".*" + "|" + ".*" + orgNametoSearch.toUpperCase() + ".*";

    for (int i = 0; i <= response.then().extract().jsonPath().getList("data").size() - 1; i++) {
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.name[" + i + "]")
              .toString()
              .matches(orgNameRegExText));
    }
  }
  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 7)
  public void verify_Get_Collection_Of_Organizations_Pagination_EmptyName(String validAuthorization, String queryParameters ) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .queryParam(queryParameters)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("offset", is("0"));
    response.then().body("containsKey('totalCount')", is(true));
    response.then().body("containsKey('resultCount')", is(true));

    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));
  }

  @Parameters({"validAuthorization", "organizationRequestBodyFilePath"})
  @Test(priority = 8)
  public void verify_Post_Create_New_Organization(String validAuthorization, String organizationRequestBodyFilePath ) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(organizationRequestBodyFilePath);
    String newOrgName = "QAAPIAutomation"+ HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", newOrgName);
    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .request()
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //capturing created client ID
    capturedNewORGANIZATION_ID = response.then().extract().path("data.id").toString();
    log.info("Captured new Organization ID:" + capturedNewORGANIZATION_ID);

    //JSON response Pay load validations
    response.then().body("data.id", is(notNullValue()));
    response.then().body("data.name", is(newOrgName));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters({"validAuthorization", "organizationRequestBodyFilePath"})
  @Test(priority = 9)
  public void verify_Post_Create_New_Organization_EmptyName_InRequestBody(String validAuthorization, String organizationRequestBodyFilePath ) throws IOException{
  //Request Details
  String requestURL =
      SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

  String requestBody = fileUtils.getJsonTextFromFile(organizationRequestBodyFilePath);
  JSONObject requestBodyJSONObject = new JSONObject(requestBody);
  JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
  requestBodyData.put("name", "");
  //Printing Request Details
  log.info("REQUEST-URL:POST-" + requestURL);
  log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());

  //Extracting response after status code validation
  Response response =
      given()
          .header("Content-Type", "application/json")
          .header("Authorization", validAuthorization)
          .request()
          .body(requestBodyJSONObject.toString())
          .post(requestURL)
          .then()
          .statusCode(400)
          .extract()
          .response();

  //printing response
  log.info("RESPONSE:" + response.asString());

  //JSON response Pay load validations
  response.then().body("error.messages.name", is("The name must be at least 1 character long."));
}

  @Parameters({"validAuthorization", "organizationPutRequestBodyFilePath"})
  @Test(priority = 10)
  public void verify_Put_Update_Organization(String validAuthorization, String organizationPutRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;
    String newOrgName = "QAAPIAutomation"+ HelperMethods.getDateAsString() + "Updated";

    String requestBody = fileUtils.getJsonTextFromFile(organizationPutRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", newOrgName);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());


    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .request()
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewORGANIZATION_ID)));
    response.then().body("data.name", is(newOrgName));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters({"validAuthorization", "organizationPutRequestBodyFilePath"})
  @Test(priority = 11)
  public void verify_Put_Update_Organization_EmptyName_InRequestBody(String validAuthorization, String organizationPutRequestBodyFilePath) throws IOException{

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;

    String requestBody = fileUtils.getJsonTextFromFile(organizationPutRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", "");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .request()
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.messages.name", is("The name must be at least 1 character long."));
  }

  @Parameters({"validAuthorization", "organizationPutRequestBodyFilePath"})
  @Test(priority = 12)
  public void verify_Put_Update_Organization_EmptyServices_InRequestBody(String validAuthorization, String organizationPutRequestBodyFilePath) throws IOException{

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;


    String newOrgName = "QAAPIAutomation"+ HelperMethods.getDateAsString() + "Updated";
    String requestBody = fileUtils.getJsonTextFromFile(organizationPutRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", newOrgName);
    requestBodyData.remove("services");
    requestBodyData.append("services", "[]");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .request()
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewORGANIZATION_ID)));
    response.then().body("data.name", is(newOrgName));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters({"validAuthorization", "organizationPutRequestBodyFilePath"})
  @Test(priority = 13)
  public void verify_Put_Update_Organization_InvalidOrganization(String validAuthorization, String organizationPutRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATION_END_POINT + "!@a3";

    String newOrgName = "QAAPIAutomation"+ HelperMethods.getDateAsString() + "Updated";
    String requestBody = fileUtils.getJsonTextFromFile(organizationPutRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", newOrgName);

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .request()
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.message", is("Organization not found."));
  }

  @Parameters("validAuthorization")
  @Test(priority = 14)
  public void verify_Delete_ExistingOrganization(String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            +capturedNewORGANIZATION_ID ;

    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    try {
      Response response =
          given()
              .header("Content-Type", "application/json")
              .header("Authorization", validAuthorization)
              .delete(requestURL)
              .then()
              .statusCode(200)
              .extract()
              .response();
    } catch (Exception ClientProtocolException)  {
      log.info("skipping ResponseParseException exception");
    }
  }
}
