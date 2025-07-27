package com.niranzan.inventory.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageFileType {
    RECEIPT_PHOTO("Bill Photo"),
    PRODUCT_PHOTO("Product Photo");

    private final String fileType;
}
