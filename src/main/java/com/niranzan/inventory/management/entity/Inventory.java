package com.niranzan.inventory.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {
    @OneToOne
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_INVENTORY_PRODUCT_PARENT")
    )
    private ProductItem productItem;

    private Integer quantity;

    @Column(length = 100)
    private String location;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
