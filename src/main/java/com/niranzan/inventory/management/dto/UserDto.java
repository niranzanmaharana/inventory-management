package com.niranzan.inventory.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserDto {
    private Long id;
    @Size(max = 10, message = "Please select salutation")
    private String salutation;
    @Size(max = 50, min = 3, message = "First name should be 3-50 characters long")
    private String firstName;
    @Size(max = 50, min = 3, message = "Last name should be 3-50 characters long")
    private String lastName;
    @NotEmpty(message = "Please select gender")
    @Size(max = 10, message = "Gender should be 10 characters long")
    private String gender;
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth should not be empty")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number should be a valid 10 digit number")
    private String mobile;
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Invalid email")
    @Size(max = 100, message = "Email should be less than 100 characters")
    private String email;
    @Size(max = 50, min = 5, message = "Username should be 5-50 characters long")
    private String username;
    @Size(max = 16, min = 8, message = "Password should be 8-16 characters long")
    private String password;
    private RoleDto role;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
