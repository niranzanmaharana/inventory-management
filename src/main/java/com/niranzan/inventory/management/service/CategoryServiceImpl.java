package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.AttributeTypeDto;
import com.niranzan.inventory.management.dto.CategoryDto;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.ProductCategoryMapper;
import com.niranzan.inventory.management.repository.CategoryRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    public static final String MSG_CATEGORY_NOT_FOUND_WITH_ID = "Category not found with id: {}";
    public static final String MSG_PARENT_CATEGORY_NOT_FOUND_WITH_ID = "Parent category not found with id: {}";
    private final CategoryRepository categoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(productCategoryMapper::toDto)
                .toList();
    }

    public List<CategoryDto> getCategoryTree() {
        List<ProductCategory> allCategories = categoryRepository.findAll();
        Map<Long, CategoryDto> dtoMap = new HashMap<>();

        // Convert all to DTOs and map by ID
        for (ProductCategory cat : allCategories) {
            CategoryDto dto = productCategoryMapper.toDto(cat);
            dto.setSubCategories(new ArrayList<>());
            dtoMap.put(dto.getId(), dto);
        }

        List<CategoryDto> rootCategories = new ArrayList<>();

        // Build the tree structure
        for (CategoryDto dto : dtoMap.values()) {
            if (dto.getParentId() != null) {
                CategoryDto parent = dtoMap.get(dto.getParentId());
                if (parent != null) {
                    parent.getSubCategories().add(dto);
                }
            } else {
                rootCategories.add(dto);
            }
        }

        return rootCategories;
    }

    @Override
    public List<CategoryDto> findAllExcept(List<Long> categoryIds) {
        return categoryRepository.findAllByIdNotIn(categoryIds).stream()
                .map(productCategoryMapper::toDto)
                .toList();
    }

    @Override
    public ProductCategory getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID, id.toString())));
    }

    @Override
    public ProductCategory save(ProductCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    @Override
    public ProductCategory update(Long id, CategoryDto categoryDto) {
        ProductCategory existing = getById(id);
        existing.setCategoryName(categoryDto.getCategoryName());
        existing.setDescription(categoryDto.getDescription());
        if (categoryDto.getParentId() != null) {
            ProductCategory parent = getById(categoryDto.getParentId());
            if (parent.isLeaf()) {
                parent.setLeaf(Boolean.FALSE);
            }
            existing.setParent(parent);
        } else {
            existing.setParent(null);
        }
        return categoryRepository.save(existing);
    }

    @Transactional
    @Override
    public ProductCategory saveCategory(CategoryDto categoryDto) {
        ProductCategory category = new ProductCategory();

        category.setId(categoryDto.getId());
        category.setCategoryName(categoryDto.getCategoryName());
        category.setDescription(categoryDto.getDescription());
        category.setLeaf(Boolean.TRUE);

        // Set parent category if provided
        if (categoryDto.getParentId() != null) {
            ProductCategory parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PARENT_CATEGORY_NOT_FOUND_WITH_ID, categoryDto.getParentId().toString())));
            if (parent.isLeaf()) {
                parent.setLeaf(Boolean.FALSE);
            }
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    @Override
    public CategoryDto toggleStatus(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setEnabled(!category.isEnabled());
                    ProductCategory savedCategory = categoryRepository.save(category);
                    return productCategoryMapper.toDto(savedCategory);
                }).orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_CATEGORY_NOT_FOUND_WITH_ID, id.toString())));
    }

    @Override
    public List<CategoryDto> findSubCategories(Long categoryId) {
        return categoryRepository.findByParent(categoryId).stream()
                .map(productCategoryMapper::toDto)
                .toList();
    }

    @Override
    public List<AttributeTypeDto> getAttributesForSubCategory(Long subCategoryId) {
        return null;
    }
}
