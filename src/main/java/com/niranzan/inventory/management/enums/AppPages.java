package com.niranzan.inventory.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppPages {
    REDIRECT_URL("redirect:/"),
    REGISTRATION_PATH("register"),
    // user pages
    USER_FORM_PATH("user/user-form"),
    USER_LIST_PATH("user/user-list"),
    USER_HOME_PATH("user/home"),
    // profile pages
    PROFILE_VIEW_PATH("profile/view-profile"),
    PROFILE_CHANGE_PASSWORD_PATH("profile/change-password"),
    // attribute pages
    ATTRIBUTE_TYPE_LIST_PATH("attribute-type/attribute-type-list"),
    ATTRIBUTE_TYPE_FORM_PATH("attribute-type/attribute-type-form"),
    // supplier pages
    SUPPLIER_LIST_PATH("supplier/supplier-list"),
    SUPPLIER_FORM_PATH("supplier/supplier-form"),
    // product category pages
    PRODUCT_CATEGORY_LIST_PATH("product-category/category-list"),
    PRODUCT_CATEGORY_FORM_PATH("product-category/category-form"),
    // product pages
    PRODUCT_FORM_PATH("product/product-form"),
    PRODUCT_LIST_PATH("product/product-list"),
    // purchase order pages
    PURCHASE_ORDER_FORM_PATH("purchase-order/purchase-order-form"),
    PURCHASE_ORDER_LIST_PATH("purchase-order/purchase-order-list");

    private final String path;
}
