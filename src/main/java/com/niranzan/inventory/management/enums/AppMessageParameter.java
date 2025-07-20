package com.niranzan.inventory.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppMessageParameter {
    ERROR_PARAM_NM("error"),
    SUCCESS_PARAM_NM("success");

    private final String name;
}
