package com.niranzan.inventory.management.entity;

import com.niranzan.inventory.management.enums.AppConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = "batch_code", name = "UK_BATCH_CODE")
})
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

    @Column(name = "batch_code", unique = true, length = 50)
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
}
