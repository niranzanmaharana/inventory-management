package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.config.AppMessage;
import com.niranzan.inventory.management.entity.SalesTransaction;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.SalesTransactionMapper;
import com.niranzan.inventory.management.repository.CustomerRepository;
import com.niranzan.inventory.management.repository.SalesTransactionRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.SalesTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final SalesTransactionRepository salesTransactionRepository;
    private final SalesTransactionMapper salesTransactionMapper;
    private final CustomerRepository customerRepository;

    public SalesTransactionResponse findByInvoiceNumber(String invoiceNumber) {
        SalesTransaction salesTransaction = salesTransactionRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(AppMessage.MSG_INVOICE_NOT_FOUND_WITH_INVOICE_NUMBER.getMessage(), String.valueOf(invoiceNumber))));
        return salesTransactionMapper.toResponse(salesTransaction);
    }

    public SalesTransactionResponse findByOrderNumber(String orderNumber) {
        SalesTransaction salesTransaction = salesTransactionRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(AppMessage.MSG_INVOICE_NOT_FOUND_WITH_ORDER_NUMBER.getMessage(), String.valueOf(orderNumber))));
        return salesTransactionMapper.toResponse(salesTransaction);
    }

    public List<SalesTransactionResponse> findByCustomerId(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(AppMessage.MSG_CUSTOMER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(customerId))));
        List<SalesTransaction> salesTransactions = salesTransactionRepository.findByCustomerId(customerId);
        return salesTransactions.stream()
                .map(salesTransactionMapper::toResponse)
                .toList();
    }

    public List<SalesTransactionResponse> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SalesTransaction> salesTransactions = salesTransactionRepository.findByDateRange(startDate, endDate);
        return salesTransactions.stream()
                .map(salesTransactionMapper::toResponse)
                .toList();
    }
}
