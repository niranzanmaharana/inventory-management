package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);

    User findUserByEmail(String email);

    User findUserByMobile(String mobile);

    User findUserByUsername(String username);

    List<UserDto> findAllUsers();

    User updateUser(UserDto userDto);

    User updateProfile(UserDto userDto);

    User findById(long id);

    User toggleUserStatus(long id);

    void changePassword(long currentUserId, String currentPassword, String newPassword);
}
