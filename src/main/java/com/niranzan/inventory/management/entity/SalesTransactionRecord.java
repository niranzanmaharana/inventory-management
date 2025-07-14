package com.niranzan.inventory.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

    public SalesTransaction getSale() {
        return salesTransaction;
    }

    public void setSale(SalesTransaction salesTransaction) {
        this.salesTransaction = salesTransaction;
    }

    public ProductItem getProduct() {
        return productItem;
    }

    public void setProduct(ProductItem productItem) {
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
