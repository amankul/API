package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

/** Created by VinayKarumuri on 7/13/18. */
public class Schema {

  // public String dynamicValue;
  public static String vscAppId;
  public static String SERVICE_END_POINT = null;
  public static String JWT = null;
  public static String ORGID = null;
  public static String postSchemaRequestURL = null;
  public static String VSCAppSchemaId = null;
  public static String VSCAppVersionSchemaId = null;
  static Logger log;
  FileUtils fileUtils = new FileUtils();
  public static HashMap<String, String> schemaMap = new HashMap<String, String>();

  @BeforeSuite
  @Parameters({"env", "jwt", "orgId"})
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


    /** Method to post schema.  Source file from resources directory **/
  @Test(dataProvider = "usesParameter")
  public void verify_Post_Schema(String path) throws IOException {

    // Request Details
    String datetime = HelperMethods.getDateAsString();
    String requestBody = fileUtils.getJsonTextFromFile(path);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

    int index = path.indexOf("Vsc");
    String name = path.substring(index).replaceAll(".json", "");

    requestBodyData.put("name", name + datetime);
    requestBodyData.put("orgId", ORGID);

    // logging Request Details
    log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", JWT)
            .body(requestBodyJSONObject.toString())
            .post(postSchemaRequestURL)
            .then()
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    response.then().statusCode(200);
    // Get schema ID and put it into Hashmap
    schemaMap.put(name, response.getBody().jsonPath().get("id"));
  }

    /** Data provider for schema: Dignity Health **/
  @DataProvider(name = "usesParameter")
  public Object[][] provideTestParam(ITestContext context) {


    return new Object[][] {
      {context.getCurrentXmlTest().getParameter("postSchemaVscAdvertisingSetting")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscApp")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscAppVersion")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscBeacon")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscBuilding")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscDatabaseVersion")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscFloor")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscGeoSettings")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscMapSettings")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscPlatform")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscPreCachingConfiguration")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscProximityAlert")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscSettings")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscVenue")},
      {context.getCurrentXmlTest().getParameter("postSchemaVscCampus")}
    };
  }

  @AfterClass
  public void tearDown() {

    System.out.println("Schema Map: " + schemaMap);
  }
}
