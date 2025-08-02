package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ProductDto {
    private Long id;
    @NotEmpty(message = "Description is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String productName;

    @NotEmpty(message = "Description is required")
    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal pricePerUnit;

    @Future(message = "Expiry date must not be in the past")
    @NotNull(message = "Expiry date should not be empty")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiryDate;
    private String invoiceImagePath;
    private String productImagePath;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Supplier is required")
    private Long supplierId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Map<Long, String> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Map<Long, String> productAttributes) {
        this.productAttributes = productAttributes;
    }
}
