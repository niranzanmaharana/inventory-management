package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.ProductCategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
//    @Mapping(source = "parent.id", target = "parentId")
    ProductCategoryDto toDto(ProductCategory productCategory);

//    @Mapping(target = "parent", ignore = true)
    ProductCategory toEntity(ProductCategoryDto dto);
}
