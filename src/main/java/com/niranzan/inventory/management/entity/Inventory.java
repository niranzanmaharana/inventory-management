package com.niranzan.inventory.management.entity;

import com.niranzan.inventory.management.enums.InventoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"batch_code"}, name = "UK_BATCH_CODE")
})
@Getter
@Setter
public class Inventory extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_INVENTORY_X_PRODUCT")
    )
    private ProductItem productItem;
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "purchase_order_item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_INVENTORY_X_PURCHASE_ORDER_ITEM")
    )
    private PurchaseOrderItem purchaseOrderItem;

    @Column(length = 100)
    private String location;

    private LocalDateTime receivedDate;

    @Column(nullable = false, unique = true, length = 10)
    private String batchCode;

    private Integer receivedQuantity;

    private Integer availableQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryStatus status = InventoryStatus.RECEIVED;
}
