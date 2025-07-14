package com.niranzan.inventory.management.enums;

public enum AppMessageParameter {
    ERROR_PARAM_NM("error"),
    SUCCESS_PARAM_NM("success");

    private final String name;

    AppMessageParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
