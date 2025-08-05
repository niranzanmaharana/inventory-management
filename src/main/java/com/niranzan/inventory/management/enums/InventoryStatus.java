package com.niranzan.inventory.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryStatus {
    RECEIVED("RECEIVED"),
    PARTIAL("PARTIAL"),
    CONSUMED("CONSUMED");

    private final String status;
}
