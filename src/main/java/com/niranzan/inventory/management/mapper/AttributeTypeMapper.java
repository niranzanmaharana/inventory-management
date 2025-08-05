package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.entity.AttributeType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttributeTypeMapper {
    AttributeTypeDto toDto(AttributeType attributeType);
}
