    package com.phunware.cme.v2.api.tests;

    import com.phunware.cmev2_api.constants.CmeV2_API_Constants;
    import com.phunware.utility.FileUtils;
    import com.phunware.utility.HelperMethods;
    import io.restassured.path.json.JsonPath;
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
    import static org.hamcrest.Matchers.is;

    /**
     * Created by VinayKarumuri on 7/13/18.
     */
    public class Schema {

      static Logger log;
      //public String dynamicValue;
      public static String vscAppId;
      public static String SERVICE_END_POINT = null;
      public static String JWT = null;
      public static String ORGID = null;
      public static String postSchemaRequestURL = null;
      public static String VSCAppSchemaId=null;
      public static String VSCAppVersionSchemaId=null;

        FileUtils fileUtils = new FileUtils();
        HashMap<String,Integer> hashMap= new HashMap<String,Integer>();


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


      @Test(dataProvider = "usesParameter")
      public void verify_Post_VscPlatform(String param) throws IOException {

        //Request Details
          String datetime = HelperMethods.getDateAsString();
          String requestBody = fileUtils.getJsonTextFromFile(param);
          JSONObject requestBodyJSONObject = new JSONObject(requestBody);
          JSONObject requestBodyData = (JSONObject) requestBodyJSONObject.get("data");

          requestBodyData.put("name", "VSC-AppVersion"+datetime);
          requestBodyData.put("orgId", ORGID);


          //logging Request Details
          log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
          log.info("REQUEST-URL:BODY-" + requestBodyJSONObject.toString());


        //logging Request Details
        log.info("REQUEST-URL:POST-" + postSchemaRequestURL);
        log.info("REQUEST-URL:BODY-" + requestBodyData);

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

        //Get schema ID and put it into Hashmap
          hashMap.put(param,response.getBody().jsonPath().get("id"));
        log.info("Schema: " + param + " Schema_ID: " + response.getBody().jsonPath().get("id"));
      }




        @DataProvider(name = "usesParameter")
        public Object[][] provideTestParam(ITestContext context) {
             String postVscAppSchema = context.getCurrentXmlTest().getParameter("postVscAppSchema");
             String postVscAppVersionSchema= context.getCurrentXmlTest().getParameter("postVscAppVersionSchema");
             String postVscPlatformSchema = context.getCurrentXmlTest().getParameter("postVscPlatformSchema");
            return new Object[][] {{postVscAppSchema },{postVscAppVersionSchema},{postVscPlatformSchema}};
        }


        @AfterClass
        public void tearDown() {

            System.out.println("HashMap: " + hashMap);

        }
    }







