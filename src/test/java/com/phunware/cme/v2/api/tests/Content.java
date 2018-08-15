package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import static com.phunware.cme.v2.api.tests.Structure.structureMap;
import static com.phunware.cme.v2.api.tests.Structure.containerId;
import static io.restassured.RestAssured.given;


/**
 * Created by VinayKarumuri on 8/10/18.
 */
public class Content {

  static Logger log;
  //public String dynamicValue;
  FileUtils fileUtils = new FileUtils();
  public static String SERVICE_END_POINT = null;
  public static String JWT = null;
  public static String postContentRequestURL;
  public static String dateTime = null;


  @BeforeClass
  @Parameters({"env","jwt","orgId"})
  public void setEnv(String env, String jwt, String orgId) throws IOException {
    log = Logger.getLogger(Structure.class);
    JWT = jwt;
    if ("PROD".equalsIgnoreCase(env)) {
      SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_PROD;
    } else if ("STAGE".equalsIgnoreCase(env)) {
      SERVICE_END_POINT = CmeV2_API_Constants.SERVICE_END_POINT_STAGE;
    } else {
      log.error("Environment is not set properly. Please check your testng xml file");
      Assert.fail("Environment is not set properly. Please check your testng xml file");
    }
    postContentRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.CONTENT_END_POINT;
    log.info("Content URL" + postContentRequestURL);
    dateTime = HelperMethods.getDateAsString();
  }





  @Test(dataProvider = "usesParameter")
  public void verify_Post_Schema(String path, Integer structureId, Integer parentId) throws IOException {

    // Request Details
    String datetime = HelperMethods.getDateAsString();
    String requestBody = fileUtils.getJsonTextFromFile(path);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    requestBodyJSONObject.put("containerId",containerId);
    requestBodyJSONObject.put("structureId", structureId);
    requestBodyJSONObject.put("parentId", parentId);


    // logging Request Details
    log.info("REQUEST-URL:POST-" + postContentRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

    // Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", JWT)
            .body(requestBodyJSONObject.toString())
            .post(postContentRequestURL)
            .then()
            .extract()
            .response();

    // printing response
    log.info("RESPONSE:" + response.asString());

    response.then().statusCode(200);
  }

  @DataProvider(name = "usesParameter")
  public Object[][] provideTestParam(ITestContext context) {
    return new Object[][] {
        {context.getCurrentXmlTest().getParameter("postApplicationContentFilePath"), structureMap.get("Application"), null},
    };
  }




}
