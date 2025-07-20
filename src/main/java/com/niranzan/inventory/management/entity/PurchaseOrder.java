package com.niranzan.inventory.management.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String supplierName;
    @Column(nullable = false)
    private Double totalAmount;
    @Column(nullable = false, length = 300)
    private String billFileUrl;
    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private Set<PurchaseOrderItem> items = new HashSet<>();

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBillFileUrl() {
        return billFileUrl;
    }

    public void setBillFileUrl(String billFileUrl) {
        this.billFileUrl = billFileUrl;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Set<PurchaseOrderItem> getItems() {
        return items;
    }

    public void setItems(Set<PurchaseOrderItem> items) {
        this.items = items;
    }
}
