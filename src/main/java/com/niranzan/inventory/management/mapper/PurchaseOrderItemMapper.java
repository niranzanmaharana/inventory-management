package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.PurchaseItemDto;
import com.niranzan.inventory.management.entity.PurchaseOrderItem;
import com.niranzan.inventory.management.view.response.PurchaseOrderItemResponse;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderItemMapper {
    public PurchaseItemDto toDto(PurchaseOrderItem item) {
        return PurchaseItemDto.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .expiryDate(item.getExpiryDate())
                .pricePerUnit(item.getPricePerUnit())
                .productId(item.getProductItem().getId())
                .build();
    }

    public PurchaseOrderItemResponse toResponse(PurchaseOrderItem purchaseOrderItem) {
        return PurchaseOrderItemResponse.builder()
                .id(purchaseOrderItem.getId())
                .productName(purchaseOrderItem.getProductItem().getProductName())
                .batchCode(purchaseOrderItem.getBatchCode())
                .quantity(purchaseOrderItem.getQuantity())
                .pricePerUnit(purchaseOrderItem.getPricePerUnit())
                .subTotal(purchaseOrderItem.getSubTotal())
                .expiryDate(purchaseOrderItem.getExpiryDate())
                .build();
    }
}
