package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.ProfileDto;
import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserRoleMapper.class)
public interface UserMapper {
    UserProfile toEntity(UserProfileDto dto);

    UserProfileDto toDto(UserProfile entity);

    @Mapping(source = "userRole.roleName", target = "roleName")
    ProfileDto toProfile(UserProfile entity);
}
