package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.UserProfileDto;
import com.niranzan.inventory.management.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserRoleMapper.class)
public interface UserMapper {

    @Mapping(source = "userRole", target = "userRole")
    UserProfile toEntity(UserProfileDto dto);

    @Mapping(source = "userRole", target = "userRole")
    UserProfileDto toDto(UserProfile entity);

    List<UserProfileDto> toDtoList(List<UserProfile> users);
}
