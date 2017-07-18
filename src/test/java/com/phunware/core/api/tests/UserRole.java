package com.phunware.core.api.tests;

import com.phunware.core_api.constants.CoreAPI_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
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

/**
 * Created by pkovurru on 11/18/16.
 */
public class UserRole {

  static Logger log;
  public static String capturedNewUserRoleID;
  FileUtils fileUtils = new FileUtils();
  public static String SERVICE_END_POINT = null;

  @BeforeSuite
  @Parameters("env")
  public void setEnv(String env) {
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

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 1)
  public void verify_Get_User_Role(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETER-" + queryParameters);

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
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));

    //validating that all org id's returned by this endpoint does not have the correct orgid

    JSONObject requestBodyJSONObject = new JSONObject(queryParameters);
    Integer orgNametoSearch = (Integer) requestBodyJSONObject.get("org_id");

    for (int i = 0;
         i <= response.then().extract().jsonPath().getList("data.org_id").size() - 1;
         i++) {
      response.then().body("data.org_id[" + i + "]", is(orgNametoSearch));
    }
  }


  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 2)
  public void verify_Get_UserRole_NonExistant_OrgID(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETER-" + queryParameters);


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
    response.then().body("offset", is(0));
    response.then().body("totalCount", is(0));
    response.then().body("resultCount", is(0));
    Assert.assertTrue(response.then().extract().jsonPath().getList("data").size() == 0);
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 3)
  public void verify_Get_UserRole_Invalid_parameter_Structure(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

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
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.message", is("Could not parse the request query."));
  }

  @Parameters({"invalidAuthorization", "queryParameters"})
  @Test(priority = 4)
  public void verify_Get_USerRole_InvalidAuth(String invalidAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", invalidAuthorization)
            .queryParam(queryParameters)
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
  @Test(priority = 5)
  public void verify_Get_UserRole_Pagination(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

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
    response.then().body("containsKey('offset')", is(true));
    response.then().body("containsKey('totalCount')", is(true));

    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));

    //validating that all org id's returned by this endpoint does not have the value other than 128
    JSONObject queryParametersJSONObject = new JSONObject(queryParameters);
    String orgNametoSearch = (String) queryParametersJSONObject.get("org_id");
    for (int i = 0;
         i <= response.then().extract().jsonPath().getList("data.org_id").size() - 1;
         i++) {
      response.then().body("data.org_id[" + i + "]", is(Integer.parseInt(orgNametoSearch)));
    }

    //result count validation.
    Assert.assertTrue(response.then().extract().jsonPath().getList("data").size() <= 15);
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 6)
  public void verify_Get_UserRole_Pagination_NonExistant_OrgID(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

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
    response.then().body("offset", is(0));
    response.then().body("totalCount", is(0));
    response.then().body("resultCount", is(0));
    Assert.assertTrue(response.then().extract().jsonPath().getList("data").size() == 0);
  }

  @Parameters({"invalidAuthorization", "queryParameters"})
  @Test(priority = 7)
  public void verify_Get_UserRole_Pagination_InvalidAuth(String invalidAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", invalidAuthorization)
            .queryParam(queryParameters)
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

  @Parameters({"validAuthorization", "orgId", "userRolePostRequestBodyFilePath"})
  @Test(priority = 8)
  public void verify_Post_Create_New_UserRole(String validAuthorization, String orgId, String userRolePostRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userRolePostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String userRoleName = "QAAPIAutomationRole" + HelperMethods.getDateAsString();
    requestBodyData.put("name", userRoleName);
    requestBodyData.put("org_id", orgId);

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
    capturedNewUserRoleID = response.then().extract().path("data.id").toString();
    log.info("Captured new User Role ID:" + capturedNewUserRoleID);

    //JSON response Pay load validations
    response.then().body("data.id", is(notNullValue()));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.name", is(userRoleName));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  //BUG - PLAT-5540

  @Parameters({"validAuthorization", "orgId", "userRolePostRequestBodyFilePath"})
  @Test(priority = 9)
  public void verify_Post_Create_New_UserRole_EmptyName(String validAuthorization, String orgId, String userRolePostRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userRolePostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", "");
    requestBodyData.put("org_id", orgId);

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
    log.info("RESPONSE:" + response.statusCode());

    //JSON response Pay load validations
    response.then().body("error.messages.name", is("The name must be at least 1 character long."));
  }


  @Parameters({"invalidAuthorization", "orgId", "userRolePostRequestBodyFilePath"})
  @Test(priority = 10)
  public void verify_Post_Create_New_UserRole_InvalidAuth(String invalidAuthorization, String orgId, String userRolePostRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userRolePostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String userRoleName = "QAAPIAutomationRole" + HelperMethods.getDateAsString();
    requestBodyData.put("name", userRoleName);
    requestBodyData.put("org_id", orgId);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", invalidAuthorization)
            .request()
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
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

  @Parameters({"validAuthorization", "orgId", "userRolePostRequestBodyFilePath"})
  @Test(priority = 11)
  public void verify_Put_Update_UserRole(String validAuthorization, String orgId, String userRolePostRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USER_ROLE_END_POINT
            + "/"
            + capturedNewUserRoleID;
    String requestBody = fileUtils.getJsonTextFromFile(userRolePostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String userRoleName = "QAAPIAutomationRole" + HelperMethods.getDateAsString() + "Updated";
    requestBodyData.put("name", userRoleName);
    requestBodyData.put("org_id", orgId);


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
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewUserRoleID)));
    response.then().body("data.name", is(userRoleName));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  //BUG - PLAT-5540
  @Parameters({"validAuthorization", "orgId", "userRolePostRequestBodyFilePath"})
  @Test(priority = 12)
  public void verify_Put_Update_UserRole_EmptyName_InRequestBody(String validAuthorization, String orgId, String userRolePostRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USER_ROLE_END_POINT
            + "/"
            + capturedNewUserRoleID;

    String requestBody = fileUtils.getJsonTextFromFile(userRolePostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String userRoleName = null;
    requestBodyData.put("name", userRoleName);
    requestBodyData.put("org_id", orgId);


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
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewUserRoleID)));
    response.then().body("data.containsKey('org_id')", is(true));
    response.then().body("data.containsKey('name')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));


  }

  @Parameters({"validAuthorization", "orgId", "userRolePostRequestBodyFilePath"})
  @Test(priority = 13)
  public void verify_Put_Update_UserRole_InvalidId(String validAuthorization, String orgId, String userRolePostRequestBodyFilePath) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT + "/" + "!@a3";

    String requestBody = fileUtils.getJsonTextFromFile(userRolePostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String userRoleName = "QAAPIAutomationRole" + HelperMethods.getDateAsString() + "Updated";
    requestBodyData.put("name", userRoleName);
    requestBodyData.put("org_id", orgId);


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
    response.then().body("error.message", is("Role not found."));
  }


  @Parameters("validAuthorization")
  @Test(priority = 14)
  public void verify_Delete_ExistingUserRole(String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USER_ROLE_END_POINT
            + "/"
            + capturedNewUserRoleID;

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
    } catch (Exception ClientProtocolException) {
      log.info("skipping ResponseParseException exception");
    }
  }
}
