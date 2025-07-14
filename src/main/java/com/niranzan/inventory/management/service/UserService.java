package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;

import java.util.List;

public interface UserService {
    UserProfile saveUser(UserProfileDto userProfileDto);

    UserProfile findUserByEmail(String email);

    UserProfile findUserByMobile(String mobile);

    UserProfile findUserByUsername(String username);

    List<UserProfileDto> findAllUsers();

    UserProfile updateUser(UserProfileDto userProfileDto);

    UserProfile updateProfile(UserProfileDto userProfileDto);

    UserProfile findById(long id);

    UserProfile toggleUserStatus(long id);

    void changePassword(long currentUserId, String currentPassword, String newPassword);
}
