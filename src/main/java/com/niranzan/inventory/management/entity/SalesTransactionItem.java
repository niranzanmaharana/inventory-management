package com.niranzan.inventory.management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_transaction_item")
@Getter
@Setter
public class SalesTransactionItem extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "sales_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_SALE_TX_ITEM_X_SALES_TX")
    )
    private SalesTransaction salesTransaction;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_SALES_TX_X_PRODUCT")
    )
    private ProductItem productItem;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "inventory_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_SALES_TX_X_INVENTORY")
    )
    private Inventory inventory;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal pricePerUnit;

    @Column(nullable = false)
    private BigDecimal subtotal;

    private BigDecimal discountPercent;

    private BigDecimal discountAmount;

    private BigDecimal taxPercent;

    private BigDecimal taxAmount;
}
