package com.niranzan.inventory.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplier")
public class Supplier extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String supplierName;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, unique = true, length = 10)
    private String mobile;
    @Column(nullable = false, unique = true, length = 200)
    private String address;
    @Column(unique = true, length = 200)
    private String website;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private Set<PurchaseOrder> purchaseOrders = new HashSet<>();
}
