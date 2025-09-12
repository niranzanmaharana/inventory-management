package com.niranzan.inventory.management.view.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerResponse {
    private Long id;
    private String customerCode;
    private String customerName;
    private String address;
    private String mobile;
    private String email;
}
