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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_WITH_ID = "UserProfile not found with id: ";
    private final UserRepository UserRepository;
    private final RoleRepository RoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserProfile saveUser(UserProfileDto userProfileDto) {
        UserProfile userProfile = userMapper.toEntity(userProfileDto);
        userProfile.setPassword(passwordEncoder.encode(userProfileDto.getPassword()));
        UserRole userRole = RoleRepository.findByRoleName(userProfileDto.getUserRole().getRoleName());
        if (userRole == null) {
            userRole = addNewRole(userProfileDto.getUserRole().getRoleName());
        }
        userProfile.setUserRole(userRole);
        return UserRepository.save(userProfile);
    }

    @Override
    public UserProfile findUserByEmail(String email) {
        return UserRepository.findByEmail(email);
    }

    @Override
    public UserProfile findUserByMobile(String mobile) {
        return UserRepository.findByMobile(mobile);
    }

    @Override
    public UserProfile findUserByUsername(String username) {
        return UserRepository.findByUsername(username);
    }

    @Override
    public List<UserProfileDto> findAllUsers() {
        List<UserProfile> userProfiles = UserRepository.findAll();
        return userProfiles.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserProfile updateUser(UserProfileDto userProfileDto) {
        UserProfile userProfile = UserRepository.findById(userProfileDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + userProfileDto.getId()));

        updateUser(userProfileDto, userProfile);

        return UserRepository.save(userProfile);
    }

    @Override
    public UserProfile updateProfile(ProfileDto profileDto) {
        UserProfile userProfile = UserRepository.findById(profileDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + profileDto.getId()));

        updateProfile(profileDto, userProfile);

        return UserRepository.save(userProfile);
    }

    @Override
    public UserProfile findById(long id) {
        return UserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public UserProfile toggleUserStatus(long id) {
        UserProfile userProfile = UserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));

        userProfile.setEnabled(!userProfile.isEnabled());
        return UserRepository.save(userProfile);
    }

    @Override
    public void changePassword(long id, String currentPassword, String newPassword) {
        UserProfile userProfile = UserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found: " + id));

        if (!passwordEncoder.matches(currentPassword, userProfile.getPassword())) {
            throw new PasswordMismatchException("Please provide the correct current password");
        }

        userProfile.setPassword(passwordEncoder.encode(newPassword));
        UserRepository.save(userProfile);
    }

    private void updateUser(UserProfileDto userProfileDto, UserProfile userProfile) {
        userProfile.setSalutation(userProfileDto.getSalutation());
        userProfile.setFirstName(userProfileDto.getFirstName());
        userProfile.setLastName(userProfileDto.getLastName());
        userProfile.setGender(userProfileDto.getGender());
        userProfile.setMobile(userProfileDto.getMobile());
        userProfile.setEmail(userProfileDto.getEmail());
        userProfile.setDob(userProfileDto.getDob());
        UserRole userRole = RoleRepository.findByRoleName(userProfileDto.getUserRole().getRoleName());
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
        return RoleRepository.save(userRole);
    }
}
