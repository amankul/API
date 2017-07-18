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

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by pkovurru on 11/14/16.
 */
public class Client {

  static Logger log;
  //public String dynamicValue;
  public static String capturedNewclientId;
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
    //dynamicValue = "Test" + Math.random();

  }


  @Parameters({"clientId", "validAuthorization"})
  @Test(priority = 1)
  public void verify_Get_ClientDetails(String clientId, String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT + clientId;

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
    response.then().body("data.id", is(Integer.parseInt(clientId)));
    response.then().body("data.containsKey('org_id')", is(true));
    response.then().body("data.containsKey('category_id')", is(true));
    response.then().body("data.containsKey('name')", is(true));
    response.then().body("data.containsKey('type')", is(true));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }

  @Parameters("validAuthorization")
  @Test(priority = 2)
  public void verify_Get_ClientDetails_InvalidClientId(String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT + "000";

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
    response.then().body("error.message", is("The specified client does not exist."));
  }


  @Parameters({"clientId", "invalidAuthorization"})
  @Test(priority = 3)
  public void verify_Get_ClientDetails_InvalidAuth(String clientId, String invalidAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT + clientId;

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

  @Parameters("validAuthorization")
  @Test(priority = 4)
  public void verify_Get_ClientTypes(String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_TYPES_END_POINT;

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
    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('key') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));
  }

  @Parameters("invalidAuthorization")
  @Test(priority = 5)
  public void verify_Get_ClientTypes_InvalidAuth(String invalidAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_TYPES_END_POINT;

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
  @Test(priority = 6)
  public void verify_Get_Client_Categories(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_CATEGORIES_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Printing Query Parameters
    log.info("Query-Params-" + queryParameters);

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
    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));
  }

  @Parameters({"invalidAuthorization", "queryParameters"})
  @Test(priority = 7)
  public void verify_Get_Client_Categories_InvalidAuth(String invalidAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_CATEGORIES_END_POINT;

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
  @Test(priority = 8)
  public void verify_Get_Client_Pagination(String validAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_PAGINATION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Printing Query Parameters
    log.info("Query-Params-" + queryParameters);

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
    response.then().body("containsKey('totalCount')", is(true));
    response.then().body("containsKey('resultCount')", is(true));

    response.then().body("data.flatten().any {it.containsKey('id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('org_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('category_id') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('name') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('type') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('is_active') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('created_at') }", is(true));
    response.then().body("data.flatten().any {it.containsKey('updated_at') }", is(true));
  }


  @Parameters({"invalidAuthorization", "queryParameters"})
  @Test(priority = 9)
  public void verify_Get_Client_Pagination_InvalidAuth(String invalidAuthorization, String queryParameters) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_PAGINATION_END_POINT;

    //Printing Request Details
    log.info("REQUEST-URL:GET-" + requestURL);

    //Printing Query Parameters
    log.info("Query-Params-" + queryParameters);

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


  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 10)
  public void verify_Post_New_Client(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

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

    //capturing created client ID
    capturedNewclientId = response.then().extract().path("data.id").toString();
    log.info("Captured new Client ID:" + capturedNewclientId);

    //JSON response Pay load validations
    response.then().body("data.id", is(notNullValue()));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.name", is(newClientName));
    response.then().body("data.type", is(clientType));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }


  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 11)
  public void verify_Post_New_Client_emptyOrgID(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT_1;
    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

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
    response.then().body("error.messages.org_id", is("No organization specified."));
  }

  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 12)
  public void verify_Post_New_Client_emptyCategoryID(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

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
    response.then().body("error.messages.category_id", is("No category specified."));
  }

  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 13)
  public void verify_Post_New_Client_emptyType(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT_1;

    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

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
    response.then().body("error.messages.type", is("No type specified."));
  }

  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 14)
  public void verify_Post_New_Client_emptyName(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL = SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT_1;
    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "";
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

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
    response.then().body("error.messages.name", is("The name must be at least 1 character long."));
  }


  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 15)
  public void verify_Put_Update_Existing_Client(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.CLIENT_END_POINT
            + capturedNewclientId;

    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

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
    response.then().body("data.id", is(Integer.parseInt(capturedNewclientId)));
    response.then().body("data.org_id", is(orgId));
    response.then().body("data.category_id", is(categoryId));
    response.then().body("data.name", is(newClientName));
    response.then().body("data.type", is(clientType));
    response.then().body("data.containsKey('is_active')", is(true));
    response.then().body("data.containsKey('created_at')", is(true));
    response.then().body("data.containsKey('updated_at')", is(true));
  }


  @Parameters({"invalidAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 16)
  public void verify_Put_Update_Existing_Client_InvalidAuth(String invalidAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.CLIENT_END_POINT
            + capturedNewclientId;
    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);

    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", invalidAuthorization)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
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


  @Parameters({"validAuthorization", "clientRequestBodyFilePath", "orgId", "clientType", "categoryId"})
  @Test(priority = 17)
  public void verify_Put_Update_Invalid_Client(String validAuthorization, String clientRequestBodyFilePath, String orgId, String clientType, String categoryId) throws IOException {

    //Request Details
    String requestURL =
        SERVICE_END_POINT + CoreAPI_Constants.CLIENT_END_POINT + "000";

    File file = new File(clientRequestBodyFilePath);
    String requestBody = fileUtils.getJsonTextFromFile(clientRequestBodyFilePath);
    String newClientName = "AppName" + HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", newClientName);
    requestBodyData.put("org_id", orgId);
    requestBodyData.put("category_id", categoryId);
    requestBodyData.put("type", clientType);


    //Printing Request Details
    log.info("REQUEST-URL:PUT-" + requestURL);
    log.info("REQUEST-URL:BODY-" + requestBody);

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", validAuthorization)
            .body(requestBodyJSONObject.toString())
            .put(requestURL)
            .then()
            .statusCode(404)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    response.then().body("error.message", is("Client not found."));
  }


  @Parameters("validAuthorization")
  @Test(priority = 18)
  public void verify_Delete_Client(String validAuthorization) {

    //Request Details
    String requestURL =
        SERVICE_END_POINT
            + CoreAPI_Constants.CLIENT_END_POINT
            + capturedNewclientId;

    //Printing Request Details
    log.info("REQUEST-URL:DELETE-" + requestURL);

    //Extracting response after status code validation
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
      log.info("SKipping HttpResponseException");
    }
  }
}
