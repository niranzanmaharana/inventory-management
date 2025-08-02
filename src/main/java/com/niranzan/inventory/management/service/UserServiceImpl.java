package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.ProfileDto;
import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;
import com.niranzan.inventory.management.entity.UserRole;
import com.niranzan.inventory.management.exceptions.PasswordMismatchException;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.UserMapper;
import com.niranzan.inventory.management.repository.RoleRepository;
import com.niranzan.inventory.management.repository.UserRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.niranzan.inventory.management.config.AppMessage.MSG_PROVIDE_A_VALID_PASSWORD;
import static com.niranzan.inventory.management.config.AppMessage.MSG_USER_NOT_FOUND_WITH_ID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserProfile saveUser(UserProfileDto userProfileDto) {
        UserProfile userProfile = userMapper.toEntity(userProfileDto);
        userProfile.setPassword(passwordEncoder.encode(userProfileDto.getPassword()));
        UserRole userRole = roleRepository.findByRoleName(userProfileDto.getUserRole().getRoleName());
        if (userRole == null) {
            userRole = addNewRole(userProfileDto.getUserRole().getRoleName());
        }
        userProfile.setUserRole(userRole);
        return userRepository.save(userProfile);
    }

    @Override
    public UserProfile findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserProfile findUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public UserProfile findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserProfileDto> findAllUsers() {
        List<UserProfile> userProfiles = userRepository.findAll();
        return userProfiles.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserProfile updateUser(UserProfileDto userProfileDto) {
        UserProfile userProfile = userRepository.findById(userProfileDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_USER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(userProfileDto.getId()))));

        updateUser(userProfileDto, userProfile);

        return userRepository.save(userProfile);
    }

    @Override
    public UserProfile updateProfile(ProfileDto profileDto) {
        UserProfile userProfile = userRepository.findById(profileDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_USER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(profileDto.getId()))));

        updateProfile(profileDto, userProfile);

        return userRepository.save(userProfile);
    }

    @Override
    public UserProfile findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_USER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));
    }

    @Override
    public UserProfile toggleUserStatus(long id) {
        UserProfile userProfile = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_USER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));

        userProfile.setEnabled(!userProfile.isEnabled());
        return userRepository.save(userProfile);
    }

    @Override
    public void changePassword(long id, String currentPassword, String newPassword) {
        UserProfile userProfile = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_USER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));

        if (!passwordEncoder.matches(currentPassword, userProfile.getPassword())) {
            throw new PasswordMismatchException(MSG_PROVIDE_A_VALID_PASSWORD.getMessage());
        }

        userProfile.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userProfile);
    }

    private void updateUser(UserProfileDto userProfileDto, UserProfile userProfile) {
        userProfile.setSalutation(userProfileDto.getSalutation());
        userProfile.setFirstName(userProfileDto.getFirstName());
        userProfile.setLastName(userProfileDto.getLastName());
        userProfile.setGender(userProfileDto.getGender());
        userProfile.setMobile(userProfileDto.getMobile());
        userProfile.setEmail(userProfileDto.getEmail());
        userProfile.setDob(userProfileDto.getDob());
        UserRole userRole = roleRepository.findByRoleName(userProfileDto.getUserRole().getRoleName());
        if (userRole == null) {
            userRole = addNewRole(userProfileDto.getUserRole().getRoleName());
        }
        userProfile.setUserRole(userRole);
    }

    private void updateProfile(ProfileDto profileDto, UserProfile userProfile) {
        userProfile.setSalutation(profileDto.getSalutation());
        userProfile.setFirstName(profileDto.getFirstName());
        userProfile.setLastName(profileDto.getLastName());
        userProfile.setGender(profileDto.getGender());
        userProfile.setMobile(profileDto.getMobile());
        userProfile.setEmail(profileDto.getEmail());
        userProfile.setDob(profileDto.getDob());
    }

    private UserRole addNewRole(String roleName) {
        UserRole userRole = new UserRole();
        userRole.setRoleName(roleName);
        return roleRepository.save(userRole);
    }
}
