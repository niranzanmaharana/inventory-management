package com.niranzan.inventory.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseOrderStatus {
    DRAFT("DRAFT"),
    PLACED("PLACED"),
    RECEIVED("RECEIVED"),
    CANCELLED("CANCELLED");

    private final String status;
}
