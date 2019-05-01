package com.control4.integration.utils;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.utils
 */

//This class builds/returns the json objects to issue the commands to the lighting devices.

public class Switch_Helper {

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

    //When needing to create an array of json objects use Arrays.asList before 'new HashMap<>'
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

    public static Map<String, Object> getSetAllLEDsCommand() {
        Map<String, Object> SetAllLEDsCommand = new HashMap<>();
        SetAllLEDsCommand.put("tParams", (new HashMap<String, Object>() {{
            put("COLOR", "000066");
        }}));
        SetAllLEDsCommand.put("command", "SET_ALL_LED");
        SetAllLEDsCommand.put("async", false);
        return SetAllLEDsCommand;
    }

    public static Map<String, Object> getSetButtonColorWhenOnCommand() {
        Map<String, Object> SetButtonColorWhenOnCommand = new HashMap<>();
        SetButtonColorWhenOnCommand.put("tParams", (new HashMap<String, Object>() {{
            put("ON_COLOR", "FF0000");
            put("BUTTON_ID", 0);
        }}));
        SetButtonColorWhenOnCommand.put("command", "SET_BUTTON_COLOR");
        SetButtonColorWhenOnCommand.put("async", false);
        return SetButtonColorWhenOnCommand;
    }

    public static Map<String, Object> getSetButtonColorWhenOffCommand() {
        Map<String, Object> SetButtonColorWhenOffCommand = new HashMap<>();
        SetButtonColorWhenOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("OFF_COLOR", "000000");
            put("BUTTON_ID", 0);
        }}));
        SetButtonColorWhenOffCommand.put("command", "SET_BUTTON_COLOR");
        SetButtonColorWhenOffCommand.put("async", false);
        return SetButtonColorWhenOffCommand;
    }

    public static Map<String, Object> getSetButtonCurrentColorCommand() {
        Map<String, Object> SetButtonCurrentColorCommand = new HashMap<>();
        SetButtonCurrentColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("CURRENT_COLOR", "00FF00");
            put("BUTTON_ID", 1);
        }}));
        SetButtonCurrentColorCommand.put("command", "SET_BUTTON_COLOR");
        SetButtonCurrentColorCommand.put("async", false);
        return SetButtonCurrentColorCommand;
    }

    public static Map<String, Object> getSendButtonActionCommand(Integer buttonAction, Integer buttonId) {
        Map<String, Object> SendButtonActionCommand = new HashMap<>();
        SendButtonActionCommand.put("tParams", (new HashMap<String, Object>() {{
            put("BUTTON_ID", buttonId);
            put("ACTION", buttonAction);
        }}));
        SendButtonActionCommand.put("command", "BUTTON_ACTION");
        SendButtonActionCommand.put("async", false);
        return SendButtonActionCommand;
    }

    public static Map<String, Object> getLightPropertiesCommand() {
        Map<String, Object> SendLightPropertiesCommand = new HashMap<>();
        SendLightPropertiesCommand.put("command", "GET_PROPERTIES");
        SendLightPropertiesCommand.put("async", false);
        return SendLightPropertiesCommand;
    }

    public static Map<String, Object> getSetBacklightColorCommand() {
        Map<String, Object> SendSetBacklightColorCommand = new HashMap<>();
        SendSetBacklightColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("COLOR", "FF0000");
        }}));
        SendSetBacklightColorCommand.put("command", "SET_BACKLIGHT_COLOR");
        SendSetBacklightColorCommand.put("async", false);
        return SendSetBacklightColorCommand;
    }

    public static Map<String, Object> getSetAutoOffCommand(Integer interval) {
        Map<String, Object> SendAutoOffCommand = new HashMap<>();
        SendAutoOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("TIME", interval);
        }}));
        SendAutoOffCommand.put("command", "SET_AUTO_OFF");
        SendAutoOffCommand.put("async", false);
        return SendAutoOffCommand;
    }

    public static Map<String, Object> getDisableAutoOffCommand() {
        Map<String, Object> SendDisableAutoOffCommand = new HashMap<>();
        SendDisableAutoOffCommand.put("command", "SET_AUTO_OFF:DISABLE");
        SendDisableAutoOffCommand.put("async", false);
        return SendDisableAutoOffCommand;
    }

    public static Map<String, Object> getRestartAutoOffCommand() {
        Map<String, Object> SendRestartAutoOffCommand = new HashMap<>();
        SendRestartAutoOffCommand.put("command", "RESTART_AUTO_OFF");
        SendRestartAutoOffCommand.put("async", false);
        return SendRestartAutoOffCommand ;
    }

    public static Map<String, Object> getLightLevelCommand() {
        Map<String, Object> SendLightLevelCommand = new HashMap<>();
        SendLightLevelCommand.put("command", "GET_LIGHT_LEVEL");
        SendLightLevelCommand.put("async", false);
        return SendLightLevelCommand ;
    }
}
