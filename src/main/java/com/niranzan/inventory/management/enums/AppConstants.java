package com.niranzan.inventory.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppConstants {
    DEFAULT_EXPIRY_DATE("2099-12-31");

    private final String value;
}
