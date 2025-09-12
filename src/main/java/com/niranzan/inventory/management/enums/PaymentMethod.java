package com.niranzan.inventory.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CASH("CASH"),
    CARD("CARD"),
    UPI("UPI");

    private final String method;
}
