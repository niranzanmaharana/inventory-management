package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.entity.Inventory;
import com.niranzan.inventory.management.view.response.StockItemResponseView;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    public StockItemResponseView toResponse(Inventory stock) {
        return StockItemResponseView.builder()
                .id(stock.getId())
                .productName(stock.getProductItem().getProductName())
                .batchCode(stock.getBatchCode())
                .orderNumber(stock.getPurchaseOrderItem().getPurchaseOrder().getOrderNumber())
                .receivedDate(stock.getReceivedDate())
                .receivedQuantity(stock.getReceivedQuantity())
                .availableQuantity(stock.getAvailableQuantity())
                .pricePerUnit(stock.getPricePerUnit())
                .expiryDate(stock.getExpiryDate())
                .build();
    }
}
