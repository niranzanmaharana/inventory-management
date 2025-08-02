package com.niranzan.inventory.management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageFileType {
    RECEIPT_PHOTO("RECEIPT_PHOTO"),
    PRODUCT_PHOTO("PRODUCT_PHOTO");

    private final String fileType;
}
