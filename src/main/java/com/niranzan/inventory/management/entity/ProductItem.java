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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = "productName", name = "UK_ProductItem_ProductName")
})
public class ProductItem extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String productName;
    @Column(nullable = false, length = 200)
    private String description;
    @Column(nullable = false)
    private BigDecimal pricePerUnit;
    @Column(nullable = false)
    private Integer quantity;
    private LocalDate expiryDate;
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
    private Set<ProductAttribute> attributes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "supplier_id",
            foreignKey = @ForeignKey(name = "FK_PRODUCT_X_SUPPLIER")
    )
    private Supplier supplier;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Set<ProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<ProductAttribute> attributes) {
        this.attributes = attributes;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
