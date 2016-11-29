package com.phunware.core.api.tests;

import com.phunware.core_api.constants.CoreAPI_Constants;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/** Created by pkovurru on 11/17/16. */
public class Organization {

  static Logger log;
  public String dynamicValue;
  public static final String ORGANIZATION_ID = "132";
  public static String capturedNewORGANIZATION_ID;

  @BeforeClass
  public void preTestSteps() {
    log = Logger.getLogger(Client.class);
    dynamicValue = "Test" + Math.random();
  }

  @Test(priority = 1)
  public void verify_Get_Organization_Details() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + ORGANIZATION_ID;

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
    response.then().body("data.id", is(132));
    response.then().body("data.containsKey('name')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 2)
  public void verify_Get_Organization_Details_InvalidOrgID() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATION_END_POINT + "000";

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
    response.then().body("error.message", is("The specified organization does not exist."));
  }

  @Test(priority = 3)
  public void verify_Get_Organization_Details_InvalidAuthorization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + ORGANIZATION_ID;

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
  public void verify_Get_Collection_Of_Organizations() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"name\":\"QA\"}")
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

    //validating that all org names returned by this endpoint has the case insensitive string "qa"
    for (int i = 0; i <= response.then().extract().jsonPath().getList("data").size() - 1; i++) {
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.name[" + i + "]")
              .toString()
              .matches(".*qa.*|.*QA.*"));
    }
  }

  @Test(priority = 5)
  public void verify_Get_Collection_Of_Organizations_NameWithEmptyString() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"name\":\"\"}")
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

  @Test(priority = 6)
  public void verify_Get_Collection_Of_Organizations_Pagination() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"name\":\"qa\",\"offset\":\"1\",\"limit\":\"15\",\"org_id\":\"\"}")
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

    //validating that all org names returned by this endpoint has the case insensitive string "qa"
    for (int i = 0; i <= response.then().extract().jsonPath().getList("data").size() - 1; i++) {
      Assert.assertTrue(
          response
              .then()
              .extract()
              .path("data.name[" + i + "]")
              .toString()
              .matches(".*qa.*|.*QA.*"));
    }
  }

  @Test(priority = 7)
  public void verify_Get_Collection_Of_Organizations_Pagination_EmptyName() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", CoreAPI_Constants.AUTHORIZATION)
            .queryParam("{\"name\":\"\",\"offset\":\"0\",\"limit\":\"15\",\"org_id\":\"\"}")
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

  @Test(priority = 8)
  public void verify_Post_Create_New_Organization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;
    String requestBody = "{\"data\":{\"name\": \"" + dynamicValue + "\"}}";

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
    capturedNewORGANIZATION_ID = response.then().extract().path("data.id").toString();
    log.info("Captured new Organization ID:" + capturedNewORGANIZATION_ID);

    //JSON response Pay load validations
    response.then().body("data.id", is(notNullValue()));
    response.then().body("data.name", is(dynamicValue));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 9)
  public void verify_Post_Create_New_Organization_EmptyName_InRequestBody() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATIONS_COLLECTION_END_POINT;
    String requestBody = "{\"data\":{\"name\": \"\"}}";

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
  public void verify_Put_Update_Organization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;
    String requestBody =
        "{\"data\":{\"name\": \""
            + dynamicValue
            + "updated"
            + "\",\"services\": [\"analytics\",\"alerts\",\"content\",\"messaging\"]}}";

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
    response.then().body("data.id", is(Integer.parseInt(capturedNewORGANIZATION_ID)));
    response.then().body("data.name", is(dynamicValue + "updated"));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 11)
  public void verify_Put_Update_Organization_EmptyName_InRequestBody() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;
    String requestBody =
        "{\"data\":{\"name\": \"\",\"services\": [\"analytics\",\"alerts\",\"content\",\"messaging\"]}}";

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

  @Test(priority = 12)
  public void verify_Put_Update_Organization_EmptyServices_InRequestBody() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;
    String requestBody =
        "{\"data\":{\"name\": \"" + dynamicValue + "updated" + "\",\"services\":[\"\"]}}";

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
    response.then().body("data.id", is(Integer.parseInt(capturedNewORGANIZATION_ID)));
    response.then().body("data.name", is(dynamicValue + "updated"));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Test(priority = 13)
  public void verify_Put_Update_Organization_InvalidOrganization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT + CoreAPI_Constants.ORGANIZATION_END_POINT + "!@a3";
    String requestBody =
        "{\"data\":{\"name\": \""
            + dynamicValue
            + "update"
            + "\",\"services\": [\"analytics\",\"alerts\",\"content\",\"messaging\"]}}";

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
    response.then().body("error.message", is("Organization not found."));
  }

  @Test(priority = 14)
  public void verify_Delete_ExistingOrganization() {

    //Request Details
    String requestURL =
        CoreAPI_Constants.SERVICE_END_POINT
            + CoreAPI_Constants.ORGANIZATION_END_POINT
            + capturedNewORGANIZATION_ID;

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
