package com.niranzan.inventory.management.view.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    private Long id;
    private String orderNumber;
    private String supplierName;
    private LocalDate purchaseDate;
    private String status;
    private List<PurchaseOrderItemResponse> items;
}
