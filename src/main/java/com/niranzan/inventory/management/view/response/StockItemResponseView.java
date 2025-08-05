package com.niranzan.inventory.management.view.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItemResponseView {
    private Long id;
    private String productName;
    private String batchCode;
    private String orderNumber;
    private LocalDateTime receivedDate;
    private Integer receivedQuantity;
    private Integer availableQuantity;
    private BigDecimal pricePerUnit;
    private LocalDate expiryDate;
}
