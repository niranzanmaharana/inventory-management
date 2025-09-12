package com.niranzan.inventory.management.view.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesTransactionItemResponse {
    private String productName;
    private String productCode;
    private String categoryName;
    private String batchCode;
    private Integer quantity;
    private BigDecimal amount;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal taxPercent;
    private BigDecimal taxAmount;
    private BigDecimal subtotal;
}
