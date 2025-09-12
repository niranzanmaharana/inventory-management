package com.niranzan.inventory.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesTransactionItemDto {
    private Long id;
    private Long productId;
    private Long inventoryId;
    private Integer quantity;
    private BigDecimal discountPercent;
    private BigDecimal taxPercent;
}
