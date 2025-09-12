package com.niranzan.inventory.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@AllArgsConstructor
@NoArgsConstructor
public class ProductBatchDto {
    private Long inventoryId;
    private Long productId;
    private String productName;
    private String productCode;
    private String categoryName;
    private String batchCode;
    private BigDecimal amount;
    private int receivedQuantity;
    private int availableQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
}
