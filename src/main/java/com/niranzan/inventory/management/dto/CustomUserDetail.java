package com.niranzan.inventory.management.dto;

import com.niranzan.inventory.management.entity.UserProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetail implements UserDetails {
    private final UserProfile userProfile;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetail(UserProfile userProfile, Collection<? extends GrantedAuthority> authorities) {
        this.userProfile = userProfile;
        this.authorities = authorities;
    }

    public String getFirstName() {
        return userProfile.getFirstName();
    }

    public String getLastName() {
        return userProfile.getLastName();
    }

    public String getFullName() {
        return userProfile.getFirstName() + " " + userProfile.getLastName();
    }

    public String getEmail() {
        return userProfile.getEmail();
    }

    public Long getId() {
        return this.userProfile.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userProfile.getPassword();
    }

    @Override
    public String getUsername() {
        return userProfile.getUsername(); // Use username for login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
