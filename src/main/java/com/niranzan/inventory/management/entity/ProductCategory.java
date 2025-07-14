package com.niranzan.inventory.management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = "categoryName", name = "UK_ProductCategory_CategoryName")
})
public class ProductCategory extends BaseEntity {
    @NotEmpty(message = "Category name must not be empty")
    @Size(min = 3, max = 50, message = "Category name should be between 3 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String categoryName;
    @NotEmpty(message = "Description must not be empty")
    @Size(min = 3, max = 200, message = "Description should be between 3-200 chars")
    @Column(nullable = false, length = 200)
    private String description;

    // Parent category (null if it's a top-level category)
    @ManyToOne
    @JoinColumn(
            name = "parent_id",
            foreignKey = @ForeignKey(name = "FK_PRODUCT_CATEGORY_SELF_PARENT")
    )
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ProductCategory> subCategories = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ProductCategory getParent() {
        return parent;
    }

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

    public List<ProductCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<ProductCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
