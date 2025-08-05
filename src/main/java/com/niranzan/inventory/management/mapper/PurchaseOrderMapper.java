package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.PurchaseOrderDto;
import com.niranzan.inventory.management.entity.PurchaseOrder;
import com.niranzan.inventory.management.view.response.PurchaseOrderItemResponse;
import com.niranzan.inventory.management.view.response.PurchaseOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PurchaseOrderMapper {
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

    public PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {
        PurchaseOrderResponse purchaseOrderResponse = PurchaseOrderResponse.builder()
                .id(purchaseOrder.getId())
                .orderNumber(purchaseOrder.getOrderNumber())
                .purchaseDate(purchaseOrder.getPurchaseDate())
                .supplierName(purchaseOrder.getSupplier().getSupplierName())
                .status(purchaseOrder.getStatus().getStatus())
                .items(new ArrayList<>())
                .build();
        List<PurchaseOrderItemResponse> itemResponses = purchaseOrder.getItems().stream()
                .map(purchaseOrderItemMapper::toResponse)
                .toList();
        purchaseOrderResponse.setItems(itemResponses);
        return purchaseOrderResponse;
    }

    public PurchaseOrderDto toDto(PurchaseOrder purchaseOrder) {
        PurchaseOrderDto purchaseOrderDto = new PurchaseOrderDto();
        purchaseOrderDto.setId(purchaseOrder.getId());
        purchaseOrderDto.setOrderNumber(purchaseOrder.getOrderNumber());
        purchaseOrderDto.setPurchaseDate(purchaseOrder.getPurchaseDate());
        purchaseOrderDto.setSupplierId(purchaseOrder.getSupplier().getId());
        purchaseOrderDto.setStatus(purchaseOrder.getStatus().getStatus());
        purchaseOrderDto.setItems(new ArrayList<>());
        return purchaseOrderDto;
    }
}
