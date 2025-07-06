package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.UserDto;
import com.niranzan.inventory.management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserDto dto);

    UserDto toDto(User user);
}
