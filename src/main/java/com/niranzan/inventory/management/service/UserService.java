package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto, String roleName);

    User findUserByEmail(String email);

    User findUserByMobile(String mobile);

    User findUserByUsername(String username);

    List<UserDto> findAllUsers();
}
