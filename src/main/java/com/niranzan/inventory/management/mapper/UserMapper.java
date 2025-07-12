package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {
    @Mapping(target = "role", source = "role")
    UserDto toDto(User user);

    @Mapping(target = "role", source = "role")
    User toEntity(UserDto dto);
}
