package com.niranzan.inventory.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppErrorCode {
    DUPLICATE_ELEMENT("DUPLICATE_ELEMENT", "duplicate element");

    private final String errorCode;
    private final String errorCodeValue;
}
