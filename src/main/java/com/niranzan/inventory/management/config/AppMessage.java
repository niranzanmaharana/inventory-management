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
    MSG_PO_NOT_FOUND_WITH_ID("Purchase order not found with id: {}"),
    MSG_PO_ITEM_NOT_FOUND_WITH_ID("Purchase order item not found with id: {}"),
    MSG_CUSTOMER_NOT_FOUND_WITH_ID("Customer not found with id: {}"),
    MSG_INVENTORY_NOT_FOUND_WITH_ID("Inventory not found with id: {}"),
    MSG_INVOICE_NOT_FOUND_WITH_INVOICE_NUMBER("Invoice not found with invoice number: {}"),
    MSG_INVOICE_NOT_FOUND_WITH_ORDER_NUMBER("Invoice not found with order number: {}"),

    MSG_PROVIDE_A_VALID_PASSWORD("Please provide the correct current password"),
    MSG_CANNOT_UPDATE_PURCHASE_ORDER("This purchase order is locked for editing because its items are already in use or sold."),
    MSG_PO_IS_NOT_IN_DRAFT("Can not {} the purchase order as this is not in {} state"),

    MSG_INVALID_ATTRIBUTES_EXCEPTION("Invalid attributes exception: {}"),
    MSG_UNKNOWN_EXCEPTION("Unknown exception: {}");

    private final String message;
}
