package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.CustomUserDetail;
import com.niranzan.inventory.management.entity.UserProfile;
import com.niranzan.inventory.management.entity.UserRole;
import com.niranzan.inventory.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfile userProfile = userRepository.findByUsername(username);

        if (userProfile != null) {
            Collection<? extends GrantedAuthority> authorities = mapRolesToAuthorities(Collections.singleton(userProfile.getUserRole()));
            return new CustomUserDetail(userProfile, authorities);
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<UserRole> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .toList();
    }
}
