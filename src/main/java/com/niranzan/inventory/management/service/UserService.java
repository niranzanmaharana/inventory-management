package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
