package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.ProductCategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.ProductCategoryMapper;
import com.niranzan.inventory.management.repository.ProductCategoryRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductCategoryCategoryServiceImpl implements ProductCategoryService {
    public static final String MSG_CATEGORY_NOT_FOUND_WITH_ID = "Category not found with id: {}";
    public static final String MSG_PARENT_CATEGORY_NOT_FOUND_WITH_ID = "Parent category not found with id: {}";
    private final ProductCategoryRepository categoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryCategoryServiceImpl(ProductCategoryRepository categoryRepository, ProductCategoryMapper productCategoryMapper) {
        this.categoryRepository = categoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    @Override
    public List<ProductCategoryDto> getAllActiveCategories() {
        return categoryRepository.findAll().stream().map(productCategoryMapper::toDto).toList();
    }

    @Override
    public List<ProductCategory> getAllTopLevelCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<ProductCategoryDto> getAllTopCategoriesWithSubCategories() {
        List<ProductCategory> topCategories = categoryRepository.findByParentIsNull();
        return topCategories.stream()
                .map(productCategoryMapper::toDto)
                .toList();
    }

    @Override
    public ProductCategory getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID, id.toString())));
    }

    @Override
    public List<ProductCategory> getAllParentExcept(Long excludeId) {
        return categoryRepository.findAll().stream().filter(productCategory -> Objects.isNull(productCategory.getParent())).toList();
    }

    @Override
    public ProductCategory save(ProductCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public ProductCategory update(Long id, ProductCategoryDto categoryDto) {
        ProductCategory existing = getById(id);
        existing.setCategoryName(categoryDto.getCategoryName());
        existing.setDescription(categoryDto.getDescription());
        if (categoryDto.getParentId() != null) {
            ProductCategory parent = getById(categoryDto.getParentId());
            existing.setParent(parent);
        } else {
            existing.setParent(null);
        }
        return categoryRepository.save(existing);
    }

    @Override
    public ProductCategory saveCategory(ProductCategoryDto categoryDto) {
        ProductCategory category = new ProductCategory();
        category.setId(categoryDto.getId());
        category.setCategoryName(categoryDto.getCategoryName());
        category.setDescription(categoryDto.getDescription());

        // Set parent category if provided
        if (categoryDto.getParentId() != null) {
            ProductCategory parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PARENT_CATEGORY_NOT_FOUND_WITH_ID, categoryDto.getParentId().toString())));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public ProductCategory updateCategory(Long id, ProductCategory updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setCategoryName(updatedCategory.getCategoryName());
                    category.setDescription(updatedCategory.getDescription());
                    return categoryRepository.save(category);
                }).orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID, id.toString())));
    }

    @Override
    public ProductCategory disableCategory(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setEnabled(Boolean.FALSE);
                    return categoryRepository.save(category);
                }).orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID, id.toString())));
    }

    @Override
    public ProductCategory enableCategory(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setEnabled(Boolean.TRUE);
                    return categoryRepository.save(category);
                }).orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID, id.toString())));
    }
}
