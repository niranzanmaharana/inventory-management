package com.niranzan.inventory.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesTransactionDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private LocalDateTime transactionDate;
    private String paymentMethod;
    private BigDecimal totalDiscount;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;
    private List<SalesTransactionItemDto> items = new ArrayList<>();
}
