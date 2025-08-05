package com.niranzan.inventory.management.entity;

import com.niranzan.inventory.management.enums.PurchaseOrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "purchase_order", uniqueConstraints = {
        @UniqueConstraint(columnNames = "order_number", name = "UK_ORDER_NUMBER")
})
public class PurchaseOrder extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_X_SUPPLIER")
    )
    private Supplier supplier;

    @Column(unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(nullable = false, length = 300, columnDefinition = "VARCHAR(300) DEFAULT 'PENDING'")
    private String billFileUrl = "PENDING";

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    @Column(name = "remarks")
    private String remarks;
}
