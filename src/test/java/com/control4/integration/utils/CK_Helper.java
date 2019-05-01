package com.control4.integration.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.utils
 */

//This class builds/returns the json objects to issue the commands to the lighting device.
public class CK_Helper {


    public static Map<String, Object> getCKPropertiesCommand() {
        Map<String, Object> sendCKPropertiesCommand = new HashMap<>();
        sendCKPropertiesCommand.put("command", "GET_PROPERTIES");
        sendCKPropertiesCommand.put("async", false);
        return sendCKPropertiesCommand;
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

    public static Map<String, Object> getSetButtonColorWhenOnCommand(String colorCode, Integer buttonId) {
        Map<String, Object> sendSetButtonColorWhenOnCommand = new HashMap<>();
        sendSetButtonColorWhenOnCommand.put("tParams", (new HashMap<String, Object>() {{
            put("ON_COLOR", colorCode);
            put("BUTTON_ID", buttonId);
        }}));
        sendSetButtonColorWhenOnCommand.put("command", "KEYPAD_BUTTON_COLOR");
        sendSetButtonColorWhenOnCommand.put("async", false);
        return sendSetButtonColorWhenOnCommand;
    }

    public static Map<String, Object> getSetButtonColorWhenOffCommand(String colorCode, Integer buttonId) {
        Map<String, Object> sendSetButtonColorWhenOffCommand = new HashMap<>();
        sendSetButtonColorWhenOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("OFF_COLOR", colorCode);
            put("BUTTON_ID", buttonId);
        }}));
        sendSetButtonColorWhenOffCommand.put("command", "KEYPAD_BUTTON_COLOR");
        sendSetButtonColorWhenOffCommand.put("async", false);
        return sendSetButtonColorWhenOffCommand;
    }

    public static Map<String, Object> getUseOnColorCommand(Integer buttonId) {
        Map<String, Object> sendUseOnColorCommand = new HashMap<>();
        sendUseOnColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("BUTTON_ID", buttonId);
        }}));
        sendUseOnColorCommand.put("command", "KEYPAD_BUTTON_INFO");
        sendUseOnColorCommand.put("async", false);
        return sendUseOnColorCommand;
    }

    public static Map<String, Object> getUseOffColorCommand(Integer buttonId) {
        Map<String, Object> sendUseOffColorCommand = new HashMap<>();
        sendUseOffColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("BUTTON_ID", buttonId);
        }}));
        sendUseOffColorCommand.put("command", "KEYPAD_BUTTON_INFO");
        sendUseOffColorCommand.put("async", false);
        return sendUseOffColorCommand;
    }

    public static Map<String, Object> getSetButtonLEDCommand(Integer buttonId, String colorCode) {
        Map<String, Object> sendSetButtonLEDCommand = new HashMap<>();
        sendSetButtonLEDCommand.put("tParams", (new HashMap<String, Object>() {{
            put("BUTTON_ID", buttonId);
            put("CURRENT_COLOR", colorCode);
        }}));
        sendSetButtonLEDCommand.put("command", "KEYPAD_BUTTON_COLOR");
        sendSetButtonLEDCommand.put("async", false);
        return sendSetButtonLEDCommand;
    }

    public static Map<String, Object> getSetAllLEDsWhenOnCommand(String colorCode) {
        Map<String, Object> sendSetAllLEDsWhenOnCommand = new HashMap<>();
        sendSetAllLEDsWhenOnCommand.put("tParams", (new HashMap<String, Object>() {{
            put("ON_COLOR", colorCode);
        }}));
        sendSetAllLEDsWhenOnCommand.put("command", "KEYPAD_ALL_BUTTON_COLOR");
        sendSetAllLEDsWhenOnCommand.put("async", false);
        return sendSetAllLEDsWhenOnCommand;
    }

    public static Map<String, Object> getSetAllLEDsWhenOffCommand(String colorCode) {
        Map<String, Object> sendSetAllLEDsWhenOffCommand = new HashMap<>();
        sendSetAllLEDsWhenOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("OFF_COLOR", colorCode);
        }}));
        sendSetAllLEDsWhenOffCommand.put("command", "KEYPAD_ALL_BUTTON_COLOR");
        sendSetAllLEDsWhenOffCommand.put("async", false);
        return sendSetAllLEDsWhenOffCommand;
    }

    public static Map<String, Object> getSetAllLEDsCommand(String colorCode) {
        Map<String, Object> sendSetAllLEDsCommand = new HashMap<>();
        sendSetAllLEDsCommand.put("tParams", (new HashMap<String, Object>() {{
            put("CURRENT_COLOR", colorCode);
        }}));
        sendSetAllLEDsCommand.put("command", "KEYPAD_ALL_BUTTON_COLOR");
        sendSetAllLEDsCommand.put("async", false);
        return sendSetAllLEDsCommand;
    }

    public static Map<String, Object> getSendButtonActionCommand(Integer buttonAction, Integer buttonId) {
        Map<String, Object> sendButtonActionCommand = new HashMap<>();
        sendButtonActionCommand.put("tParams", (new HashMap<String, Object>() {{
            put("BUTTON_ID", buttonId);
            put("ACTION", buttonAction);
        }}));
        sendButtonActionCommand.put("command", "KEYPAD_BUTTON_ACTION");
        sendButtonActionCommand.put("async", false);
        return sendButtonActionCommand;
    }
}
