package com.phunware.cme.v2.api.tests;

import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
import com.phunware.utility.FileUtils;
import com.phunware.utility.HelperMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;

import static com.phunware.cme.v2.api.tests.Schema.hashMap;
import static io.restassured.RestAssured.given;



/**
 * Created by VinayKarumuri on 7/20/18.
 */

public class Structure {

  static Logger log;
  //public String dynamicValue;
  FileUtils fileUtils = new FileUtils();
  public static String SERVICE_END_POINT = null;
  public static String JWT = null;
  public static String postStructureRequestURL = null;
  public static String postSchemaRequestURL = null;
  public static String postContainerRequestURL = null;
  public static String dateTime = null;


  public static String structureRequestBody = null;


  public static String containerId = null;

  public static final String structureTypeOBJECT = "object";
  public static final String structureTypeARRAY = "array";
  public static String applicationStructureName = "Application";
  public static String appVersionStructureName = "AppVersion";

  public static int applicationstructureId=0;

  public HashMap<String, Integer> structure = new HashMap<String, Integer>();


  @BeforeClass
  @Parameters({"env","jwt","orgId","postStructureFilePath"})
  public void setEnv(String env, String jwt, String orgId, String postStructureFilePath) throws IOException{
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
    postSchemaRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.SCHEMAS_END_POINT;
    postStructureRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.STRUCTURE_END_POINT;
    postContainerRequestURL = SERVICE_END_POINT + CmeV2_API_Constants.CONTAINERS_END_POINT;
    log.info("Hah " + postContainerRequestURL);
    dateTime = HelperMethods.getDateAsString();
    structureRequestBody = fileUtils.getJsonTextFromFile(postStructureFilePath);
  }



  @Parameters("postContainerFilePath")
  @Test(priority = 1)
  public void verify_Post_Container(String postContainerFilePath) throws IOException {

    //Request Details
    //log.info(postContainerFilePath);
    String requestBody = fileUtils.getJsonTextFromFile(postContainerFilePath);
    JSONObject requestBodyJSONObject = new JSONObject(requestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("name", "DignityAutomation"+dateTime);

    //Printing Request Details
    log.info("REQUEST-URL:POST-" + postContainerRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", JWT)
            .body(requestBodyJSONObject.toString())
            .post(postContainerRequestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    containerId = response.getBody().jsonPath().get("id");
    log.info("ContainerId = " + containerId);
  }

  /*
    DataProviders has these variables
    - ContainerId
    - name
    - type
    - field
    - schemaId
    - parentId
    - Structure Id - corresponding to each to store

  */
  @DataProvider(name = "dignityStructure")
  public Object[][] dignityStructureDataSet() {
    return new Object[][]{
        {containerId,applicationStructureName, structureTypeOBJECT,applicationStructureName, hashMap.get("VscApp"), ""},
        {containerId,appVersionStructureName, structureTypeOBJECT,appVersionStructureName + dateTime,hashMap.get("VscAppVersion"),applicationStructureName}

    };
  }




  @Test(dataProvider = "dignityStructure", priority = 2)
  public void verify_Post_Structure(String containerId, String name ,String type,String field ,String schemaId ,String parentId) throws IOException {

    //Request Details
    log.info(structureRequestBody);
    JSONObject requestBodyJSONObject = new JSONObject(structureRequestBody);
    JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");
    requestBodyData.put("containerId" , containerId );
    requestBodyData.put("name", name + dateTime);
    requestBodyData.put("type" , type );
    requestBodyData.put("field", field);
    requestBodyData.put("schemaId" , schemaId);
    log.info("parentId " + structure.get(parentId));
    requestBodyData.put("parentId", structure.get(parentId));


    //Printing Request Details
    log.info("REQUEST-URL:POST-" + postStructureRequestURL);
    log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());

    //Extracting response after status code validation
    Response response =
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", JWT)
            .body(requestBodyJSONObject.toString())
            .post(postStructureRequestURL)
            .then()
            .statusCode(200)
            .extract()
            .response();

    //printing response
    log.info("RESPONSE:" + response.asString());

    //JSON response Pay load validations
    int StructureId  = response.getBody().jsonPath().get("id");
    log.info("structureId = " + StructureId );
    structure.put(name,StructureId);
    log.info("Structure Id " + structure);

  }



}
