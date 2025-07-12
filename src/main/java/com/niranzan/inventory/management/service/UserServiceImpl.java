package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.Role;
import com.niranzan.inventory.management.entity.User;
import com.niranzan.inventory.management.exceptions.PasswordMismatchException;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.repository.RoleRepository;
import com.niranzan.inventory.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User saveUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByRoleName(userDto.getRole().getRoleName());
        if (role == null) {
            role = addNewRole(userDto.getRole().getRoleName());
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public User updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userDto.getId()));

        updateUser(userDto, user);

        return userRepository.save(user);
    }

    @Override
    public User updateProfile(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userDto.getId()));

        updateProfile(userDto, user);

        return userRepository.save(user);
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public User toggleUserStatus(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    @Override
    public void changePassword(long id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordMismatchException("Please provide the correct current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void updateUser(UserDto userDto, User user) {
        user.setSalutation(userDto.getSalutation());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setGender(userDto.getGender());
        user.setMobile(userDto.getMobile());
        user.setEmail(userDto.getEmail());
        user.setDob(userDto.getDob());
        Role role = roleRepository.findByRoleName(userDto.getRole().getRoleName());
        if (role == null) {
            role = addNewRole(userDto.getRole().getRoleName());
        }
        user.setRole(role);
    }

    private void updateProfile(UserDto userDto, User user) {
        user.setSalutation(userDto.getSalutation());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setGender(userDto.getGender());
        user.setMobile(userDto.getMobile());
        user.setEmail(userDto.getEmail());
        user.setDob(userDto.getDob());
    }

    private Role addNewRole(String roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        return roleRepository.save(role);
    }
}
