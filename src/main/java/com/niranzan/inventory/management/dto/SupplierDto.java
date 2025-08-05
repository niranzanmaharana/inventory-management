package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    private Long id;

    @NotEmpty(message = "Supplier name is required")
    @Size(min = 3, max = 100, message = "Supplier name must be between 3 and 100 chars")
    private String supplierName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 100, message = "Email should be max 100 chars")
    private String email;

    @NotEmpty(message = "Contact number is required")
    @Pattern(regexp = "\\d{10}", message = "Enter a 10-digit contact number")
    private String mobile;

    @Size(max = 100, message = "Website should be max 100 chars")
    @Pattern(
            regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(/\\S*)?$",
            message = "Website must be a valid URL"
    )
    private String website;

    @NotEmpty(message = "Address is required")
    @Size(max = 200, message = "Address should be max 200 chars")
    private String address;
    private boolean enabled;
}
