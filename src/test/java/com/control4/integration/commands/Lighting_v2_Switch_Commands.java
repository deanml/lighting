package com.control4.integration.commands;

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
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**  Retrieved from {{ base_url  }}/api/v1/items/<proxyid>/commands
     *  Supported commands for Switches:
     *  "Set Backlight Color",
     *  "Set Auto Off Time",
     *  "Disable Auto Off",
     *  "Restart Auto Off Timer",
     *  "Turn On",
     *  "Turn Off",
     *  "Toggle",
     *  "Set all LEDs",
     *  "Set Button Color When On",
     *  "Set Button Color When Off",
     *  "Set Button Color",
     *  "Send Button Action"
     */

@Test(dependsOnMethods={"getLocalJWT"})
public class Lighting_v2_Switch_Commands extends IntegrationTestBase {

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

    @Test(dataProvider = "switchIds", description = "Should turn on the Switch.")
    public void turnOnTheSwitch(Integer id) throws InterruptedException {
            given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                    body(Switch_Helper.getOnCommand()).
                expect().
                    statusCode(200).
                when().
                    post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        //Validate the state of the light using the variables endpoint
        given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                    queryParam("q", "{\"varName\":\"LIGHT_STATE\"}").
                expect().
                    statusCode(200).
                when().
                    get("items/"+ (id + 1) +"/variables").prettyPeek().
                then().
                    body("[0].value", equalTo(1));

        //Turn the light back off
        turnOff(id);
    }

    @Test(dataProvider = "switchIds", description = "Should toggle the Switch.")
    public void toggleTheSwitch(Integer id) throws InterruptedException {
        given().
                //log().all().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                body(Switch_Helper.getButtonToggleCommand()).
                expect().
                    statusCode(200).
                when().
                    post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        //Validate the state of the light using the variables endpoint
        given().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                    queryParam("q", "{\"varName\":\"LIGHT_STATE\"}").
                expect().
                    statusCode(200).
                when().
                    get("items/"+ (id + 1) +"/variables").prettyPeek().
                then().
                    body("[0].value", equalTo(1));

        //Turn the light back off
        turnOff(id);
    }

    @Test(dataProvider = "switchIds", description = "Should set all LED's ON/OFF a specific color, in this case 000066 or blue")
    public void setAllLEDsColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                body(Switch_Helper.getSetAllLEDsCommand()).
                expect().
                    statusCode(200).
                when().
                    post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);

        //Validate the colors of the LEDs
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].OFF_COLOR", equalTo(66)).
                and().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].ON_COLOR", equalTo(66)).
                and().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[1].OFF_COLOR", equalTo(66)).
                and().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[1].ON_COLOR", equalTo(66)).
                and().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[2].OFF_COLOR", equalTo(66));

    }

    @Test(dataProvider = "switchIds", description = "Should set button color when ON, in this case top button set to RED.")
    public void setButtonColorWhenOn(Integer id) throws InterruptedException {
        given().
                //log().all().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                    body(Switch_Helper.getSetButtonColorWhenOnCommand()).
                expect().
                    statusCode(200).
                when().
                    post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);

        //Validate the button color setting when on.  In this case top button should be red (xFF0000)
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].ON_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "switchIds", description = "Should set button color when OFF, in this case top button to Black.")
    public void setButtonColorWhenOff(Integer id) throws InterruptedException {
        given().
                //log().all().
                request().
                    header("Authorization", "Bearer " + sessionId).
                    header("Content-Type", "application/json").
                    body(Switch_Helper.getSetButtonColorWhenOffCommand()).
                expect().
                    statusCode(200).
                when().
                    post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        //Validate the button color setting when off.  In this case the top button should be black (x000000)
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].OFF_COLOR", equalTo(0));
    }

    @Test(dataProvider = "switchIds", description = "Should set/change current button color, that is not persisted.")
    public void setButtonColor(Integer id) {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getSetButtonCurrentColorCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        //sleep(2000);
        //TODO I have not found a way to validate this led change yet.  Only for Current color setting  For now rely on 200 status.
        //Validate the current button color setting.  In this case the botton button should be red (xFF0000)
/*        ValidatableResponse response = given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (driverId + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].COLOR", equalTo(0));*/
    }

    //TODO add tests for all the actions, press, release, double click, triple click
    @Test(dataProvider = "switchIds", description = "Should send a click button action, in this case sending the click action to the toggle button.")
    public void sendClickButtonAction(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                //In this case the button is the toggle button and the action is click ButtonId: 2, Action: 2
                body(Switch_Helper.getSendButtonActionCommand(buttonActions.CLICK.buttonActionId, buttonIds.TOGGLE.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);

        //Validate that that the button action fired.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"LIGHT_STATE\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").prettyPeek().
                then().
                body("[0].value", equalTo(1));

        //Turn off the light
        turnOff(id);

    }

    @Test(dataProvider = "switchIds", description = "Should set the backlight color for the switch, in this case red.")
    public void setBacklightColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getSetBacklightColorCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek();

        sleep(2000);
        // Validate that that the color for backlighting was indeed changed.
        // Should be red (xFF0000), also make the properties query on the driver not the proxy.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.BACKLIGHT_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "switchIds", description = "Should set the Auto Off for a light switch.")
    public void setAutoOff(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getSetAutoOffCommand(30)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        // Should validate that auto off is set to 30 seconds.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.AUTO_OFF_TIME", equalTo(30));
    }

    @Test(dataProvider = "switchIds", description = "Should disable the auto off.")
    public void disableAutoOff(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getDisableAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek();

        sleep(2000);
        // Should validate that auto off is disabled.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.AUTO_OFF_TIME", equalTo(0));
    }

    @Test(dataProvider = "switchIds", description = "Should restart the timer for auto off.")
    public void restartAutoOff(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getSetAutoOffCommand(5)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        // Should validate that auto off was restarted.  I am doing this by setting auto off to 5 seconds,
        // turning on the light, sending the restart auto off, waiting an additional 5 seconds then checking the light state.
        turnOn(id);
        sleep(2000);

        //reset the auto off timer
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getRestartAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        //Wait another 4 seconds check to make sure light is still on
        sleep(4000);
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", greaterThan(0));

        sleep(3000);
        //Now check to see if light is off.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Switch_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", equalTo(0));
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
