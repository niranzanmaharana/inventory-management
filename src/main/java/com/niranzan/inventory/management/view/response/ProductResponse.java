package com.niranzan.inventory.management.view.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String productName;
    private String description;
    private String invoiceImagePath;
    private String productImagePath;
    private String categoryName;
    private String subCategoryName;
    private boolean enabled;
}
