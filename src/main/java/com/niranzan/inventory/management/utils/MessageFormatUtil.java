package com.niranzan.inventory.management.utils;

public class MessageFormatUtil {
    private MessageFormatUtil() {
        // To restrict from instantiation
    }
    public static String format(String message, String... values) {
        for (String value : values) {
            message = message.replaceFirst("\\{\\}", value);
        }
        return message;
    }
}
