package com.niranzan.inventory.management.enums;

public enum AppMessagePlaceholder {
    ERROR_MSG_PLACEHOLDER("error"),
    SUCCESS_MSG_PLACEHOLDER("success");

    private final String placeholderName;

    AppMessagePlaceholder(String placeholderName) {
        this.placeholderName = placeholderName;
    }

    public String getPlaceholderName() {
        return placeholderName;
    }
}
