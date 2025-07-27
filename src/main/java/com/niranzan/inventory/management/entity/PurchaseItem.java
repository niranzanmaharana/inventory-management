package com.niranzan.inventory.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_item")
public class PurchaseItem extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "purchase_order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_ITEM_X_PURCHASE_ORDER")
    )
    private PurchaseOrder purchaseOrder;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PURCHASE_ORDER_X_PRODUCT")
    )
    private ProductItem productItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(nullable = false)
    private LocalDate expiryDate;
}
