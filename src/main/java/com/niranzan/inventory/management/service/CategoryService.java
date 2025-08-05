package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.dto.CategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();

    ProductCategory update(Long id, CategoryDto categoryDto);

    ProductCategory saveCategory(CategoryDto category);

    List<CategoryDto> findAllExcept(List<Long> categoryIds);

    ProductCategory getById(Long id);

    ProductCategory save(ProductCategory category);

    CategoryDto toggleStatus(Long id);

    List<CategoryDto> findSubCategories(Long categoryId);

    List<AttributeTypeDto> getAttributesForSubCategory(Long subCategoryId);

    List<CategoryDto> getCategoryTree();
}
