package com.control4.integration.commands;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package Package com.control4.integration.commands
 */

import com.control4.integration.IntegrationTestBase;
import com.control4.integration.utils.Dimmer_Helper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.*;

/** Retrieved from {{ base_url  }}/api/v1/items/<proxyid>/commands
     *  Commands Supported:
     *
     *    "SET_BACKLIGHT_COLOR", *
     *    "SET_AUTO_OFF", *
     *    "SET_AUTO_OFF:DISABLE", *
     *    "RESTART_AUTO_OFF", *
     *    "ON", *
     *    "OFF", *
     *    "TOGGLE", *
     *    "SET_LEVEL", *
     *    "RAMP_TO_LEVEL", *
     *    "RAMP_TO_PRESET", *
     *    "SET_ALL_LED", *
     *    "SET_BUTTON_COLOR", * When on
     *    "SET_BUTTON_COLOR", * When off
     *    "SET_BUTTON_COLOR", * Current color
     *    "BUTTON_ACTION" *
     */

@Test(dependsOnMethods={"getLocalJWT"})
public class Lighting_v2_Dimmer_Commands extends IntegrationTestBase {

    protected String[] fileName = {"adaptive_phase_dimmer.c4i", "forward_phase_dimmer.c4i", "combo_dimmer.c4i" };

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
        TOP(0), BOTTOM(1), TOGGLE(2);
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
            LOGGER.warn("No Dimmer Drivers in Project, Skipping Tests...");
        } else {
            driverMapping.forEach((key, value) -> LOGGER.info("Running tests for Driver:ID " + key + ":" + value));
        }
    }


    @Test(dataProvider = "dimmerIds", description = "Should turn on each dimmer.")
    public void turnOnTheDimmer(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getOnCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
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

        //Turn the dimmer off
        turnOff(id);
    }

    @Test(dataProvider = "dimmerIds", description = "Should toggle the Dimmer.")
    public void toggleTheDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getButtonToggleCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the state of the dimmer using the variables endpoint
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

        //Turn the dimmer off
        turnOff(id);
    }

    @Test(dataProvider = "dimmerIds", description = "Should set the level of the dimmer to 45%.")
    public void setLevelOfDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the state of the dimmer
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", equalTo(45));

        //Turn the dimmer off
        turnOff(id);
    }

    @Test(dataProvider = "dimmerIds", description = "Should ramp to the level 80% on the dimmer over 5 seconds.")
    public void rampToLevelDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getRampToLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        //Validate the state of the dimmer should still be ramping and less than 80% on light level.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", lessThanOrEqualTo(80));

        sleep(7000);
        //Now check to make sure level is at 80%
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", equalTo(80));

        //Turn the dimmer off
        turnOff(id);
    }

    @Test(dataProvider = "dimmerIds", description = "Should ramp to the Preset level of 100% on the dimmer over 5 seconds.")
    public void rampToPresetDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getRampToPresetCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        //Validate the state of the dimmer should still be ramping and less than 100% on light level.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", lessThan(100));

        sleep(7000);
        //Now check to make sure level is at 100%
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", equalTo(100));

        //Turn the dimmer off
        turnOff(id);
    }

    @Test(dataProvider = "dimmerIds", description = "Should set all LED colors on the dimmer.")
    public void setAllLEDDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetAllLEDsCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the led colors were set
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightPropertiesCommand()).
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

    @Test(dataProvider = "dimmerIds", description = "Should set button color when ON, in this case top button set to RED.")
    public void setButtonColorWhenOnDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetButtonColorWhenOnCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);

        //Validate the button color setting when on.  In this case top button should be red (xFF0000)
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].ON_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "dimmerIds", description = "Should set button color when OFF, in this case top button to Black.")
    public void setButtonColorWhenOffDimmer(Integer id) throws InterruptedException {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetButtonColorWhenOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        //Validate the button color setting when off.  In this case the top button should be black (x000000)
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("State.BUTTON_LIST_INFO.BUTTON_INFO[0].OFF_COLOR", equalTo(0));
    }

    @Test(dataProvider = "dimmerIds", description = "Should set/change current button color, that is not persisted.")
    public void setButtonColorDimmer(Integer id) {
        given().
                //log().all().
                        request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetButtonCurrentColorCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        //sleep(3000);
        //TODO I have not found a way to validate this led change yet.  Only for Current color setting  For now rely on 200 status.
    }

    //TODO handle the other buttons and actions.
    @Test(dataProvider = "dimmerIds", description = "Should send the click action to the toggle button on the device.")
    public void sendButtonActionClickToToggleDimmer(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSendButtonActionCommand(buttonActions.CLICK.buttonActionId, buttonIds.TOGGLE.buttonId)).
                expect().
                statusCode(200).
                when().
                post("items/" + (id + 1) + "/commands").prettyPeek();

        sleep(3000);
        //Validate the button action was fired off
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", equalTo(100));
        //Turn off the device
        turnOff(id);
    }

    @Test(dataProvider = "dimmerIds", description = "Should set the Backlight color on the dimmer to RED")
    public void setBacklightColorDimmer(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetBacklightColorCommand("FF0000")).
                expect().
                statusCode(200).
                when().
                post("items/" + (id) + "/commands").prettyPeek();

        sleep(3000);
        //Validate the backlight color was changed
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.BACKLIGHT_COLOR", equalTo("FF0000"));
    }

    @Test(dataProvider = "dimmerIds", description = "Should set the Auto Off for a dimmer to 30 seconds.")
    public void setAutoOffDimmer(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetAutoOffCommand(30)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        sleep(3000);
        // Should validate that auto off is set to 30 seconds.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.AUTO_OFF_TIME", equalTo(30));
    }

    @Test(dataProvider = "dimmerIds", description = "Should disable the Auto Off for a dimmer.")
    public void disableAutoOffDimmer(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getDisableAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek();

        sleep(3000);
        // Should validate that auto off is set to 30 seconds.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightPropertiesCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id) +"/commands").prettyPeek().
                then().
                body("state.AUTO_OFF_TIME", equalTo(0));
    }

    @Test(dataProvider = "dimmerIds", description = "Should restart the auto off timer.")
    public void restartAutoOffDimmer(Integer id) throws InterruptedException {
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getSetAutoOffCommand(5)).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();

        // Should validate that auto off was restarted.  I am doing this by setting auto off to 5 seconds,
        // turning on the dimmer, sending the restart auto off, waiting an additional 6 seconds then checking the dimmer state.
        turnOn(id);
        sleep(3000);

        //reset the auto off timer
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getRestartAutoOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
        sleep(3000);
        //Check to see that light is still on.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek().
                then().
                body("light_level.level", greaterThan(0));

        sleep(8000);
        //Now check to see if light is off.
        given().
                request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getLightLevelCommand()).
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
                body(Dimmer_Helper.getOnCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
    }
    @Test(enabled=false)
    public void turnOff(Integer id) {
        given().request().
                header("Authorization", "Bearer " + sessionId).
                header("Content-Type", "application/json").
                body(Dimmer_Helper.getOffCommand()).
                expect().
                statusCode(200).
                when().
                post("items/"+ (id + 1) +"/commands").prettyPeek();
    }

    @DataProvider(name = "dimmerIds")
    public Object[][] ckData() {
        Object[][] obj = new Object[driverMapping.entrySet().size()][1];
        for (int i = 0; i < driverMapping.entrySet().size(); i++) {
            if (driverMapping.containsKey("Adaptive Phase Dimmer " + i)) {
                obj[i][0] = driverMapping.get("Adaptive Phase Dimmer " + i);
            } else if (driverMapping.containsKey("Forward Phase Dimmer " + i)) {
                obj[i][0] = driverMapping.get("Forward Phase Dimmer " + i);
            } else if (driverMapping.containsKey("Keypad Dimmer " + i)) {
                obj[i][0] = driverMapping.get("Keypad Dimmer " + i);
            }
        }
        return obj;
    }

}
