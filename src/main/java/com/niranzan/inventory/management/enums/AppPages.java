package com.niranzan.inventory.management.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppPages {
    REDIRECT_URL("redirect:/"),
    REGISTRATION_PAGE("register"),
    // user pages
    USER_FORM("user/user-form"),
    USER_LIST("user/user-list"),
    USER_HOME("user/home"),
    // profile pages
    PROFILE_VIEW_PAGE("profile/view-profile"),
    PROFILE_CHANGE_PASSWORD_PAGE("profile/change-password"),
    // product pages
    PRODUCT_CATEGORY_LIST_PAGE("product-category/category-list"),
    PRODUCT_CATEGORY_FORM_PAGE("product-category/category-form");

    private final String pageName;
}
