package com.control4.integration.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marvindean on Mar, 2019.
 * Project lighting
 * Package com.control4.integration.utils
 */

//This class builds/returns the json objects to issue the commands to the lighting device.
public class FSC_Helper {

    public static Map<String, Object> getFSCPropertiesCommand() {
        Map<String, Object> sendFSCPropertiesCommand = new HashMap<>();
        sendFSCPropertiesCommand.put("command", "GET_PROPERTIES");
        sendFSCPropertiesCommand.put("async", false);
        return sendFSCPropertiesCommand;
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
        Map<String, Object> sendSetAutoOffCommand = new HashMap<>();
        sendSetAutoOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("TIME", interval);
        }}));
        sendSetAutoOffCommand.put("command", "SET_AUTO_OFF");
        sendSetAutoOffCommand.put("async", false);
        return sendSetAutoOffCommand;
    }

    public static Map<String, Object> getDisableAutoOffCommand() {
        Map<String, Object> sendSetAutoOffCommand = new HashMap<>();
        sendSetAutoOffCommand.put("tParams", (new HashMap<String, Object>() {{
            put("TIME", 0);
        }}));
        sendSetAutoOffCommand.put("command", "SET_AUTO_OFF");
        sendSetAutoOffCommand.put("async", false);
        return sendSetAutoOffCommand;
    }

    public static Map<String, Object> getRestartAutoOffCommand() {
        Map<String, Object> sendRestartAutoOffCommand = new HashMap<>();
        sendRestartAutoOffCommand.put("command", "RESTART_AUTO_OFF");
        sendRestartAutoOffCommand.put("async", false);
        return sendRestartAutoOffCommand;
    }

    public static Map<String, Object> getFanOnCommand() {
        Map<String, Object> sendFanOnCommand = new HashMap<>();
        sendFanOnCommand.put("command", "ON");
        sendFanOnCommand.put("async", false);
        return sendFanOnCommand;
    }

    public static Map<String, Object> getFanOffCommand() {
        Map<String, Object> sendFanOffCommand = new HashMap<>();
        sendFanOffCommand.put("command", "OFF");
        sendFanOffCommand.put("async", false);
        return sendFanOffCommand;
    }


    public static Map<String, Object> getFanToggleCommand() {
        Map<String, Object> sendFanToggleCommand = new HashMap<>();
        sendFanToggleCommand.put("command", "TOGGLE");
        sendFanToggleCommand.put("async", false);
        return sendFanToggleCommand;
    }

    public static Map<String, Object> getSpeedUpCommand() {
        Map<String, Object> sendSpeedUpCommand = new HashMap<>();
        sendSpeedUpCommand.put("command", "CYCLE_SPEED_UP");
        sendSpeedUpCommand.put("async", false);
        return sendSpeedUpCommand;
    }

    public static Map<String, Object> getSpeedDownCommand() {
        Map<String, Object> sendSpeedDownCommand = new HashMap<>();
        sendSpeedDownCommand.put("command", "CYCLE_SPEED_DOWN");
        sendSpeedDownCommand.put("async", false);
        return sendSpeedDownCommand;
    }

    public static Map<String, Object> getSetSpeedCommand(Integer speedVal) {
        Map<String, Object> sendSetSpeedCommand = new HashMap<>();
        sendSetSpeedCommand.put("tParams", (new HashMap<String, Object>() {{
            put("SPEED", speedVal);
        }}));
        sendSetSpeedCommand.put("command", "SET_SPEED");
        sendSetSpeedCommand.put("async", false);
        return sendSetSpeedCommand;
    }

    public static Map<String, Object> getPresetCommand(Integer speedVal) {
        Map<String, Object> sendPresetCommand = new HashMap<>();
        sendPresetCommand.put("tParams", (new HashMap<String, Object>() {{
            put("PRESET", speedVal);
        }}));
        sendPresetCommand.put("command", "DESIGNATE_PRESET");
        sendPresetCommand.put("async", false);
        return sendPresetCommand;
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

    public static Map<String, Object> getSetButtonColorCommand(String colorCode, Integer buttonId) {
        Map<String, Object> sendSetButtonColorCommand = new HashMap<>();
        sendSetButtonColorCommand.put("tParams", (new HashMap<String, Object>() {{
            put("CURRENT_COLOR", colorCode);
            put("BUTTON_ID", buttonId);
        }}));
        sendSetButtonColorCommand.put("command", "KEYPAD_BUTTON_COLOR");
        sendSetButtonColorCommand.put("async", false);
        return sendSetButtonColorCommand;
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
