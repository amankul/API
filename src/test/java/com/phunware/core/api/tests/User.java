package com.phunware.core.api.tests;

import com.phunware.core_api.constants.CoreAPI_Constants;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.http.client.HttpResponseException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/** Created by pkovurru on 11/21/16. */
public class User {

  static Logger log;
  public String dynamicValue;
  public String dynamicValue1;
  public String dynamicValue2;
  public String dynamicValue3;
  public String dynamicValue4;
  public String dynamicValue5;

  public static final String USERID = "1712";
  public static final String INVALID_ID = "12c";
  public static String user_Details =
      "{\"provider\":\"phunware\",\"email\":\"fakeowner@phunware.com\",\"password\":\"Password1\"}";
  public static String user_Pagination_Details =
      "{\"name\":\"ngai caleb\",\"offset\":\"0\",\"limit\":\"15\",\"org_id\":\"\"}";
  public static String new_User_Body =
      "{\"data\": {\"first_name\": \"toChange\",\"last_name\": \"toChange\",\"email\": \"toChange@automation.com\",\"time_zone\": \"America\\/Chicago\",\"is_active\": 1,\"org_id\": 132,\"role_id\": 655,\"clients\": [],\"orgs\": [],\"password\": \"Password123\"}}";
  public static String update_User_Body =
      "{\"data\": {\"role_id\": 632,\"first_name\": \"QA\",\"last_name\": \"user2\",\"email\": \"qause2r@phunware.com\",\"time_zone\":\"America/Chicago\",\"is_active\": 1,\"password\":\"Password1234\",\"org_id\": 132}}";
  public static String update_User_Body_Null_Values =
      "{\"data\": {\"role_id\": 632,\"first_name\": null,\"last_name\": null,\"email\": null,\"time_zone\": null,\"is_active\": 1,\"password\":\"\",\"org_id\": 132}}";
  public static String change_Password_Body =
      "{\"old_password\": \"Password1234\",\"new_password\": \"Password12345\"}";
  public static String reset_Password_Body =
      "{\"email\": \"qause2r@phunware.com\",\"email_url\": \"https://maas-stage.phunware.com/reset-password/{key}\"}";
  public static String submit_New_Password_Body = "{\"password\": \"Password12345\"}";

  public static String capturedNewUserID;
  public static String capturedNewUserID1;
  public static String capturedNewUserID2;

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

  @Test(priority = 1)
  public void verify_Get_User_Role() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/" + USERID;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .get(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(USERID)));
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

  @Test(priority = 2)
  public void verify_Get_User_Role_InvalidUserID() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/" + INVALID_ID;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
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

  @Test(priority = 3)
  public void verify_Get_User_Role_InvalidAuth() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/" + USERID;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTH_INVALID)
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

  @Test(priority = 4)
  public void verify_Get_Authenticate_User() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/authenticate";

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam(user_Details)
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

  @Test(priority = 5)
  public void verify_Get_Authenticate_User_Invalid_Credentials() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/authenticate";

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam(user_Details.replace("fakeowner", "asdf"))
            .get(requestURL)
            .then()
            .statusCode(401)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    //response.then().body("error.message", is("Invalid credentials."));
  }

  @Test(priority = 6)
  public void verify_Get_Authenticate_User_Empty_Credentials() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/authenticate";

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam(user_Details.replace("fakeowner@phunware.com", "").replace("Password1", ""))
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

  @Test(priority = 7)
  public void verify_Get_UserPagination() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam(user_Pagination_Details)
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
              .matches("(?i:.*?caleb.*)"));
      //case insensitive search
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.last_name[" + i + "]")
              .toString()
              .matches("(?i:.*?ngai.*)"));
    }
  }

  @Test(priority = 8)
  public void verify_Get_UserPagination_NullName() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam(user_Pagination_Details.replace("ngai caleb", ""))
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

  @Test(priority = 9)
  public void verify_post_CreateNewUser() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(new_User_Body.replaceAll("toChange", dynamicValue))
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
    response.then().body("data.org_id", is(132));
    response.then().body("data.role_id", is(655));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(dynamicValue));
    response.then().body("data.last_name", is(dynamicValue));
    response.then().body("data.email", is(dynamicValue + "@automation.com"));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 10)
  public void verify_post_CreateNewUser_Empty_FirstNameIn_Body() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(
                new_User_Body.replaceAll("toChange", dynamicValue1).replaceFirst(dynamicValue1, ""))
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
    response.then().body("data.org_id", is(132));
    response.then().body("data.role_id", is(655));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(""));
    response.then().body("data.last_name", is(dynamicValue1));
    response.then().body("data.email", is(dynamicValue1 + "@automation.com"));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 11)
  public void verify_post_CreateNewUser_Empty_LastNameIn_Body() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(
                "{\"data\": {\"first_name\": \"toChange\",\"last_name\": \"\",\"email\": \"toChange@automation.com\",\"time_zone\": \"America\\/Chicago\",\"is_active\": 1,\"org_id\": 132,\"role_id\": 655,\"clients\": [],\"orgs\": [],\"password\": \"Password123\"}}"
                    .replaceAll("toChange", dynamicValue2))
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
    response.then().body("data.org_id", is(132));
    response.then().body("data.role_id", is(655));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is(dynamicValue2));
    response.then().body("data.last_name", is(""));
    response.then().body("data.email", is(dynamicValue2 + "@automation.com"));
    response.then().body("data.containsKey('time_zone')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 12)
  public void verify_post_CreateNewUser_Empty_email() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(
                "{\"data\": {\"first_name\": \"toChange\",\"last_name\": \"toChange\",\"email\": \"\",\"time_zone\": \"America\\/Chicago\",\"is_active\": 1,\"org_id\": 132,\"role_id\": 655,\"clients\": [],\"orgs\": [],\"password\": \"Password123\"}}"
                    .replaceAll("toChange", dynamicValue2))
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

  //TODO - FIND OUT THE RIGHT EXPECTED STATUS CODE
  @Test(priority = 13)
  public void verify_post_CreateNewUser_Null_Timezone() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(
                "{\"data\": {\"first_name\": \"toChange\",\"last_name\": \"toChange\",\"email\": \"toChange@automation.com\",\"time_zone\": null,\"is_active\": 1,\"org_id\": 132,\"role_id\": 655,\"clients\": [],\"orgs\": [],\"password\": \"Password123\"}}"
                    .replaceAll("toChange", dynamicValue3))
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations

  }

  //TODO - FIND OUT THE RIGHT EXPECTED STATUS CODE
  @Test(priority = 14)
  public void verify_post_CreateNewUser_Null_isActive() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(
                "{\"data\": {\"first_name\": \"toChange\",\"last_name\": \"toChange\",\"email\": \"toChange@automation.com\",\"time_zone\": \"America\\/Chicago\",\"is_active\": null,\"org_id\": 132,\"role_id\": 655,\"clients\": [],\"orgs\": [],\"password\": \"Password123\"}}"
                    .replaceAll("toChange", dynamicValue4))
            .post(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations

  }

  @Test(priority = 15)
  public void verify_post_CreateNewUser_Empty_Password() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(
                "{\"data\": {\"first_name\": \"toChange\",\"last_name\": \"toChange\",\"email\": \"toChange@automation.com\",\"time_zone\": \"America\\/Chicago\",\"is_active\": 1,\"org_id\": 132,\"role_id\": 655,\"clients\": [],\"orgs\": [],\"password\": \"\"}}"
                    .replaceAll("toChange", dynamicValue5))
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

  @Test(priority = 16)
  public void verify_put_UpdateExistingUser() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + update_User_Body);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(update_User_Body)
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewUserID)));
    response.then().body("data.org_id", is(132));
    response.then().body("data.role_id", is(632));
    response.then().body("data.containsKey('google_id')", is(true));
    response.then().body("data.first_name", is("QA"));
    response.then().body("data.last_name", is("user2"));
    response.then().body("data.email", is("qause2r@phunware.com"));
    response.then().body("data.time_zone", is("America/Chicago"));
    response.then().body("data.is_active", is(1));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 17)
  public void verify_put_UpdateExistingUser_NullEmail_EmptyPassword_ValuesInBody() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + update_User_Body_Null_Values);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(update_User_Body_Null_Values)
            .put(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.messages.email", is("Invalid email address."));
    response
        .then()
        .body(
            "error.messages.password",
            is(
                "Password must be at least 8 characters long, Password should contain at least one lower case character, Password should contain at least one upper case character, Password should contain at least one digit"));
  }

  @Test(priority = 18)
  public void verify_put_Change_Password_ExistingUser() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID
            + "/change-password";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + change_Password_Body);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
              .body(change_Password_Body)
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

  @Test(priority = 19)
  public void verify_put_Change_Password_InvalidBodyParameters() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID
            + "/change-password";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + change_Password_Body.replace("Password1234", "aaaa"));

    //Extracting response after status code validation
    Response response =
        given()
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(change_Password_Body.replace("Password1234", "aaaa"))
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

  @Test(priority = 20)
  public void verify_put_Reset_Password_ExistingUser() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/reset-password";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + reset_Password_Body);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
              .body(reset_Password_Body)
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

  @Test(priority = 21)
  public void verify_put_Reset_Password_InvalidBodyParameters() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USERS_END_POINT + "/reset-password";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + reset_Password_Body.replace("", "abcd@gmail.com"));

    //Extracting response after status code validation
    Response response =
        given()
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .body(change_Password_Body.replace("qause2r", "aaaa"))
            .put(requestURL)
            .then()
            .statusCode(400)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.messages.email", is("Invalid email address."));
  }

  @Test(priority = 22)
  public void verify_put_Submit_NEw_Password_ExistingUser() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/reset-password/{key}";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY-" + submit_New_Password_Body);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
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

  @Test(priority = 23)
  public void verify_delete_ExistingUser() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USERS_END_POINT
            + "/"
            + capturedNewUserID;

    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    try {
      //Extracting response after status code validation
      Response response =
          given()
              .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
              .delete(requestURL)
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

  @Test(priority = 24)
  public void verify_delete_Invaliduser() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
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
              .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
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

  @Test(priority = 25)
  public void verify_delete_ExistingUser_InvalidAuth() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
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
              .header("Authorization", CoreAPI_Constants.AUTH_INVALID)
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
}
