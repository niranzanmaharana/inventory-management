package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto {
    private Long id;
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal pricePerUnit;

    @NotNull(message = "Discount percent unit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount percent must be positive")
    private BigDecimal discountPercent = BigDecimal.valueOf(0.0);

    @NotNull(message = "Tax percent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tax percent must be positive")
    private BigDecimal taxPercent = BigDecimal.valueOf(0.0);

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiryDate;
}
