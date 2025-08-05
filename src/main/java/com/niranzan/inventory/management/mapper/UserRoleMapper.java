package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.UserRoleDto;
import com.niranzan.inventory.management.entity.UserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    UserRoleDto toDto(UserRole entity);
}
