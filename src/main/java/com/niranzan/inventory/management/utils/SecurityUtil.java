package com.niranzan.inventory.management.utils;

import com.niranzan.inventory.management.dto.CustomUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private SecurityUtil() {
        // This constructor to enforce not to instantiate this util
    }
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetail userDetails) {
                return userDetails.getId();
            }
        }
        return null;
    }
}
