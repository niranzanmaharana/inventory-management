package com.niranzan.inventory.management.enums;

public enum AppRole {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MANAGER("ROLE_MANAGER"),
    ROLE_STAFF("ROLE_STAFF");

    private final String roleName;

    AppRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
