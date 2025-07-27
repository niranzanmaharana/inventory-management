package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class CategoryDto {
    private Long id;
    @NotEmpty(message = "Category name must not be empty")
    @Size(min = 3, max = 50, message = "Category name should be between 3 and 50 characters")
    private String categoryName;
    @NotEmpty(message = "Description must not be empty")
    @Size(min = 3, max = 200, message = "Description should be between 3-200 chars")
    private String description;
    private boolean enabled;
    private Long parentId;
    private boolean leaf;
    private Set<CategoryDto> subCategories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Set<CategoryDto> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<CategoryDto> subCategories) {
        this.subCategories = subCategories;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
}
