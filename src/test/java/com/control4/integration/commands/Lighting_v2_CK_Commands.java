package com.control4.integration.commands;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.commands
 */

import com.control4.integration.IntegrationTestBase;
import com.control4.integration.utils.CK_Helper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.equalTo;

/**  Retrieved from {{ base_url  }}/api/v1/items/<proxyid>/commands
     *
     *   "Set Backlight Color",
     *   "Use On Color",
     *   "Use Off Color",
     *   "Set Button LED when On",
     *   "Set Button LED when Off",
     *   "Set Button LED",
     *   "Set all LEDs when On",
     *   "Set all LEDs when Off",
     *   "Set all LEDs",
     *   "Press Button",
     *   "Release Button",
     *   "Single Click Button",
     *   "Double Click Button",
     *   "Triple Click Button"
     */

@Test(dependsOnMethods={"getLocalJWT"})
public class Lighting_v2_CK_Commands extends IntegrationTestBase {

    protected String[] fileName = {"configurable_keypad.c4i"};

    public enum buttonActions
    {
        PRESS(1), RELEASE(0), CLICK(2), DOUBLECLICK(3), TRIPLECLICK(4);
        private int buttonActionId;

        private buttonActions(int buttonActionId) {
            this.buttonActionId = buttonActionId;
        }
    }

    public enum buttonIds
    {
        ONE(0), TWO(1), THREE(2), FOUR(3), UP(5), DOWN(6);
        private int buttonId;

        private buttonIds(int buttonId) {
            this.buttonId = buttonId;
        }
    }

    @Test(priority=-1)
    public void setup() {

        //Drivers of type <filename> that are not online are not returned since they will fail some of these tests.
        getDriverIds(fileName);
        if (driverMapping.isEmpty()) {
            LOGGER.warn("No Configurable Keypad Drivers in Project, Skipping Tests...");
        } else {
        driverMapping.forEach((key, value) -> LOGGER.info("Running tests for Driver:ID " + key + ":" + value));
        }
    }

    @Test(dataProvider = "keypadIds", description = "Should set the backlight color to RED on the configurable keypad.")
    public void setBackLightColorKeypad(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSetBacklightColorCommand("FF0000")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the backlight color was set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.BACKLIGHT_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "keypadIds", description = "Should set the on color to RED for Button ID 0 ")
    public void setOnColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSetButtonColorWhenOnCommand("FF0000", buttonIds.ONE.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the on color was set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+buttonIds.ONE.buttonId+"].ON_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "keypadIds", description = "Should use the on color on button 2.")
    public void setOffColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getUseOffColorCommand(buttonIds.TWO.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
                //This command maps to the GET_KEYPAD_INFO c4soap command.  Nothing to validate here but will send command and check for 200 Result.
    }

    @Test(dataProvider = "keypadIds", description = "Should use the on color on button 2.")
    public void setUseOnColor(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getUseOnColorCommand(buttonIds.TWO.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
        //This command maps to the GET_KEYPAD_INFO c4soap command.  Nothing to validate here but will send command and check for 200 Result.
    }

    @Test(dataProvider = "keypadIds", description = "Should set the led color on button 2.")
    public void setButtonLED(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSetButtonLEDCommand(buttonIds.TWO.buttonId, "00ff00")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the led color was set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+buttonIds.TWO.buttonId+"].CURRENT_COLOR", equalTo("00ff00"));

    }

    @Test(dataProvider = "keypadIds", description = "Should set all the leds to color BLUE when on.")
    public void setAllLEDsWhenOn(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSetAllLEDsWhenOnCommand( "0000ff")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        //Validate the led color was set for all buttons
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[0].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[1].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[2].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[3].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[4].ON_COLOR", equalTo("0000ff")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[5].ON_COLOR", equalTo("0000ff"));
    }

    @Test(dataProvider = "keypadIds", description = "Should set all the leds to color BLACK when off.")
    public void setAllLEDsWhenOff(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSetAllLEDsWhenOffCommand( "000000")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        //Validate the led color was set for all buttons
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[0].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[1].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[2].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[3].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[4].OFF_COLOR", equalTo(0)).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[5].OFF_COLOR", equalTo(0));
    }

    @Test(dataProvider = "keypadIds", description = "Should set all the leds to color GREEN.")
    public void setAllLEDs(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSetAllLEDsCommand( "00ff00")).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);
        //Validate the led color was set for all buttons to green
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[0].CURRENT_COLOR", equalTo("00ff00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[1].CURRENT_COLOR", equalTo("00ff00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[2].CURRENT_COLOR", equalTo("00ff00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[3].CURRENT_COLOR", equalTo("00ff00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[4].CURRENT_COLOR", equalTo("00ff00")).
                and().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO[5].CURRENT_COLOR", equalTo("00ff00"));
    }

    //TODO - implment all the test functions for button clicks.  I've only done single click...
    @Test(dataProvider = "keypadIds", description = "Should set the led to RED when button 1 is pressed.")
    public void pressButton(Integer id) throws InterruptedException {

    }

    @Test(dataProvider = "keypadIds", description = "Should set the led to RED when button 1 is released.")
    public void releaseButton(Integer id) throws InterruptedException {

    }

    @Test(dataProvider = "keypadIds", description = "Should set current led to RED when button 1 is single clicked.")
    public void singleClickButton(Integer id) throws InterruptedException {

        //Delete the programming items if any.
        deleteEventSetCurrentColor(id + 1, (5000 + buttonActions.CLICK.buttonActionId));

        //Create a programming item on keypad proxy to 'when button 1 is single clicked, set current color to RED.'
        addEventSetCurrentColor(id + 1, (5000 + buttonActions.CLICK.buttonActionId));

        sleep(2000);
        //Now send the press button 1 command
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getSendButtonActionCommand((buttonActions.CLICK.buttonActionId), buttonIds.ONE.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(2000);

        //Validate the current led color was set for button 1
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(CK_Helper.getCKPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.KEYPAD_BUTTON_INFO["+buttonIds.ONE.buttonId+"].CURRENT_COLOR", equalTo("FF0000"));

        sleep(2000);
        //Delete the programming item.
        deleteEventSetCurrentColor(id + 1, (5000 + buttonActions.CLICK.buttonActionId));
    }

    @Test(dataProvider = "keypadIds", description = "Should set all the LED to RED when button one is double clicked.")
    public void doubleClickButton(Integer id) throws InterruptedException {

    }

    @Test(dataProvider = "keypadIds", description = "Should set all the led to RED when button one is triple clicked.")
    public void tripleClickButton(Integer id) throws InterruptedException {

    }

    @DataProvider(name = "keypadIds")
    public Object[][] ckData() {
       Object[][] obj = new Object[driverMapping.entrySet().size()][1];
       for (int i = 0; i < driverMapping.entrySet().size(); i++) {
            obj[i][0] = driverMapping.get("Configurable Keypad " + i);
       }
        return obj;
    }
}
