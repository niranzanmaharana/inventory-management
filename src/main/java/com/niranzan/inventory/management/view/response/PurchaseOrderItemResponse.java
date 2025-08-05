package com.niranzan.inventory.management.view.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponse {
    private Long id;
    private String productName;
    private String batchCode;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal subTotal;
    private LocalDate expiryDate;
    private String status;
}
