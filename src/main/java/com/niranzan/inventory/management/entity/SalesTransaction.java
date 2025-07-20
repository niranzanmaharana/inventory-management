package com.niranzan.inventory.management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sales_transaction")
public class SalesTransaction extends BaseEntity {
    private String customerName;

    private LocalDateTime saleDate;

    private Double totalAmount;

    @OneToMany(mappedBy = "salesTransaction", cascade = CascadeType.ALL)
    private Set<SalesTransactionRecord> items = new HashSet<>();

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<SalesTransactionRecord> getItems() {
        return items;
    }

    public void setItems(Set<SalesTransactionRecord> items) {
        this.items = items;
    }
}
