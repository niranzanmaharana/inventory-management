package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.dto.CustomerDto;
import com.niranzan.inventory.management.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .mobile(customer.getMobile())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .postalCode(customer.getPostalCode())
                .build();
    }

    public Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setMobile(dto.getMobile());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setPostalCode(dto.getPostalCode());
        return customer;
    }
}
