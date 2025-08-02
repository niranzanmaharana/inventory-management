package com.niranzan.inventory.management.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppMessage {
    MSG_USER_NOT_FOUND_WITH_ID("UserProfile not found with id: {}"),
    MSG_PRODUCT_NOT_FOUND_WITH_ID("Product not found with id: {}"),
    MSG_SUPPLIER_NOT_FOUND_WITH_ID("Supplier not found with id: {}"),
    MSG_CATEGORY_NOT_FOUND_WITH_ID("Category not found with id: {}"),
    MSG_PROVIDE_A_VALID_PASSWORD("Please provide the correct current password"),

    MSG_INVALID_ATTRIBUTES_EXCEPTION("Invalid attributes exception: {}"),
    MSG_UNKNOWN_EXCEPTION("Unknown exception: {}");

    private final String message;
}
