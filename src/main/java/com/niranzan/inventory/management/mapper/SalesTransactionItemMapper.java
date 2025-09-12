package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.entity.Inventory;
import com.niranzan.inventory.management.entity.ProductCategory;
import com.niranzan.inventory.management.entity.ProductItem;
import com.niranzan.inventory.management.entity.SalesTransactionItem;
import com.niranzan.inventory.management.view.response.SalesTransactionItemResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SalesTransactionItemMapper {
    public SalesTransactionItemResponse toResponse(SalesTransactionItem item) {
        SalesTransactionItemResponse response = new SalesTransactionItemResponse();
        ProductItem product = item.getProductItem();
        if (Objects.nonNull(product)) {
            response.setProductName(product.getProductName());
            response.setProductCode(product.getProductCode());
            ProductCategory category = product.getCategory();
            if (Objects.nonNull(category)) {
                response.setCategoryName(category.getCategoryName());
            }
        }

        Inventory inventory = item.getInventory();
        if (Objects.nonNull(inventory)) {
            response.setBatchCode(inventory.getBatchCode());
            response.setAmount(inventory.getPricePerUnit());
        }
        response.setQuantity(item.getQuantity());

        response.setDiscountPercent(item.getDiscountPercent());
        response.setDiscountAmount(item.getDiscountAmount());
        response.setTaxPercent(item.getTaxPercent());
        response.setTaxAmount(item.getTaxAmount());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}
