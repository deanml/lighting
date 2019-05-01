package com.control4.integration.commands;

import com.control4.integration.IntegrationTestBase;
import com.control4.integration.utils.FSC_Helper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.commands
 */

/**   [
        PROXY
         "Set Backlight Color", *
         "Set Auto Off Time", *
         "Disable Auto Off", *
         "Restart Auto Off Timer", *
         "Fan On", *
         "Fan Off", *
         "Fan Toggle", *
         "Speed Up", *
         "Speed Down", *
         "Set Speed", *
         "Designate Preset" *
         ]
        KEYPAD
         [
         "Set Backlight Color", *
         "Set Auto Off Time", *
         "Disable Auto Off", *
         "Restart Auto Off Timer", *
         "Use On Color", *
         "Use Off Color", *
         "Set Button LED when On", *
         "Set Button LED when Off", *
         "Set Button LED", *
         "Set all LEDs when On", *
         "Set all LEDs when Off", *
         "Set all LEDs", *
         "Press Button",
         "Release Button",
         "Single Click Button",
         "Double Click Button",
         "Triple Click Button"
      ]
 */

@Test(dependsOnMethods={"getLocalJWT"})
public class Lighting_v2_FSC_Commands extends IntegrationTestBase {

    protected String[] fileName = {"fan_speed_controller.c4i"};

    // Taken from /api/v1/items/<proxy id>/commands GET SET_SPEED command.
    public enum fanSpeed {
        OFF(0), LOW(1), MEDIUM(2), MEDIUMHIGH(3), HIGH(4);
        private int fanSpeedId;

        private fanSpeed(int fanSpeedId) {
            this.fanSpeedId = fanSpeedId;
        }
    }

    // Taken from /api/v1/items/<keypad ID>/commands POST with GET_PROPERTIES command
    public enum fscButtonIds {
        HIGH(0), MEDHIGH(1), MEDIUM(2), LOW(3), OFF(4);
        private int buttonId;

        private fscButtonIds(int buttonId) {
            this.buttonId = buttonId;
        }
    }

    public enum buttonActions
    {
        PRESS(1), RELEASE(0), CLICK(2), DOUBLECLICK(3), TRIPLECLICK(4);
        private int buttonActionId;

        private buttonActions(int buttonActionId) {
            this.buttonActionId = buttonActionId;
        }
    }

    @Test(priority=-1)
    public void setup() {

        //Drivers of type <filename> that are not online are not returned since they will fail some of these tests.
        getDriverIds(fileName);
        if (driverMapping.isEmpty()) {
            LOGGER.warn("No "+ fileName +" Drivers in Project, Skipping Tests...");
        } else {
            driverMapping.forEach((key, value) -> LOGGER.info("Running tests for Driver:ID " + key + ":" + value));
        }
    }

    @Test(dataProvider = "fscIds", description = "Should turn on the Fan to Preset Level.")
    public void turnOnTheFSC(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFanOnCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(4000);
        //Validate the state of the fan using the variables endpoint
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"IS_ON\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", greaterThan(0));

        turnOff(id);
    }

    @Test(dataProvider = "fscIds", description = "Should turn on the Fan Off.")
    public void turnOffTheFSC(Integer id) throws InterruptedException {
        turnOn(id);
        sleep(2000);
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFanOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(4000);
        //Validate the state of the fan using the variables endpoint
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"IS_ON\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", equalTo(0));
    }

    @Test(dataProvider = "fscIds", description = "Should toggle the Fan On and Off.")
    public void toggleTheFSC(Integer id) throws InterruptedException {
        //Make sure the fan is off and auto off disabled
        turnOff(id);
        disableAutoOff(id);

        sleep(2000);
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFanToggleCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(4000);
        //Validate the state of the fan using the variables endpoint
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"IS_ON\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", greaterThan(0));

        //Toggle it back off
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFanToggleCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(4000);
        //Check that it is off again
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"IS_ON\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", equalTo(0));
    }

    @Test(dataProvider = "fscIds", description = "Should set the backlight color to RED on the FSC.")
    public void setBackLightColorFSC(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetBacklightColorCommand("FF0000")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);
        //Validate the backlight color was set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").
                then().
                body("state.BACKLIGHT_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "fscIds", description = "Should set the auto off timer on the FSC to 30 seconds.")
    public void setAutoOffFSC(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetAutoOffCommand(30)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);
        //Validate the backlight color was set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").
                then().
                body("state.AUTO_OFF_TIME", equalTo(30));
    }

    @Test(dataProvider = "fscIds", description = "Should disable the  auto off timer on the FSC.")
    public void disableAutoOffFSC(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getDisableAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);
        //Validate the backlight color was set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").
                then().
                body("state.AUTO_OFF_TIME", equalTo(0));
    }

    @Test(dataProvider = "fscIds", description = "Should restart the auto off timer on the FSC.")
    public void restartAutoOffFSC(Integer id) throws InterruptedException {
        given().
                //log().all().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetAutoOffCommand(5)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

                // Should validate that auto off was restarted.  I am doing this by setting auto off to 5 seconds,
                // turning on the fsc, sending the restart auto off, waiting an additional 6 seconds then checking the fsc state.

                turnOn(id);
        sleep(3000);

        //reset the auto off timer
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getRestartAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);

        //Validate the state of the fan using the variables endpoint checking for on
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"IS_ON\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", greaterThan(0));

        sleep(8000);

        //Now check to see if fan is off.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"IS_ON\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", equalTo(0));
    }

    @Test(dataProvider = "fscIds", description = "Should adjust speed up.")
    public void speedUpFSC(Integer id) throws InterruptedException {
        //Make sure fan is off
        turnOff(id);
        sleep(2000);
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSpeedUpCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);
        //Validate the FSC speed was adjusted up.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"FAN_SPEED\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", equalTo(1));

        turnOff(id);
    }

    @Test(dataProvider = "fscIds", description = "Should adjust speed down.")
    public void speedDownFSC(Integer id) throws InterruptedException {
        //Set fan speed to medium or 2
        setSpeed(id, fanSpeed.MEDIUM.fanSpeedId);

        sleep(2000);
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSpeedDownCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);
        //Validate the FSC speed was adjusted down.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"FAN_SPEED\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", equalTo(1));

        turnOff(id);
    }

    @Test(dataProvider = "fscIds", description = "Should adjust the preset.")
    public void designatePresetFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                //log().all().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getPresetCommand(fanSpeed.MEDIUMHIGH.fanSpeedId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");

        sleep(3000);
        //Validate the preset was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").
                then().
                body("state.PRESET_SPEED", equalTo(3));
    }

    @Test(dataProvider = "fscIds", description = "Should set the speed.")
    public void setSpeedFSC(Integer id) throws InterruptedException {
        //Set fan speed to medium or 2
        setSpeed(id, fanSpeed.MEDIUM.fanSpeedId);

        sleep(3000);
        //Validate the FSC speed was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                queryParam("q", "{\"varName\":\"FAN_SPEED\"}").
                expect().
                statusCode(200).
                when().
                get("items/"+ (id + 1) +"/variables").
                then().
                body("[0].value", equalTo(2));

        turnOff(id);
    }

    //        FSC Keypad commands

    @Test(dataProvider = "fscIds", description = "Should use the on color on button 2.")
    public void setUseOnColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getUseOnColorCommand(fscButtonIds.MEDIUM.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");
        //This command maps to the GET_KEYPAD_INFO c4soap command.  Nothing to validate here but will send command and check for 200 Result.
    }

    @Test(dataProvider = "fscIds", description = "Should use the off color on button 2.")
    public void setUseOffColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getUseOffColorCommand(fscButtonIds.MEDIUM.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");
        //This command maps to the GET_KEYPAD_INFO c4soap command.  Nothing to validate here but will send command and check for 200 Result.
    }

    @Test(dataProvider = "fscIds", description = "Should set the button color on the button 2 when on to GREEN.")
    public void setLEDWhenOnFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetButtonColorWhenOnCommand("00FF00", fscButtonIds.MEDIUM.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(3000);
        //Validate the led was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+ fscButtonIds.MEDIUM.buttonId+"].ON_COLOR", equalTo("00FF00"));
    }

    @Test(dataProvider = "fscIds", description = "Should set the button color on the button 2 when off, to RED.")
    public void setLEDWhenOffFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetButtonColorWhenOffCommand("FF0000", fscButtonIds.MEDIUM.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(3000);
        //Validate the led was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+ fscButtonIds.MEDIUM.buttonId+"].OFF_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "fscIds", description = "Should set the button LED color on the button 2 to RED.")
    public void setButtonLEDFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetButtonColorCommand("FF0000", fscButtonIds.MEDIUM.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(3000);
        //Validate the led was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+ fscButtonIds.MEDIUM.buttonId+"].CURRENT_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "fscIds", description = "Should set all LEDS when on to BLUE.")
    public void setAllLEDsWhenOnFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetAllLEDsWhenOnCommand("0000ff")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(3000);
        //Validate the led was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[0].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[1].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[2].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[3].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[4].ON_COLOR", equalTo("0000ff"));
    }

    @Test(dataProvider = "fscIds", description = "Should set all LEDS when off to BLACK.")
    public void setAllLEDsWhenOffFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetAllLEDsWhenOffCommand("000000")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(3000);
        //Validate the led was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[0].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[1].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[2].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[3].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[4].OFF_COLOR", equalTo(0));
    }

    @Test(dataProvider = "fscIds", description = "Should set all LEDS color.")
    public void setAllLEDsFSC(Integer id) throws InterruptedException {
        //Set the preset to medium high
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetAllLEDsCommand("00FF00")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(3000);
        //Validate the led was adjusted.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[0].CURRENT_COLOR", equalTo("00FF00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[1].CURRENT_COLOR", equalTo("00FF00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[2].CURRENT_COLOR", equalTo("00FF00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[3].CURRENT_COLOR", equalTo("00FF00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[4].CURRENT_COLOR", equalTo("00FF00"));
    }

    @Test(dataProvider = "fscIds", description = "Should set current led to RED when button 1 is single clicked.")
    public void singleClickButtonFSC(Integer id) throws InterruptedException {

        //Delete the programming items if any.
        deleteEventSetCurrentColor(id + 2, (5000 + buttonActions.CLICK.buttonActionId));

        //Create a programming item on keypad proxy to 'when button LOW is single clicked, set current color to RED.'
        addEventSetCurrentColor(id + 2, (5000 + buttonActions.CLICK.buttonActionId));

        sleep(2000);
        //Now send the press button 1 command
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSendButtonActionCommand((buttonActions.CLICK.buttonActionId), fscButtonIds.HIGH.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands");

        sleep(2000);

        //Validate the current led color was set for button 1
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFSCPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 2) +"/commands").
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+ fscButtonIds.HIGH.buttonId+"].CURRENT_COLOR", equalTo("FF0000"));

        sleep(2000);
        //Delete the programming item.
        deleteEventSetCurrentColor(id + 2, (5000 + buttonActions.CLICK.buttonActionId));

    }



    //        Local Utility Methods and Data Providers

    @Test(enabled = false)
    public void turnOn(Integer id) {
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFanOnCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");
    }
    @Test(enabled=false)
    public void turnOff(Integer id) {
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getFanOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");
    }

    @Test(enabled=false)
    public void disableAutoOff(Integer id) {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getDisableAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");
    }

    @Test(enabled=false)
    public void setSpeed(Integer id, Integer speedValue) {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(FSC_Helper.getSetSpeedCommand(speedValue)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands");
    }

    @DataProvider(name = "fscIds")
    public Object[][] ckData() {
        Object[][] obj = new Object[driverMapping.entrySet().size()][1];
        for (int i = 0; i < driverMapping.entrySet().size(); i++) {
            obj[i][0] = driverMapping.get("Fan Speed Controller " + i);
        }
        return obj;
    }

}
