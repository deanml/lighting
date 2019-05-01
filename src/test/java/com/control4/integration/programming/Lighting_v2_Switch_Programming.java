package com.control4.integration.programming;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.commands
 */

import com.control4.integration.IntegrationTestBase;
import com.control4.integration.utils.Switch_Helper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

@Test(dependsOnMethods={"getLocalJWT"})
public class Lighting_v2_Switch_Programming extends IntegrationTestBase {

    protected String[] fileName = {"switch_gen3.c4i"};

    public enum buttonActions
    {
        PRESS(1), RELEASE(0), CLICK(2), DOUBLECLICK(3), TRIPLECLICK(4);
        private int buttonActionId;

        buttonActions(int buttonActionId) {
            this.buttonActionId = buttonActionId;
        }
    }

    public enum buttonIds
    {
        TOP(0), BOTTOM(1), TOGGLE(2);
        private int buttonId;

        buttonIds(int buttonId) {
            this.buttonId = buttonId;
        }
    }

    @Test(priority=-1)
    public void setup() {
        //Drivers of type <filename> that are not online are not returned since they will fail some of these tests.
        getDriverIds(fileName);
        if (driverMapping.isEmpty()) {
            LOGGER.warn("No Switch Drivers in Project, Skipping Tests...");
        } else {
            driverMapping.forEach((key, value) -> LOGGER.info("Running tests for Driver:ID " + key + ":" + value));
        }
    }

    @Test(enabled = false)
    public void turnOn(Integer id) {
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getOnCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
    }

    @Test(enabled = false)
    public void turnOff(Integer id) {
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
    }

    @DataProvider(name = "switchIds")
    public Object[][] ckData() {
        Object[][] obj = new Object[driverMapping.entrySet().size()][1];
        for (int i = 0; i < driverMapping.entrySet().size(); i++) {
            obj[i][0] = driverMapping.get("Switch " + i);
        }
        return obj;
    }
}
