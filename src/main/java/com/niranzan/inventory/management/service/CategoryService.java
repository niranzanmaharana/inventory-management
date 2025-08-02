package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.dto.CategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    Set<CategoryDto> findAll();

    ProductCategory update(Long id, CategoryDto categoryDto);

    ProductCategory saveCategory(CategoryDto category);

    Set<CategoryDto> findAllExcept(List<Long> categoryIds);

    List<ProductCategory> findParentCategories();

    List<CategoryDto> getAllTopCategoriesWithSubCategories();

    ProductCategory getById(Long id);

    List<ProductCategory> getAllParentExcept(Long id);

    ProductCategory save(ProductCategory category);

    CategoryDto toggleStatus(Long id);

    List<CategoryDto> findSubCategories(Long categoryId);

    List<AttributeTypeDto> getAttributesForSubCategory(Long subCategoryId);

    List<CategoryDto> getCategoryTree();
}
