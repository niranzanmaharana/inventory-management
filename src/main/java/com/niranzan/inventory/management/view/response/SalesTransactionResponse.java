package com.niranzan.inventory.management.view.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesTransactionResponse {
    private Long id;
    private CustomerResponse customer;
    private LocalDateTime transactionDate;
    private String formattedTxDate;
    private String paymentMethod;
    private String invoiceNumber;
    private String orderNumber;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;
    private List<SalesTransactionItemResponse> items = new ArrayList<>();
}
