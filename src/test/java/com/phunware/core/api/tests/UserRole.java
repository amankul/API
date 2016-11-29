package com.phunware.core.api.tests;

import com.phunware.core_api.constants.CoreAPI_Constants;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

/** Created by pkovurru on 11/18/16. */
public class UserRole {

  static Logger log;
  public String dynamicValue;
  public static String capturedNewUserRoleID;

  @BeforeClass
  public void preTestSteps() {
    log = Logger.getLogger(Client.class);
    dynamicValue = "Test" + Math.random();
  }

  @Test(priority = 1)
  public void verify_Get_User_Role() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"org_id\":128}")
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

    //validating that all org id's returned by this endpoint does not have the value other than 128
    for (int i = 0;
        i <= response.then().extract().jsonPath().getList("data.org_id").size() - 1;
        i++) {
      response.then().body("data.org_id[" + i + "]", is(128));
    }
  }

  @Test(priority = 2)
  public void verify_Get_UserRole_NonExistant_OrgID() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"org_id\":\"abc\"}")
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

  @Test(priority = 3)
  public void verify_Get_UserRole_Invalid_parameter_Structure() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"org_id\":abc}")
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

  @Test(priority = 4)
  public void verify_Get_USerRole_InvalidAuth() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTH_INVALID)
            .queryParam("{\"org_id\":\"128\"}")
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

  @Test(priority = 5)
  public void verify_Get_UserRole_Pagination() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"offset\":\"0\",\"limit\":\"15\",\"org_id\":\"1\"}")
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
    for (int i = 0;
        i <= response.then().extract().jsonPath().getList("data.org_id").size() - 1;
        i++) {
      response.then().body("data.org_id[" + i + "]", is(1));
    }

    //result count validation.
    Assert.assertTrue(response.then().extract().jsonPath().getList("data").size() <= 15);
  }

  @Test(priority = 6)
  public void verify_Get_UserRole_Pagination_NonExistant_OrgID() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"offset\":\"0\",\"limit\":\"15\",\"org_id\":\"!2#\"}")
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

  @Test(priority = 7)
  public void verify_Get_UserRole_Pagination_InvalidAuth() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTH_INVALID)
            .queryParam("{\"offset\":\"0\",\"limit\":\"15\",\"org_id\":\"1\"}")
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

  @Test(priority = 8)
  public void verify_Post_Create_New_UserRole() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;
    String requestBody = "{\"data\":{\"name\": \"" + dynamicValue + "\",\"org_id\": 132}}";

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .request()
            .body(requestBody)
            .post(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //capturing created client ID
    capturedNewUserRoleID = response.then().extract().path("data.id").toString();
    log.info("Captured new Organization ID:" + capturedNewUserRoleID);

    //JSON response Pay load validations
    response.then().body("data.id", is(notNullValue()));
    response.then().body("data.org_id", is(132));
    response.then().body("data.name", is(dynamicValue));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  //TODO CROSS CHECK WITH CALEB
  // @Test(priority=9)
  public void verify_Post_Create_New_UserRole_EmptyName() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;
    String requestBody = "{\"data\":{\"name\": \"\",\"org_id\": 132}}";

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .request()
            .body(requestBody)
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

  @Test(priority = 10)
  public void verify_Post_Create_New_UserRole_InvalidAuth() {

    //Request Details
    String requestURL = CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT;
    String requestBody = "{\"data\":{\"name\": \"" + dynamicValue + "\",\"org_id\": 132}}";

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTH_INVALID)
            .request()
            .body(requestBody)
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

  @Test(priority = 11)
  public void verify_Put_Update_Organization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USER_ROLE_END_POINT
            + "/"
            + capturedNewUserRoleID;
    String requestBody =
        "{\"data\":{\"name\": \"" + dynamicValue + "updated" + "\",\"org_id\": 132}}";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .request()
            .body(requestBody)
            .put(requestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("data.id", is(Integer.parseInt(capturedNewUserRoleID)));
    response.then().body("data.name", is(dynamicValue + "updated"));
    response.then().body("data.org_id", is(132));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  //TODO NEED TO CHECK WHY THIS IS RETURNING A 200 instead of 400
  @Test(priority = 12)
  public void verify_Put_Update_Organization_EmptyName_InRequestBody() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USER_ROLE_END_POINT
            + "/"
            + capturedNewUserRoleID;
    String requestBody = "{\"data\":{\"name\":\"\",\"org_id\": 132}}";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .request()
            .body(requestBody)
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

  @Test(priority = 13)
  public void verify_Put_Update_Organization_InvalidOrganization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.USER_ROLE_END_POINT + "/" + "!@a3";
    String requestBody =
        "{\"data\":{\"name\": \"" + dynamicValue + "updated" + "\",\"org_id\": 132}}";

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-BODY:-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .request()
            .body(requestBody)
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

  @Test(priority = 14)
  public void verify_Delete_ExistingUserRole() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.USER_ROLE_END_POINT
            + "/"
            + capturedNewUserRoleID;

    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    try {
      Response response =
          given()
              .header("Content-Type", "application/json")
              .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
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
