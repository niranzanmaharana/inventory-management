package com.niranzan.inventory.management.entity;

import com.niranzan.inventory.management.enums.AppConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_item")
public class PurchaseOrderItem extends BaseEntity {
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

    @Column(name = "batch_code", length = 50)
    private String batchCode;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(nullable = false)
    private LocalDate expiryDate = LocalDate.parse(AppConstants.DEFAULT_EXPIRY_DATE.getValue());

    @Column(name = "received", nullable = false)
    private Boolean received = false;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountPercent = BigDecimal.valueOf(0.0);

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxPercent = BigDecimal.valueOf(0.0);
}
