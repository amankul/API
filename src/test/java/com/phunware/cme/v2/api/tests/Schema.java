package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.path.json.JsonPath;
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

/**
 * Created by VinayKarumuri on 7/13/18.
 */
public class Schema {

  static Logger log;
  //public String dynamicValue;
  public static String vscAppId;
  FileUtils fileUtils = new FileUtils();
  public static String SERVICE_END_POINT = null;
  public static String JWT = null;
  public static String ORGID = null;
  public static String postSchemaRequestURL = null;
  public static String VSCAppSchemaId=null;
  public static String VSCAppVersionSchemaId=null;

  @BeforeSuite
  @Parameters({"env","jwt","orgId"})
  public void setEnv(String env, String jwt, String orgId) {
    JWT = jwt;
    ORGID = orgId;
    if ("PROD".equalsIgnoreCase(env)) {
      SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
    } else if ("STAGE".equalsIgnoreCase(env)) {
      SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
      Assert.fail("Environment is not set properly. Please check your testng xml file");
    }
    postSchemaRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.SCHEMAS_END_POINT;
  }

  @BeforeClass
  public void preTestSteps() {

    log = Logger.getLogger(Schema.class);
  }



  @Parameters("postVscAppSchema")
  @Test(priority = 1)
  public void verify_Post_VscAppSchema(String postVscAppSchema ) throws IOException {

    //Request Details
    log.info(postVscAppSchema);
    String requestBody = fileUtils.getJsonTextFromFile(postVscAppSchema);
    String datetime = HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", "VSC-App"+datetime);
    requestBodyData.put("orgId", ORGID);


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", JWT)
            .body(requestBodyJSONObject.toString())
            .post(postSchemaRequestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    VSCAppSchemaId = response.getBody().jsonPath().get("id");
    log.info("VscAppSchemaId = " + VSCAppSchemaId);
  }

  @Parameters("postVscAppVersionSchema")
  @Test(priority = 2)
  public void verify_Post_VscAppVersionSchema(String postVscAppVersionSchema ) throws IOException {

    //Request Details
    log.info(postVscAppVersionSchema);
    String requestBody = fileUtils.getJsonTextFromFile(postVscAppVersionSchema);
    String datetime = HelperMethods.getDateAsString();
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    requestBodyData.put("name", "VSC-AppVersion"+datetime);
    requestBodyData.put("orgId", ORGID);


    //logging Request Details
    log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", JWT)
            .body(requestBodyJSONObject.toString())
            .post(postSchemaRequestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    VSCAppVersionSchemaId = response.getBody().jsonPath().get("id");
    log.info("VSCAppVersionSchemaId = " + VSCAppVersionSchemaId);
  }



  @Parameters("postVscPlatformSchema")
  @Test(priority = 3)
  public void verify_Post_VscPlatform(String postVscPlatformSchema ) throws IOException {


    //Request Details
    log.info(postVscPlatformSchema);
    String datetime = HelperMethods.getDateAsString();

      JSONObject requestBodyData=HelperMethods.generateRequestBody(postVscPlatformSchema);

    requestBodyData.put("name", "VSC-Platform"+datetime);
    requestBodyData.put("orgId", ORGID);


    //logging Request Details
    log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyData);

    //Extracting response after status code validation
    Response response =
            given()
                    .header("Content-Type", "application/json")
                    .header("Authorization", JWT)
                    .body(requestBodyData.toString())
                    .post(postSchemaRequestURL)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    String VSCPlatformSchemaId = response.getBody().jsonPath().get("id");
    log.info("VSCPlatformSchemaId = " +  VSCPlatformSchemaId);
  }

}




