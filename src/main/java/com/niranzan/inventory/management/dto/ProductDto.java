package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;

public class ProductDto {
    private Long id;
    @NotEmpty(message = "Product name is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String productName;

    private String productCode;

    @NotEmpty(message = "Description is required")
    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;
    private String invoiceImagePath;
    private String productImagePath;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Map<Long, String> productAttributes = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Map<Long, String> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Map<Long, String> productAttributes) {
        this.productAttributes = productAttributes;
    }
}
