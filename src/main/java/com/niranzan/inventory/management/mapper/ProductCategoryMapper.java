package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.CategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    @Mapping(source = "parent.id", target = "parentId")
    CategoryDto toDto(ProductCategory productCategory);

    ProductCategory toEntity(CategoryDto dto);
}
