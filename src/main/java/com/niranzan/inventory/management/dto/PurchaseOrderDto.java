package com.niranzan.inventory.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;

    @NotEmpty(message = "Supplier is required")
    private Long supplierId;

    @NotEmpty(message = "Purchase date is required")
    private LocalDateTime purchaseDate;

    @Valid
    @Size(min = 1, message = "At least one item must be added")
    private List<PurchaseItemDto> items = new ArrayList<>();
}