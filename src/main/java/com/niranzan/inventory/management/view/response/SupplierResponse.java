package com.niranzan.inventory.management.view.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Long id;
    private String supplierName;
    private String email;
    private String mobile;
    private String address;
    private String website;
}
