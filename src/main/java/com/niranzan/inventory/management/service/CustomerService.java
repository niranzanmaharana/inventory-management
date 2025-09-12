package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.CustomerDto;
import com.niranzan.inventory.management.entity.Customer;
import com.niranzan.inventory.management.mapper.CustomerMapper;
import com.niranzan.inventory.management.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerDto> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(customerMapper::toDto).toList();
    }

    public List<CustomerDto> search(String keyword) {
        List<Customer> customers = customerRepository.findByFirstNameContainingIgnoreCaseOrMobileContaining(keyword, keyword);
        return customers.stream().map(customerMapper::toDto).toList();
    }

    public CustomerDto save(CustomerDto customerDto) {
        Customer entity = customerMapper.toEntity(customerDto);
        entity.setCustomerCode(generateCustomerCode(customerDto.getFirstName(), customerDto.getLastName()));
        Customer savedCustomer = customerRepository.save(entity);
        return customerMapper.toDto(savedCustomer);
    }

    private String generateCustomerCode(String firstName, String lastName) {
        Long maxId = customerRepository.findMaxId();
        long nextId = (maxId != null ? maxId + 1 : 1);

        String codePrefix;

        if (lastName != null && !lastName.trim().isEmpty()) {
            codePrefix = firstName.substring(0, 1).toUpperCase() +
                    lastName.substring(0, 1).toUpperCase();
        } else {
            codePrefix = firstName.substring(0, Math.min(2, firstName.length())).toUpperCase();
        }

        if (codePrefix.length() < 2) {
            codePrefix = String.format("%-2s", codePrefix).replace(' ', 'X');
        }

        return String.format("%s%06d", codePrefix, nextId);
    }
}
