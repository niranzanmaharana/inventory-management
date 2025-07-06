package com.niranzan.inventory.management.enums;

public enum AppErrorCode {
    DUPLICATE_ELEMENT("DUPLICATE_ELEMENT", "duplicate element");

    private final String errorCode;
    private final String errorCodeValue;

    AppErrorCode(String errorCode, String errorCodeValue) {
        this.errorCode = errorCode;
        this.errorCodeValue = errorCodeValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeValue() {
        return errorCodeValue;
    }
}
