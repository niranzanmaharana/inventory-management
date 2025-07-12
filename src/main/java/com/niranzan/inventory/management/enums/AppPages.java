package com.niranzan.inventory.management.enums;

public enum AppPages {
    REDIRECT_URL("redirect:/"),
    REGISTRATION_PAGE("register"),
    // user pages
    USER_FORM("user/user-form"),
    USER_LIST("user/users"),
    USER_HOME("user/home"),
    // profile pages
    PROFILE_VIEW_PAGE("profile/view-profile"),
    PROFILE_CHANGE_PASSWORD_PAGE("profile/change-password");

    private final String pageName;

    AppPages(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }
}
