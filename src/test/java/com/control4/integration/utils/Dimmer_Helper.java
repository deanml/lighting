package com.control4.integration.utils;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.utils
 */

//This class builds/returns the json objects to issue the commands to the lighting device.
public class Dimmer_Helper {

    public static JSONObject getOnCommand() {
        JSONObject onCommand = new JSONObject();
        onCommand.put("command", "ON");
        onCommand.put("async", false);
        return onCommand;
    }

    public static JSONObject getOffCommand() {
        JSONObject offCommand = new JSONObject();
        offCommand.put("command", "OFF");
        offCommand.put("async", false);
        return offCommand;
    }

    public static Map<String, Object> getButtonToggleCommand() {
        Map<String, Object> buttonToggleCommand = new HashMap<>();
        buttonToggleCommand.put("tParams", (new HashMap<String, Object>() {{
            put("ACTION", 2);
            put("BUTTON_ID", 2);
        }}));
        buttonToggleCommand.put("command", "BUTTON_ACTION");
        buttonToggleCommand.put("async", false);
        return buttonToggleCommand;
    }

    public static Map<String, Object> getSetLevelCommand() {
        Map<String, Object> sendSetLevelCommand = new HashMap<>();
        sendSetLevelCommand.put("tParams", (new HashMap<String, Object>() {{
            put("LEVEL", 45);
        }}));
        sendSetLevelCommand.put("command", "SET_LEVEL");
        sendSetLevelCommand.put("async", false);
        return sendSetLevelCommand;
    }

    public static Map<String, Object> getLightLevelCommand() {
        Map<String, Object> sendGetLightLevelCommand = new HashMap<>();
        sendGetLightLevelCommand.put("command", "GET_LIGHT_LEVEL");
        sendGetLightLevelCommand.put("async", false);
        return sendGetLightLevelCommand;
    }

    public static Map<String, Object> getRampToLevelCommand() {
        Map<String, Object> sendRampToLevelCommand = new HashMap<>();
        sendRampToLevelCommand.put("tParams", (new HashMap<String, Object>() {{
            put("LEVEL", 80);
            put("TIME", 5000);
        }}));
        sendRampToLevelCommand.put("command", "RAMP_TO_LEVEL");
        sendRampToLevelCommand.put("async", false);
        return sendRampToLevelCommand;
    }

    public static Map<String, Object> getRampToPresetCommand() {
        Map<String, Object> sendRampToPresetCommand = new HashMap<>();
        sendRampToPresetCommand.put("tParams", (new HashMap<String, Object>() {{
            put("TIME", 5000);
        }}));
        sendRampToPresetCommand.put("command", "RAMP_TO_PRESET");
        sendRampToPresetCommand.put("async", false);
        return sendRampToPresetCommand;
    }

    public static Map<String, Object> getSetAllLEDsCommand() {
        Map<String, Object> sendSetAllLEDsCommand = new HashMap<>();
        sendSetAllLEDsCommand.put("tParams", (new HashMap<String, Object>() {{
            put("COLOR", "000066");
        }}));
        sendSetAllLEDsCommand.put("command", "SET_ALL_LED");
        sendSetAllLEDsCommand.put("async", false);
        return sendSetAllLEDsCommand;
    }

    public static Map<String, Object> getSetButtonColorWhenOnCommand() {
        Map<String, Object> sendSetButtonColorWhenOnCommand = new HashMap<>();
        sendSetButtonColorWhenOnCommand.put("tParams", (new HashMap<String, Object>() {{
            put("ON_COLOR", "FF0000");
            put("BUTTON_ID", 0);
        }}));
        sendSetButtonColorWhenOnCommand.put("command", "SET_BUTTON_COLOR");
        sendSetButtonColorWhenOnCommand.put("async", false);
        return sendSetButtonColorWhenOnCommand;
    }

    public static Map<String, Object> getSetButtonColorWhenOffCommand() {
        Map<String, Object> sendSetButtonColorWhenOffCommand = new HashMap<>();
        sendSetButtonColorWhenOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("OFF_COLOR", "000000");
            put("BUTTON_ID", 0);
        }}));
        sendSetButtonColorWhenOffCommand.put("command", "SET_BUTTON_COLOR");
        sendSetButtonColorWhenOffCommand.put("async", false);
        return sendSetButtonColorWhenOffCommand;
    }

    public static Map<String, Object> getSetButtonCurrentColorCommand() {
        Map<String, Object> sendSetButtonCurrentColorCommand = new HashMap<>();
        sendSetButtonCurrentColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("CURRENT_COLOR", "00FF00");
            put("BUTTON_ID", 1);
        }}));
        sendSetButtonCurrentColorCommand.put("command", "SET_BUTTON_COLOR");
        sendSetButtonCurrentColorCommand.put("async", false);
        return sendSetButtonCurrentColorCommand;
    }

    public static Map<String, Object> getLightPropertiesCommand() {
        Map<String, Object> sendLightPropertiesCommand = new HashMap<>();
        sendLightPropertiesCommand.put("command", "GET_PROPERTIES");
        sendLightPropertiesCommand.put("async", false);
        return sendLightPropertiesCommand;
    }

    public static Map<String, Object> getSendButtonActionCommand(Integer buttonAction, Integer buttonId) {
        Map<String, Object> sendButtonActionCommand = new HashMap<>();
        sendButtonActionCommand.put("tParams", (new HashMap<String, Object>() {{
            put("BUTTON_ID", buttonId);
            put("ACTION", buttonAction);
        }}));
        sendButtonActionCommand.put("command", "BUTTON_ACTION");
        sendButtonActionCommand.put("async", false);
        return sendButtonActionCommand;
    }

    public static Map<String, Object> getSetBacklightColorCommand(String colorCode) {
        Map<String, Object> sendSetBacklightColorCommand = new HashMap<>();
        sendSetBacklightColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("COLOR", colorCode);
        }}));
        sendSetBacklightColorCommand.put("command", "SET_BACKLIGHT_COLOR");
        sendSetBacklightColorCommand.put("async", false);
        return sendSetBacklightColorCommand;
    }

    public static Map<String, Object> getSetAutoOffCommand(Integer interval) {
        Map<String, Object> sendAutoOffCommand = new HashMap<>();
        sendAutoOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("TIME", interval);
        }}));
        sendAutoOffCommand.put("command", "SET_AUTO_OFF");
        sendAutoOffCommand.put("async", false);
        return sendAutoOffCommand;
    }

    public static Map<String, Object> getDisableAutoOffCommand() {
        Map<String, Object> sendDisableAutoOffCommand = new HashMap<>();
        sendDisableAutoOffCommand.put("command", "SET_AUTO_OFF:DISABLE");
        sendDisableAutoOffCommand.put("async", false);
        return sendDisableAutoOffCommand;
    }

    public static Map<String, Object> getRestartAutoOffCommand() {
        Map<String, Object> sendRestartAutoOffCommand = new HashMap<>();
        sendRestartAutoOffCommand.put("command", "RESTART_AUTO_OFF");
        sendRestartAutoOffCommand.put("async", false);
        return sendRestartAutoOffCommand ;
    }


}
