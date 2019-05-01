package com.control4.integration;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package ${PACKAGE}
 */

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.testng.annotations.*;
import org.testng.log4testng.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by Marvin Dean on 2/12/18.
 * This class has a test which will walk through the auth mechanism, grabbing JWT so that I can hit endpoints that require session auth.  All classes that
 * inherit from this base class will have access to the the baseURI and BasePath as well as the sessionid.  This class can also
 * be used to force inheritance for testNG related annotations and setup (like the @beforeClass annotation) leaving the test classes as POJO's for
 * better readability.
 **/

public abstract class IntegrationTestBase {

    protected String sessionId;
    protected Integer testRoomId;
    public Map<String, Integer> driverMapping = new HashMap<>();
    public static final Logger LOGGER = Logger.getLogger(IntegrationTestBase.class);

    @BeforeClass
    @Parameters ({ "BaseURI", "BasePath" })
    public void setUp(String BaseURI,
                      String BasePath) {

        Parser defaultParser = Parser.JSON;
        RestAssured.useRelaxedHTTPSValidation();
        enableLoggingOfRequestAndResponseIfValidationFails();
        baseURI = BaseURI;
        basePath = BasePath;

    }

    @AfterClass()
    public void cleanup() {
        //TODO Add cleanup code here if needed.  Avoid nesting BeforeClass and AfterClass in child classes.
    }

    @Test
    @Parameters({ "User", "Password", "Environment" })
    public void getLocalJWT(String user,
                            String password,
                            String environment) {

        //Get Request Token
        Response response = given().
            request().
            header("Content-Type", "application/json").
            body(" {\n" +
                 "   \"password\":\""+ password +"\",\n" +
                 "   \"user\":\""+ user +"\"\n" +
                 " }").
            when().
            post("localjwt");

        sessionId = response.jsonPath().get("token");
        System.out.println("Got the JWT for the " + environment + " environment...");

        //Validate Token is correct and works
        given().
            request().
            header("Authorization", "Bearer " + sessionId).
            expect().
            statusCode(200).
            when().
            get("status");
    }

    @Test
    public void getTestRoomId() {

        //Get Test Room Id
        Response response = given().
            request().
            header("Content-Type", "application/json").
            header("Authorization", "Bearer " + sessionId).
            queryParam("query","{\"name\":\"TestRoom\"}").
            when().
            get("items");

            testRoomId = response.jsonPath().get("[0].id");

        //If a test room did not exist, create one and return the id.
        if (testRoomId == null) {
            response = given().
                    request().
                    header("Content-Type", "application/json").
                    header("Authorization", "Bearer " + sessionId).
                    body("{ \n" +
                            "\t\"type\":\"room\",\n" +
                            "\t\"name\":\"TestRoom\"\n" +
                            "}").
                    when().
                    post("items");

            testRoomId = response.jsonPath().get("id");
        }
        System.out.println("Got the test room id of " + testRoomId);
    }

    //This is a method that sets a global map containing driver name and id for any driver type passed into the method.
    public void getDriverIds(String[] driverFileName) {
        String deviceQuery;

            if (driverFileName.length > 1) {
                 deviceQuery = "{\"$or\":[{\"filename\":\""+ driverFileName[0] +"\"},{\"filename\":\""+ driverFileName[1] +"\"},{\"filename\":\""+ driverFileName[2] +"\"}]}";
            } else {
                 deviceQuery = "{\"$and\":[{\"filename\":\""+ driverFileName[0] +"\"},{\"isOnline\":true}]}";
            }
            Response response = given().
                     request().
                     header("Authorization", "Bearer " + sessionId).
                     header("Content-Type", "application/json").
                     queryParam("query", deviceQuery).
                     expect().
                     statusCode(200).
                     when().
                     get("items").
                     then().extract().response();

                //Put the response in a list Set up a new hashmap for the converted id.
                ArrayList<Object> listResponse = (ArrayList<Object>) response.jsonPath().getList("$");
                Map<String, Integer> convertedMap;

                //Iterate the array of Hashmap objects convert the objects to hashmaps and then retrieve the id put it in the drivermapping hashtable....
                for (Integer i = 0; i < listResponse.size(); i++) {
                  if (response.jsonPath().get("isOnline["+ i +"]")) {
                    Object driver = listResponse.get(i);
                    convertedMap = (HashMap<String, Integer>) driver;
                    driverMapping.put(String.valueOf(convertedMap.get("name") + " " + i), convertedMap.get("id"));
                    }
                }
    }

    //This method will add a programmed event to the passed in device with action of setting the current LED color.
    public void addEventSetCurrentColor(Integer deviceId, Integer eventId) {
            given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body("{\n" +
                        "\"type\":1,\n" +
                        "\"codeItemId\":0,\n" +
                        "\"parentCodeItemId\":0,\n" +
                        "\"beforeCodeItemId\":0,\n" +
                        "\"deviceId\":"+deviceId+",\n" +
                        "\"creator\":3,\n" +
                        "\"command\":\"KEYPAD_BUTTON_COLOR\",\n" +
                        "\"params\":[\n" +
                        "{\"name\":\"BUTTON_ID\",\"value\":0,\"display\":\"Button 1\"},{\"name\":\"CURRENT_COLOR\",\"value\":\"FF0000\",\"display\":\"#FF0000\"}\n" +
                        "]\n" +
                        "}").
                expect().
                statusCode(200).
                when().
                post("items/"+deviceId+"/events/"+eventId+"/commands").prettyPeek().
                then().
                body("deviceId", equalTo(deviceId));
    }

    //This method will delete all the code items on the event associated with the device.
    public void deleteEventSetCurrentColor(Integer deviceId, Integer eventId) {

        //Check to see if any code items already exist.
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                expect().
                statusCode(200).
                when().
                get("items/"+deviceId+"/events/"+eventId+"/").
                then().
                extract().response();
                String codeItems = response.getBody().asString();

        //If codeitems exist, delete them
        if (codeItems.contains("codeItems") ) {
            given().
                    request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                    expect().
                    statusCode(200).
                    when().
                    delete("items/" + deviceId + "/events/" + eventId + "/0");
        }
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        Test test = method.getAnnotation(Test.class);
        LOGGER.info("Test description is " + test.description());
    }
}


