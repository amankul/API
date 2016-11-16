package com.phunware.core.api.tests;

import com.phunware.core_api.constants.CoreAPI_Constants;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by pkovurru on 11/14/16.
 */
public class Client {

    static Logger log;
    public String dynamicValue;
    public static String clientID="468";
    public static String capturedNewClientID;

    @BeforeClass
    public void preTestSteps(){
        log = Logger.getLogger(Client.class);
        dynamicValue= "Test"+Math.random();
    }


    @Test(priority=1)
    public void verify_Get_ClientDetails(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT+clientID;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                            .header("Authorization",CoreAPI_Constants.AUTHORIZATION)
                            .get(requestURL).then().statusCode(200).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("data.id",is(468));
        response.then().body("data.containsKey('org_id')",is(true));
        response.then().body("data.containsKey('category_id')",is(true));
        response.then().body("data.containsKey('name')",is(true));
        response.then().body("data.containsKey('type')",is(true));
        response.then().body("data.containsKey('is_active')",is(true));
        response.then().body("data.containsKey('created_at')",is(true));
        response.then().body("data.containsKey('updated_at')",is(true));

    }


    @Test(priority=2)
    public void verify_Get_ClientDetails_InvalidClientID(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT+"000";

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION)
                .get(requestURL).then().statusCode(404).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("error.message",is("The specified client does not exist."));


    }


    @Test(priority=3)
    public void verify_Get_ClientDetails_InvalidAuth(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT+clientID;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTH_INVALID)
                .get(requestURL).then().statusCode(401).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("status",is("access denied"));
        response.then().body("msg",is("invalid token"));


    }

    @Test(priority=4)
    public void verify_Get_ClientTypes(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_TYPES_END_POINT;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION)
                .get(requestURL).then().statusCode(200).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("data.flatten().any {it.containsKey('id') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('name') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('key') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('created_at') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('updated_at') }",is(true));
    }

    @Test(priority=5)
    public void verify_Get_ClientTypes_InvalidAuth(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_TYPES_END_POINT;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTH_INVALID)
                .get(requestURL).then().statusCode(401).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("status",is("access denied"));
        response.then().body("msg",is("invalid token"));
    }

    @Test(priority=6)
    public void verify_Get_Client_Categories(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_CATEGORIES_END_POINT;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION)
                .queryParam("{\"type_id\":1}")
                .get(requestURL).then().statusCode(200).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("data.flatten().any {it.containsKey('id') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('name') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('created_at') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('updated_at') }",is(true));
    }

    @Test(priority=7)
    public void verify_Get_Client_Categories_InvalidAuth(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_CATEGORIES_END_POINT;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTH_INVALID)
                .queryParam("{\"type_id\":1}")
                .get(requestURL).then().statusCode(401).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("status",is("access denied"));
        response.then().body("msg",is("invalid token"));
    }


    @Test(priority=8)
    public void verify_Get_Client_Pagination(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_PAGINATION_END_POINT;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION)
                .queryParam("{\"offset\":\"15\",\"limit\":\"15\",\"org_id\":\"1\"}")
                .get(requestURL).then().statusCode(200).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("offset",is("15"));
        response.then().body("containsKey('totalCount')",is(true));
        response.then().body("containsKey('resultCount')",is(true));

        response.then().body("data.flatten().any {it.containsKey('id') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('org_id') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('category_id') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('name') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('type') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('is_active') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('created_at') }",is(true));
        response.then().body("data.flatten().any {it.containsKey('updated_at') }",is(true));

    }

    @Test(priority=9)
    public void verify_Get_Client_Pagination_InvalidAuth(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_PAGINATION_END_POINT;

        //Printing Request Details
        log.info("REQUEST-URL:GET-"+requestURL);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTH_INVALID)
                .queryParam("{\"offset\":\"15\",\"limit\":\"15\",\"org_id\":\"1\"}")
                .get(requestURL).then().statusCode(401).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("status",is("access denied"));
        response.then().body("msg",is("invalid token"));

    }


    @Test(priority=10)
    public void verify_Post_New_Client(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT_1;
        String requestBody= "{\"data\":{\"name\": \""+dynamicValue+"\",\"org_id\": 132,\"category_id\": 2,\"type\": \"ios\"}}";

        //Printing Request Details
        log.info("REQUEST-URL:POST-"+requestURL);
        log.info("REQUEST-URL:BODY-"+requestBody);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION).body(requestBody)
                .post(requestURL).then().statusCode(200).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //capturing created client ID
        capturedNewClientID=response.then().extract().path("data.id").toString();

        //JSON response Pay load validations
        response.then().body("data.id",is(notNullValue()));
        response.then().body("data.org_id",is(132));
        response.then().body("data.category_id",is(2));
        response.then().body("data.name",is(dynamicValue));
        response.then().body("data.type",is("ios"));
        response.then().body("data.containsKey('is_active')",is(true));
        response.then().body("data.containsKey('created_at')",is(true));
        response.then().body("data.containsKey('updated_at')",is(true));

    }


    @Test(priority=11)
    public void verify_Post_New_Client_emptyOrgID(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT_1;
        String requestBody= "{\"data\":{\"name\": \""+dynamicValue+"\",\"org_id\": \"\",\"category_id\": 2,\"type\": \"ios\"}}";

        //Printing Request Details
        log.info("REQUEST-URL:POST-"+requestURL);
        log.info("REQUEST-URL:BODY-"+requestBody);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION).body(requestBody)
                .post(requestURL).then().statusCode(400).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("error.messages.org_id",is("No organization specified."));

    }


    @Test(priority=12)
    public void verify_Post_New_Client_emptyCategoryID(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT_1;
        String requestBody= "{\"data\":{\"name\": \""+dynamicValue+"\",\"org_id\": 132 ,\"category_id\": \"\",\"type\": \"ios\"}}";

        //Printing Request Details
        log.info("REQUEST-URL:POST-"+requestURL);
        log.info("REQUEST-URL:BODY-"+requestBody);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION).body(requestBody)
                .post(requestURL).then().statusCode(400).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("error.messages.category_id",is("No category specified."));
    }

    @Test(priority=13)
    public void verify_Post_New_Client_emptyType(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT_1;
        String requestBody= "{\"data\":{\"name\": \""+dynamicValue+"\",\"org_id\": 132 ,\"category_id\": 2,\"type\": \"\"}}";

        //Printing Request Details
        log.info("REQUEST-URL:POST-"+requestURL);
        log.info("REQUEST-URL:BODY-"+requestBody);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION).body(requestBody)
                .post(requestURL).then().statusCode(400).extract().response();


        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("error.messages.type",is("No type specified."));
    }

    @Test(priority=14)
    public void verify_Post_New_Client_emptyName(){

        //Request Details
        String requestURL= CoreAPI_Constants.SERVICE_END_POINT+ CoreAPI_Constants.CLIENT_END_POINT_1;
        String requestBody= "{\"data\":{\"name\": \"\",\"org_id\": 132 ,\"category_id\": 2,\"type\": \"ios\"}}";

        //Printing Request Details
        log.info("REQUEST-URL:POST-"+requestURL);
        log.info("REQUEST-URL:BODY-"+requestBody);

        //Extracting response after status code validation
        Response response = given().header("Content-Type", "application/json")
                .header("Authorization",CoreAPI_Constants.AUTHORIZATION).body(requestBody)
                .post(requestURL).then().statusCode(400).extract().response();

        //printing response
        log.info("RESPONSE:"+response.asString());

        //JSON response Pay load validations
        response.then().body("error.messages.name",is("The name must be at least 1 character long."));
    }

    
}


