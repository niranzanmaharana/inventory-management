package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.ProductCategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryDto> getAllActiveCategories();

    ProductCategory saveCategory(ProductCategoryDto category);

    ProductCategory updateCategory(Long id, ProductCategory updatedCategory);

    ProductCategory disableCategory(Long id);

    List<ProductCategory> getAllTopLevelCategories();

    List<ProductCategoryDto> getAllTopCategoriesWithSubCategories();

    ProductCategory getById(Long id);

    List<ProductCategory> getAllParentExcept(Long id);

    ProductCategory save(ProductCategory category);

    ProductCategory update(Long id, ProductCategoryDto category);

    ProductCategory enableCategory(Long id);
}
