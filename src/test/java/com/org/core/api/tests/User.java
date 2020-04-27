package com.org.core.api.tests;

import com.org.core_api.constants.CoreAPI_Constants;
import com.org.utility.FileUtils;
import com.org.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/** Created by pkovurru on 11/21/16. */
public class User {

  static Logger log;
  public static String SERVICE_END_POINT = null;
  public String dynamicValue;
  public String dynamicValue1;
  public String dynamicValue2;
  public String dynamicValue3;
  public String dynamicValue4;
  public String dynamicValue5;


  public static String capturedNewUserID;
  public static String capturedNewUserID1;
  public static String capturedNewUserID2;
  FileUtils fileUtils = new FileUtils();


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
    dynamicValue = "Test" + Math.random();
    dynamicValue1 = "Test" + Math.random();
    dynamicValue2 = "Test" + Math.random();
    dynamicValue3 = "Test" + Math.random();
    dynamicValue4 = "Test" + Math.random();
    dynamicValue5 = "Test" + Math.random();
  }

  @Parameters({"validAuthorization","userId"})
  @Test(priority = 1)
  public void verify_Get_User(String validAuthorization, String userId) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/" + Integer.parseInt(userId);

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(userId)));
    response.then().body("data.containsKey('org_id')", is(true));
    response.then().body("data.containsKey('role_id')", is(true));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.containsKey('first_name')", is(true));
    response.then().body("data.containsKey('last_name')", is(true));
    response.then().body("data.containsKey('email')", is(true));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters("validAuthorization")
  @Test(priority = 2)
  public void verify_Get_User_InvalidUserID(String validAuthorization ) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/" + "12c";

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
    response.then().body("error.message", is("The specified user does not exist."));
  }

  @Parameters({"invalidAuthorization","userId"})
  @Test(priority = 3)
  public void verify_Get_User_InvalidAuth(String invalidAuthorization, String userId) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/" + userId;

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
  public void verify_Get_Authenticate_User(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.AUTHENTICATE_USER_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETERS-" + queryParameters);


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
    response.then().body("data.containsKey('id')", is(true));
    response.then().body("data.containsKey('org_id')", is(true));
    response.then().body("data.containsKey('role_id')", is(true));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.containsKey('first_name')", is(true));
    response.then().body("data.containsKey('last_name')", is(true));
    response.then().body("data.containsKey('email')", is(true));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));

    response.then().body("session.containsKey('id')", is(true));
    response.then().body("session.containsKey('created_at')", is(true));
    response.then().body("session.containsKey('expires_at')", is(true));
  }


  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 5)
  public void verify_Get_Authenticate_User_Invalid_Credentials(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.AUTHENTICATE_USER_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETERS-" + queryParameters);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .queryParam(queryParameters)
            .get(requestURL)
            .then()
            .statusCode(401)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 6)
  public void verify_Get_Authenticate_User_Empty_Credentials(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.AUTHENTICATE_USER_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETERS-" + queryParameters);


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
    response.then().body("error.message", is("Missing email address."));
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 7)
  public void verify_Get_UserPagination(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETERS-" + queryParameters);

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
    response.then().body("data.flatten().any {it.containsKey('org_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('role_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('google_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('email') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('time_zone') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));

    //validating that all org id's returned by this endpoint does not have the value other than 128
    JSONObject queryParametersJSONObject = new JSONObject(queryParameters);
    String name = (String) queryParametersJSONObject.get("name");
    String names[] = name.split(" ");


   for (int i = 0;
        i <= response.then().extract().jsonPath().getList("data.org_id").size() - 1;
        i++) {
      //case insensitive search
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.first_name[" + i + "]")
              .toString()
              .matches("(?i:.*?" + names[1] +".*)"));
      //case insensitive search
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.last_name[" + i + "]")
              .toString()
              .matches("(?i:.*?" +names[0]+".*)"));
    }
  }

  @Parameters({"validAuthorization", "queryParameters"})
  @Test(priority = 8)
  public void verify_Get_UserPagination_NullName(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);
    log.info("QUERY PARAMETERS-" + queryParameters);

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
    response.then().body("data.flatten().any {it.containsKey('org_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('role_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('first_name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('last_name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('google_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('email') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('time_zone') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));
  }



  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId", "newUserEmailId"})
  @Test(priority = 9)
  public void verify_post_CreateNewUser(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId, String newUserEmailId) throws IOException{

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "qa";
    String lastName = "automation" + HelperMethods.getDateAsString();

    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email",newUserEmailId);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST BODY - POST" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //capturing Created new User ID
    capturedNewUserID = response.then().extract().path("data.id").toString();
    log.info("Captured created user ID:" + capturedNewUserID);

    //JSON response Pay load validations
    response.then().body("data.containsKey('id')", is(true));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.role_id", is(roleId));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(firstName));
    response.then().body("data.last_name", is(lastName));
    response.then().body("data.email", is(newUserEmailId));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 10)
  public void verify_post_CreateNewUser_Empty_FirstNameIn_Body(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "";
    String lastName = "automation" + HelperMethods.getDateAsString();
    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email",firstName+lastName+"@gmail.com");
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST" + requestBodyJSONObject.toString());


    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());


    //capturing Created new User ID
    capturedNewUserID1 = response.then().extract().path("data.id").toString();
    log.info("Captured created user ID:" + capturedNewUserID1);

    //JSON response Pay load validations
    response.then().body("data.containsKey('id')", is(true));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.role_id", is(roleId));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(""));
    response.then().body("data.last_name", is(lastName));
    response.then().body("data.email", is(firstName+lastName+"@gmail.com"));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 11)
  public void verify_post_CreateNewUser_Empty_LastNameIn_Body(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "qa"+ HelperMethods.getDateAsString();
    String lastName = "";
    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email",firstName+lastName+"@gmail.com");
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //capturing Created new User ID
    capturedNewUserID2 = response.then().extract().path("data.id").toString();
    log.info("Captured created user ID:" + capturedNewUserID2);

    //JSON response Pay load validations
    response.then().body("data.containsKey('id')", is(true));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.role_id", is(roleId));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(firstName));
    response.then().body("data.last_name", is(lastName));
    response.then().body("data.email", is(firstName+lastName+"@gmail.com"));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }


  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 12)
  public void verify_post_CreateNewUser_Empty_email(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "firstName";
    String lastName = "lastName";
    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email","");
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST-" + requestBodyJSONObject.toString());


    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.messages.email", is("Invalid email address."));
  }

  //PLAT-5633
  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 13)
  public void verify_post_CreateNewUser_Null_Timezone(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "firstName";
    String lastName = "lastName";
    String timezone = null;
    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email","notimezone" + HelperMethods.getDateAsString() + "@gmail.com");
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);
    requestBodyData.put("time_zone",timezone);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations

  }

  //Bug PLAT-5634

  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 14)
  public void verify_post_CreateNewUser_Null_isActive(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{


    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;
    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "firstName";
    String lastName = "lastName";
    String isactive = null;
    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email","notactiveuser" + HelperMethods.getDateAsString() + "@gmail.com");
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);
    requestBodyData.put("is_active", isactive);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST-" + requestBodyJSONObject.toString());


    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations

  }

  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 15)
  public void verify_post_CreateNewUser_Empty_Password(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "firstName";
    String lastName = "lastName";
    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email","nopassword@gmail.com");
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);
    requestBodyData.put("password","");

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:POST-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response
        .then()
        .body(
            "error.messages.password",
            is(
                "Password must be at least 8 characters long, Password should contain at least one lower case character, Password should contain at least one upper case character, Password should contain at least one digit"));
  }

  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId", "newUserEmailId"})
  @Test(priority = 16)
  public void verify_put_UpdateExistingUser(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId, String newUserEmailId) throws IOException{


    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = "qa";
    String lastName = "automation" + HelperMethods.getDateAsString() + "Updated";

    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email",newUserEmailId);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewUserID)));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.role_id", is(roleId));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(firstName));
    response.then().body("data.last_name", is(lastName));
    response.then().body("data.email", is(newUserEmailId));
    response.then().body("data.time_zone", is("America/Chicago"));
    response.then().body("data.is_active", is(1));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters({"validAuthorization", "userPostRequestBodyFilePath" , "orgId", "roleId"})
  @Test(priority = 17)
  public void verify_put_UpdateExistingUser_NullEmail_EmptyPassword_ValuesInBody(String validAuthorization, String userPostRequestBodyFilePath, String orgId, String roleId) throws IOException{


    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    String requestBody = fileUtils.getJsonTextFromFile(userPostRequestBodyFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    String firstName = null;
    String lastName = null;
    String updatedEmailId = null;

    requestBodyData.put("first_name",firstName);
    requestBodyData.put("last_name",lastName);
    requestBodyData.put("email",updatedEmailId);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("role_id", roleId);
    requestBodyData.put("password", "");

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization",validAuthorization)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    //response.then().body("error.messages.email", is("Invalid email address."));
    response
        .then()
        .body(
            "error.messages.password",
            is(
                "Password must be at least 8 characters long, Password should contain at least one lower case character, Password should contain at least one upper case character, Password should contain at least one digit"));
  }

  @Parameters({"validAuthorization", "userPutChangePasswordFilePath"})
  @Test(priority = 18)
  public void verify_put_Change_Password_ExistingUser(String validAuthorization, String userPutChangePasswordFilePath) throws IOException{

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID
            + "/change-password";
    String requestBody = fileUtils.getJsonTextFromFile(userPutChangePasswordFilePath);


    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + requestBody);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", validAuthorization)
              .body(requestBody)
              .put(requestURL)
              .then()
              .statusCode(200)
              .extract()
              .response();

      //printing response
      log.info("RESPONSE:" + response.asString());

      //JSON response Pay load validations
      Assert.assertEquals(response.asString(), "");
    } catch (Exception ClientProtocolException) {
      log.info(
          "Handling HttpResponseException which is caused when rest-assured is not able to parse response");
    }
  }

  @Parameters({"validAuthorization", "userPutChangePasswordFilePath"})
  @Test(priority = 19)
  public void verify_put_Change_Password_InvalidCurrentPassword(String validAuthorization, String userPutChangePasswordFilePath) throws IOException{

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID
            + "/change-password";

    String requestBody = fileUtils.getJsonTextFromFile(userPutChangePasswordFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("new_password", "aaaa");


    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.message", is("Current Password is invalid."));
  }

  @Parameters({"validAuthorization", "userPutResetPasswordFilePath", "newUserEmailId"})
  @Test(priority = 20)
  public void verify_put_Reset_Password_ExistingUser(String validAuthorization, String userPutResetPasswordFilePath, String newUserEmailId)throws IOException{

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/reset-password";

    String requestBody = fileUtils.getJsonTextFromFile(userPutResetPasswordFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("email", newUserEmailId);

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + requestBodyJSONObject.toString());

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", validAuthorization)
              .body(requestBodyJSONObject.toString())
              .put(requestURL)
              .then()
              .statusCode(200)
              .extract()
              .response();

      //printing response
      log.info("RESPONSE:" + response.asString());

      //JSON response Pay load validations
      Assert.assertEquals(response.asString(), "");
    } catch (Exception ClientProtocolException) {
      log.info(
          "Handling HttpResponseException which is caused when rest-assured is not able to parse response");
    }
  }

  @Parameters({"validAuthorization", "userPutResetPasswordFilePath"})
  @Test(priority = 21)
  public void verify_put_Reset_Password_InvalidBodyParameters(String validAuthorization, String userPutResetPasswordFilePath) throws IOException{

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.RESET_USER_END_POINT;
    File file = new File(userPutResetPasswordFilePath);
    String requestBody = fileUtils.getJsonTextFromFile(userPutResetPasswordFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("email", "nouserlikethis@gmail.com");

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.message", is("That email address does not exist."));
  }
/*
// This is commented out for future work. Please see PLAT-5602 for more details.

  @Parameters("validAuthorization")
  @Test(priority = 22)
  public void verify_put_Submit_New_Password_ExistingUser(String validAuthorization ) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/reset-password/{key}";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + submit_New_Password_Body);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", validAuthorization)
              .body(submit_New_Password_Body)
              .put(requestURL)
              .then()
              .statusCode(200)
              .extract()
              .response();

      //printing response
      log.info("RESPONSE:" + response.asString());

      //JSON response Pay load validations
      Assert.assertEquals(response.asString(), "");

    } catch (Exception ClientProtocolException) {
      log.info(
          "Handling HttpResponseException which is caused when rest-assured is not able to parse response");
    }
  }
*/


  @Parameters("validAuthorization")
  @Test(priority = 23)
  public void verify_delete_ExistingUser(String validAuthorization ) throws ClientProtocolException {

    //Request Details
/*     String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            +2002 ;*/

    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    log.info("Deleting CapturedNewUserID1");

   //Request Details
    String requestURL1 =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID1;

    //Request Details
    String requestURL2 =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID2;


    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);
    log.info("REQUEST-URL:DELETE-" + requestURL1);
    log.info("REQUEST-URL:DELETE-" + requestURL2);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", validAuthorization)
              .delete(requestURL)
              .then()
              .statusCode(200)
              .extract()
             .response();

      log.info("Response CapturedNewUserID1");
      Response response1 =
          given()
              .header("Authorization", validAuthorization)
              .delete(requestURL1)
              .then()
              .statusCode(200)
              .extract()
              .response();

      Response response2 =
          given()
              .header("Authorization", validAuthorization)
              .delete(requestURL2)
              .then()
              .statusCode(200)
              .extract()
              .response();

      //printing response
     log.info("RESPONSE:" + response.asString());

      //JSON response Pay load validations
      Assert.assertEquals(response.asString(), "");
      Assert.assertEquals(response1.asString(), "");
      Assert.assertEquals(response2.asString(), "");


    } catch (Exception ClientProtocolException) {
      log.info(
          "Handling HttpResponseException which is caused when rest-assured is not able to parse response");
    }
  }


  @Parameters("validAuthorization")
  @Test(priority = 24)
  public void verify_delete_Invaliduser(String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Content-Type", "application/json")
              .header("Authorization", validAuthorization)
              .delete(requestURL)
              .then()
              .statusCode(400)
              .extract()
              .response();

      //printing response
      log.info("RESPONSE:" + response.asString());

      //JSON response Pay load validations
      Assert.assertEquals(response.asString(), "");

    } catch (Exception ClientProtocolException) {
      log.info(
          "Handling HttpResponseException which is caused when rest-assured is not able to parse response");
    }
  }

  @Parameters("invalidAuthorization")
  @Test(priority = 25)
  public void verify_delete_ExistingUser_InvalidAuth(String invalidAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;


    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Content-Type", "application/json")
              .header("Authorization", invalidAuthorization)
              .delete(requestURL)
              .then()
              .statusCode(401)
              .extract()
              .response();


      //printing response
      log.info("RESPONSE:" + response.asString());

      //JSON response Pay load validations
      response.then().body("status", is("access denied"));
      response.then().body("msg", is("invalid token"));

    } catch (Exception ClientProtocolException) {
      log.info(
          "Handling HttpResponseException which is caused when rest-assured is not able to parse response");
    }
  }

  @AfterSuite
    @Parameters({"validAuthorization", "env"})
    public void deleteUsersCreated(String validAuthorization, String env) {
    if (env.equalsIgnoreCase("STAGE")) {

      String requestURL1 =
          SERVICE_END_POINT
              + CoreAPI_Constants.USERS_END_POINT
              + "/"
              + capturedNewUserID1;

      String requestURL2 =
          SERVICE_END_POINT
              + CoreAPI_Constants.USERS_END_POINT
              + "/"
              + capturedNewUserID2;

      log.info("REQUEST-URL:DELETE-" + requestURL1);

      try {
        Response response1 =
            given()
                .header("Authorization", validAuthorization)
                .delete(requestURL1)
                .then()
                .statusCode(200)
                .extract()
                .response();
      } catch (Exception e) {
        log.info("Exception when trying to parse the HTTP Response");
      }

      log.info("REQUEST-URL:DELETE-" + requestURL2);
      try {
        given()
            .header("Authorization", validAuthorization)
            .delete(requestURL2)
            .then()
            .statusCode(200)
            .extract()
            .response();
      } catch (Exception e) {
        log.info("Exception when trying to parse the HTTP Response");

      }


    }
  }



}
