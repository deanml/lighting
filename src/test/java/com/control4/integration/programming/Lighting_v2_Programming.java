package com.control4.integration.programming;


/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package ${PACKAGE}
 */

import com.control4.integration.IntegrationTestBase;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Test(dependsOnMethods={"getLocalJWT"})
public class Lighting_v2_Programming extends IntegrationTestBase {

    private static Map<String, Integer> driverMapping = new HashMap<>();

    @Test(dataProvider = "DriverDataProvider", priority=-1, description = "Should set up all the v2 devices in a test room.")
    public void setupDrivers(String type, String name, int parentId, String filename, int expected) {

        Response response = given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                body("{\n" +
                        "   \"type\": \""+ type +"\",\n" +
                        "   \"name\": \""+ name +"\",\n" +
                        "   \"parentId\": \""+ parentId +"\",\n" +
                        "   \"filename\": \""+ filename +"\"\n" +
                        "}").
                expect().
                    statusCode(expected).
                when().
                    post("items").prettyPeek();
        Integer driverId = response.jsonPath().get("id.protocol");
        driverMapping.put(name, driverId);
    }

    @Test(description = "Should test that our session id is good and controller is reachable.")
    public void testGetStatus() {
        given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                expect().
                    statusCode(200).
                when().
                    get("status").prettyPeek().
                then().
                    body("director", equalTo("connected")).
                    body(containsString("driverCacheProgress"));

    }

    @Test(description = "Should ensure that all test drivers exist and are ready to be acted upon.")
    public void ensureDriversExist() {
        given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                expect().
                    statusCode(200).
                when().
                    get("locations/rooms/" + testRoomId + "/properties/light_devices?fields=roomName,name").prettyPeek().
                then().
                    body("visible.name", containsInAnyOrder(
                            "apdv2",
                            "ffdv2",
                            "switchv2",
                            "kdv2",
                            "fscv2"
                    ));
    }

    @Test(description = "Should test that all the dimmer proxy commands are present and not changed.", dataProvider = "DimmerDataProvider")
    public void getDimmerProxyCommands(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                expect().
                    statusCode(expected).
                when().
                    get("items/" + ( driverMapping.get(name) + 1) + "/commands?fields=command").prettyPeek().
                then().
                    extract().response();
                String jsonStr = response.getBody().asString();
                assertThat(jsonStr, allOf(
                        containsString("ON"),
                        containsString("OFF"),
                        containsString("TOGGLE"),
                        containsString("SET_LEVEL"),
                        containsString("RAMP_TO_LEVEL"),
                        containsString("RAMP_TO_PRESET"),
                        containsString("SET_ALL_LED"),
                        containsString("SET_BUTTON_COLOR"),
                        containsString("BUTTON_ACTION"),
                        containsString("SET_BACKLIGHT_COLOR"),
                        containsString("SET_AUTO_OFF"),
                        containsString("SET_AUTO_OFF:DISABLE"),
                        containsString("RESTART_AUTO_OFF")
                ));
    }

    @Test(description = "Should test that all the dimmer driver commands are present and not changed.", dataProvider = "DimmerDataProvider")
    public void getDimmerDriverCommands(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                expect().
                    statusCode(expected).
                when().
                    get("items/" + ( driverMapping.get(name)) + "/commands").prettyPeek().
                then().
                    extract().response();
        String jsonStr = response.getBody().asString();
        assertThat(jsonStr, allOf(
                containsString("SET_BACKLIGHT_COLOR"),
                containsString("SET_AUTO_OFF"),
                containsString("SET_AUTO_OFF:DISABLE"),
                containsString("RESTART_AUTO_OFF")
                ));
    }

    @Test(description = "Should test that all dimmer proxy conditionals are present and not changed", dataProvider = "DimmerDataProvider")
    public void getDimmerProxyConditionalCommands(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                expect().
                    statusCode(expected).
                when().
                    get("items/" + ( driverMapping.get(name) + 1) + "/conditionals/?fields=label").prettyPeek().
                then().
                    extract().response();
        String jsonStr = response.getBody().asString();
        assertThat(jsonStr, allOf(
                containsString("Is On"),
                containsString("Is Off"),
                containsString("Level is"),
                containsString("Is Pressed"),
                containsString("Is Released"),
                containsString("LED Color Equals"),
                containsString("LED Color Not Equal")
        ));
    }

    @Test(description = "Should test that all dimmer driver conditionals are present and not changed", dataProvider = "DimmerDataProvider")
    public void getDimmerDriverConditionalCommands(String name, Integer expected) {
        System.out.println("Checking " + name);
        given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                expect().
                    statusCode(expected).
                when().
                    get("items/" + ( driverMapping.get(name) ) + "/conditionals/?fields=label").prettyPeek().
                then().
                    body("$", hasSize(0));
    }

    @Test(description = "Should test that all other proxy commands are present and not changed", dataProvider = "OtherDataProvider")
    public void getOtherProxyCommands(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(expected).
                when().
                get("items/" + ( driverMapping.get(name) + 1) + "/commands?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        if (name.equals("switchv2")) {
            assertThat(jsonStr, allOf(
                    containsString("Turn On"),
                    containsString("Turn Off"),
                    containsString("Toggle"),
                    containsString("Set all LEDs"),
                    containsString("Set Button Color When On"),
                    containsString("Set Button Color When Off"),
                    containsString("Set Button Color"),
                    containsString("Send Button Action"),
                    containsString("Set Backlight Color"),
                    containsString("Set Auto Off Time"),
                    containsString("Disable Auto Off"),
                    containsString("Restart Auto Off Timer")
            ));
        } else {
            assertThat(jsonStr, allOf(
                    containsString("Use On Color"),
                    containsString("Use Off Color"),
                    containsString("Set Button LED when On"),
                    containsString("Set Button LED when Off"),
                    containsString("Set Button LED"),
                    containsString("Set all LEDs when On"),
                    containsString("Set all LEDs when Off"),
                    containsString("Set all LEDs"),
                    containsString("Press Button"),
                    containsString("Release Button"),
                    containsString("Single Click Button"),
                    containsString("Double Click Button"),
                    containsString("Triple Click Button"),
                    containsString("Set Backlight Color")
            ));
        }
    }

    @Test(description = "Should test that all other driver commands are present and not changed", dataProvider = "OtherDataProvider")
    public void getOtherDriverCommands(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(expected).
                when().
                get("items/" + ( driverMapping.get(name) ) + "/commands?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        if (name.equals("switchv2") || name.equals("fscv2")) {
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "Set Backlight Color",
                    "Set Auto Off Time",
                    "Disable Auto Off",
                    "Restart Auto Off Timer"
            )));
        } else {
            assertThat(jsonStr, containsString("Set Backlight Color"));
        }
    }

    @Test(description = "Should test that all other proxy conditionals are present and not changed", dataProvider = "OtherDataProvider")
    public void getOtherProxyConditionals(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(expected).
                when().
                get("items/" + ( driverMapping.get(name) + 1) + "/conditionals?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        if (name.equals("switchv2")) {
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "Is On",
                    "Is Off",
                    "Level is",
                    "Is Pressed",
                    "Is Released",
                    "LED Color Equals",
                    "LED Color Not Equal"
            )));
        } else {
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "[]"
            )));
        }
    }

    @Test(dataProvider = "OtherDataProvider")
    public void getOtherDriverConditionals(String name, Integer expected) {
        System.out.println("Checking " + name);
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(expected).
                when().
                get("items/" + ( driverMapping.get(name) ) + "/commands?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        if (name.equals("switchv2")) {
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "Set Backlight Color",
                    "Set Auto Off Time",
                    "Disable Auto Off",
                    "Restart Auto Off Timer"
            )));
        } else {
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "Set Backlight Color"
            )));
        }
    }

    @Test(description = "Should test that all Fan Speed Controller driver commands are present and not changed")
    public void getFanSpeedControllerDriverCommands() {
        System.out.println("Checking fscv2");
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(200).
                when().
                get("items/" + ( driverMapping.get("fscv2") ) + "/commands?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "Set Backlight Color",
                    "Set Auto Off Time",
                    "Disable Auto Off",
                    "Restart Auto Off Timer"
            )));
    }

    @Test(description = "Should test that all Fan Speed Controller driver conditionals are present and not changed")
    public void getFanSpeedControllerDriverConditionals() {
        System.out.println("Checking fscv2");
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(200).
                when().
                get("items/" + ( driverMapping.get("fscv2") ) + "/conditionals?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
            assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                    "[]"
            )));
    }

    @Test(description = "Should test that all Fan Speed Controller proxy commands are present and not changed")
    public void getFanSpeedControllerProxyCommands() {
        System.out.println("Checking fscv2");
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(200).
                when().
                get("items/" + ( driverMapping.get("fscv2") + 1 ) + "/commands?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        assertThat(jsonStr, allOf(
                containsString("Fan On"),
                containsString("Fan Off"),
                containsString("Fan Toggle"),
                containsString("Speed Up"),
                containsString("Speed Down"),
                containsString("Set Speed"),
                containsString("Designate Preset"),
                containsString("Set Backlight Color"),
                containsString("Set Auto Off Time"),
                containsString("Disable Auto Off"),
                containsString("Restart Auto Off Timer")

        ));
    }

    @Test(description = "Should test that all Fan Speed Controller proxy conditionals are present and not changed")
    public void getFanSpeedControllerProxyConditionals() {
        System.out.println("Checking fscv2");
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(200).
                when().
                get("items/" + ( driverMapping.get("fscv2") + 1 ) + "/conditionals?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
                "Fan On",
                "Fan Off",
                "Current Speed is"
        )));
    }

    @Test(description = "Should test that all Fan Speed Controller keypad proxy commands are present and not changed")
    public void getFanSpeedControllerKeypadProxyCommands() {
        System.out.println("Checking fscv2");
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(200).
                when().
                get("items/" + ( driverMapping.get("fscv2") + 2 ) + "/commands?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        assertThat(jsonStr, allOf(
                containsString("Use On Color"),
                containsString("Use Off Color"),
                containsString("Set Button LED when On"),
                containsString("Set Button LED when Off"),
                containsString("Set Button LED"),
                containsString("Set all LEDs when On"),
                containsString("Set all LEDs when Off"),
                containsString("Set all LEDs"),
                containsString("Press Button"),
                containsString("Release Button"),
                containsString("Single Click Button"),
                containsString("Double Click Button"),
                containsString("Triple Click Button"),
                containsString("Set Backlight Color"),
                containsString("Set Auto Off Time"),
                containsString("Disable Auto Off"),
                containsString("Restart Auto Off Timer")
        ));
    }

    @Test(description = "Should test that all Fan Speed Controller keypad proxy conditionals are present and not changed")
    public void getFanSpeedControllerKeypadProxyConditionals() {
        System.out.println("Checking fscv2");
        Response response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                expect().
                statusCode(200).
                when().
                get("items/" + ( driverMapping.get("fscv2") + 2 ) + "/conditionals?fields=label").prettyPeek().
                then().
                extract().response();
        String jsonStr = response.getBody().asString();
        assertThat(jsonStr, stringContainsInOrder(Arrays.asList(
            "[]"
        )));
    }

    @AfterClass
    public void tearDownTests() {
        Iterator it = driverMapping.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                given().
                        request().
                            header("Authorization", "Bearer " + sessionId).
                        expect().
                            statusCode(200).
                        when().
                            delete("items/" + pair.getValue()).prettyPeek().
                        then().
                            body("status", equalTo("Deleted"));
        }
    }

    @DataProvider(name = "DriverDataProvider")
    public Object[][] driverData() {
        //type: name: parentId: filename: expected
        return new Object[][] {
                { "device", "apdv2", testRoomId, "adaptive_phase_dimmer.c4i", 200 },
                { "device", "ffdv2", testRoomId, "forward_phase_dimmer.c4i", 200 },
                { "device", "switchv2", testRoomId, "switch_gen3.c4i", 200 },
                { "device", "kdv2", testRoomId, "combo_dimmer.c4i", 200 },
                { "device", "ckeypadv2", testRoomId, "configurable_keypad.c4i", 200 },
                { "device", "fscv2", testRoomId, "fan_speed_controller.c4i", 200 }
        };
    }

    @DataProvider(name = "DimmerDataProvider")
    public Object[][] dimmerData() {
        //name: expected
        return new Object[][] {
                { "apdv2", 200 },
                { "ffdv2", 200 },
                { "kdv2", 200 }
        };
    }

    @DataProvider(name = "OtherDataProvider")
    public Object[][] otherData() {
        //name: expected
        return new Object[][] {
                { "switchv2", 200 },
                { "ckeypadv2", 200 }
        };
    }
}
