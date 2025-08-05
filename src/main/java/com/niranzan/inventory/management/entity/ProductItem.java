package com.niranzan.inventory.management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = "product_name", name = "UK_PRODUCT_NAME"),
        @UniqueConstraint(columnNames = "product_code", name = "UK_PRODUCT_CODE")
})
public class ProductItem extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String productName;
    @Column(nullable = false, unique = true, length = 100)
    private String productCode;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(length = 300)
    private String invoiceImagePath;
    @Column(length = 300)
    private String productImagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PRODUCT_X_CATEGORY")
    )
    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductAttribute> attributes = new ArrayList<>();

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInvoiceImagePath() {
        return invoiceImagePath;
    }

    public void setInvoiceImagePath(String invoiceImagePath) {
        this.invoiceImagePath = invoiceImagePath;
    }

    public String getProductImagePath() {
        return productImagePath;
    }

    public void setProductImagePath(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public List<ProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttribute> attributes) {
        this.attributes = attributes;
    }
}
