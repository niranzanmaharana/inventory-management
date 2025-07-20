package com.niranzan.inventory.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_transaction_record")
public class SalesTransactionRecord extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "sale_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_SALE_PARENT")
    )
    private SalesTransaction salesTransaction;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_SALES_TX_PRODUCT_PARENT")
    )
    private ProductItem productItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double pricePerUnit;

    @Column(nullable = false)
    private Double subtotal;

    public SalesTransaction getSalesTransaction() {
        return salesTransaction;
    }

    public void setSalesTransaction(SalesTransaction salesTransaction) {
        this.salesTransaction = salesTransaction;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
