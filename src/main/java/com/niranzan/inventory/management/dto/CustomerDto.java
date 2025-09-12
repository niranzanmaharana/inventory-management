package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    @NotEmpty(message = "First name must not be empty")
    @Size(min = 3, max = 50, message = "First name should be between 3 and 50 characters")
    private String firstName;
    @Size(min = 3, max = 50, message = "Last name should be between 3 and 50 characters")
    private String lastName;
    @NotEmpty(message = "Mobile number should not be empty")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number should be a valid 10 digit number")
    private String mobile;
    @Email(message = "Invalid email")
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String email;
    @NotEmpty(message = "Address must not be empty")
    @Size(min = 3, max = 200, message = "Address should be between 3 and 200 characters")
    private String address;
    @NotEmpty(message = "Postal code must not be empty")
    @Size(min = 3, max = 10, message = "Postal code should be between 3 and 10 characters")
    private String postalCode;
    private String customerCode;
}
