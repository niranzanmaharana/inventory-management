package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.RoleDto;
import com.niranzan.inventory.management.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);

    Role toEntity(RoleDto dto);
}
