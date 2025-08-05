package com.niranzan.inventory.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;

    private String orderNumber;

    @NotNull(message = "Supplier is required")
    private Long supplierId;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;

    @Valid
    @Size(min = 1, message = "At least one item must be added")
    private List<PurchaseItemDto> items = new ArrayList<>();

    private String status;
}