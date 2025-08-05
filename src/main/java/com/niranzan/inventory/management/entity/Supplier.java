package com.niranzan.inventory.management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplier", uniqueConstraints = {
        @UniqueConstraint(columnNames = "supplier_name", name = "UK_SUPPLIER_NAME"),
        @UniqueConstraint(columnNames = "email", name = "UK_EMAIL"),
        @UniqueConstraint(columnNames = "mobile", name = "UK_MOBILE"),
        @UniqueConstraint(columnNames = "website", name = "UK_WEBSITE")
})
public class Supplier extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String supplierName;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, unique = true, length = 10)
    private String mobile;
    @Column(nullable = false, length = 200)
    private String address;
    @Column(unique = true, length = 200)
    private String website;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
}
