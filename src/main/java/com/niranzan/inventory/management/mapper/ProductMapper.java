package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.ProductDto;
import com.niranzan.inventory.management.entity.ProductItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(ProductItem productCategory);

    ProductItem toEntity(ProductDto dto);
}
