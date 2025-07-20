package com.niranzan.inventory.management.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchase_order")
public class Supplier extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String supplierName;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, unique = true, length = 10)
    private String mobile;
    @Column(nullable = false, unique = true, length = 200)
    private String address;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private Set<PurchaseOrder> purchaseOrders = new HashSet<>();

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(Set<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }
}
