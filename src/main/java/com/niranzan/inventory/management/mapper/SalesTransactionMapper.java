package com.niranzan.inventory.management.mapper;

import com.niranzan.inventory.management.entity.Customer;
import com.niranzan.inventory.management.entity.SalesTransaction;
import com.niranzan.inventory.management.view.response.CustomerResponse;
import com.niranzan.inventory.management.view.response.SalesTransactionItemResponse;
import com.niranzan.inventory.management.view.response.SalesTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SalesTransactionMapper {
    private final SalesTransactionItemMapper salesTransactionItemMapper;

    public SalesTransactionResponse toResponse(SalesTransaction transaction) {
        SalesTransactionResponse response = new SalesTransactionResponse();
        response.setId(transaction.getId());
        response.setPaymentMethod(transaction.getPaymentMethod().getMethod());
        response.setTransactionDate(transaction.getTransactionDateTime());
        String formatted = transaction.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        response.setFormattedTxDate(formatted);
        response.setInvoiceNumber(transaction.getInvoiceNumber());
        response.setOrderNumber(transaction.getOrderNumber());

        response.setTotalTax(transaction.getTotalTax());
        response.setTotalDiscount(transaction.getTotalDiscount());
        response.setGrandTotal(transaction.getTotalAmount());

        Customer customer = transaction.getCustomer();
        if (customer != null) {
            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setCustomerCode(customerResponse.getCustomerCode());
            customerResponse.setCustomerName(customer.getFirstName() + " - " + customer.getLastName());
            customerResponse.setAddress(customer.getAddress());
            customerResponse.setEmail(customer.getEmail());
            customerResponse.setMobile(customer.getMobile());
            response.setCustomer(customerResponse);
        }

        List<SalesTransactionItemResponse> salesTransactionItems = transaction.getItems().stream()
                .map(salesTransactionItemMapper::toResponse)
                .toList();
        response.setItems(salesTransactionItems);
        return response;
    }
}
