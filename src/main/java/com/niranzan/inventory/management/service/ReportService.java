package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.entity.SalesTransaction;
import com.niranzan.inventory.management.repository.SalesTransactionRepository;
import com.niranzan.inventory.management.view.response.SalesTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final SalesTransactionRepository salesTransactionRepository;
    private final InvoiceService invoiceService;

    public List<SalesTransactionResponse> getSalesReport(LocalDateTime startDate, LocalDateTime endDate, Long customerId) {
        List<SalesTransactionResponse> sales;

        if (startDate != null && endDate != null) {
            sales = invoiceService.findByDateRange(startDate, endDate);
            if (customerId != null) {
                sales = sales.stream()
                        .filter(sale -> sale.getCustomer() != null && sale.getCustomer().getId().equals(customerId))
                        .collect(Collectors.toList());
            }
        } else if (customerId != null) {
            sales = invoiceService.findByCustomerId(customerId);
        } else {
            // For all sales, we need to get from repository directly as there's no service method for all
            List<SalesTransaction> transactions = salesTransactionRepository.findAll();
            sales = transactions.stream()
                    .map(this::mapToSalesResponse)
                    .collect(Collectors.toList());
        }

        return sales;
    }

    public Map<String, Object> getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<SalesTransactionResponse> sales = invoiceService.findByDateRange(startDate, endDate);

        double totalRevenue = sales.stream()
                .mapToDouble(sale -> sale.getGrandTotal() != null ? sale.getGrandTotal().doubleValue() : 0)
                .sum();

        double totalTax = sales.stream()
                .mapToDouble(sale -> sale.getTotalTax() != null ? sale.getTotalTax().doubleValue() : 0)
                .sum();

        double totalDiscount = sales.stream()
                .mapToDouble(sale -> sale.getTotalDiscount() != null ? sale.getTotalDiscount().doubleValue() : 0)
                .sum();

        long totalTransactions = sales.size();

        double averageOrderValue = totalTransactions > 0 ? totalRevenue / totalTransactions : 0;

        return Map.of(
                "totalRevenue", totalRevenue,
                "totalTax", totalTax,
                "totalDiscount", totalDiscount,
                "totalTransactions", totalTransactions,
                "averageOrderValue", averageOrderValue
        );
    }

    private SalesTransactionResponse mapToSalesResponse(SalesTransaction transaction) {
        // Simple mapping - you might want to use a proper mapper
        SalesTransactionResponse response = new SalesTransactionResponse();
        response.setId(transaction.getId());
        response.setInvoiceNumber(transaction.getInvoiceNumber());
        response.setOrderNumber(transaction.getOrderNumber());
        response.setTransactionDate(transaction.getTransactionDateTime());
        response.setGrandTotal(transaction.getTotalAmount());
        response.setTotalTax(transaction.getTotalTax());
        response.setTotalDiscount(transaction.getTotalDiscount());
        response.setPaymentMethod(transaction.getPaymentMethod().getMethod());

        // Format date
        response.setFormattedTxDate(transaction.getTransactionDateTime().toString());

        return response;
    }
}
