package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.config.AppMessage;
import com.niranzan.inventory.management.dto.SalesTransactionDto;
import com.niranzan.inventory.management.dto.SalesTransactionItemDto;
import com.niranzan.inventory.management.entity.Customer;
import com.niranzan.inventory.management.entity.Inventory;
import com.niranzan.inventory.management.entity.ProductItem;
import com.niranzan.inventory.management.entity.SalesTransaction;
import com.niranzan.inventory.management.entity.SalesTransactionItem;
import com.niranzan.inventory.management.enums.PaymentMethod;
import com.niranzan.inventory.management.exceptions.InvalidFormDataException;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.repository.CustomerRepository;
import com.niranzan.inventory.management.repository.InventoryRepository;
import com.niranzan.inventory.management.repository.ProductRepository;
import com.niranzan.inventory.management.repository.SalesTransactionRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import io.jsonwebtoken.lang.Collections;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.niranzan.inventory.management.config.AppMessage.MSG_INVENTORY_NOT_FOUND_WITH_ID;
import static com.niranzan.inventory.management.config.AppMessage.MSG_PRODUCT_NOT_FOUND_WITH_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesService {
    private final SalesTransactionRepository salesTransactionRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public SalesTransaction saveSale(SalesTransactionDto salesTransactionDto) {
        log.info("Saving sale for buyer: {}", salesTransactionDto.getCustomerId());
        if (salesTransactionDto.getPaymentMethod() == null || salesTransactionDto.getPaymentMethod().isEmpty()) {
            salesTransactionDto.setPaymentMethod("CASH");
        }
        if (Collections.isEmpty(salesTransactionDto.getItems())) {
            throw new InvalidFormDataException("No items received!");
        }

        SalesTransaction transaction = new SalesTransaction();
        Customer customer = customerRepository.findById(salesTransactionDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(AppMessage.MSG_CUSTOMER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(salesTransactionDto.getCustomerId()))));
        transaction.setCustomer(customer);
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setPaid(Boolean.TRUE);
        transaction.setPaymentMethod(PaymentMethod.CASH);
        transaction.setInvoiceNumber(generateInvoiceNumber());
        transaction.setOrderNumber(generateOrderNumber());
        transaction.setTotalAmount(BigDecimal.ZERO);
        transaction.setTotalDiscount(BigDecimal.ZERO);
        transaction.setTotalTax(BigDecimal.ZERO);

        List<SalesTransactionItem> items = new ArrayList<>();

        for (SalesTransactionItemDto itemDto : salesTransactionDto.getItems()) {
            prepareSalesTransactionItems(itemDto, transaction, items);
        }

        transaction.setItems(items);

        return salesTransactionRepository.save(transaction);
    }

    private void prepareSalesTransactionItems(SalesTransactionItemDto itemDto, SalesTransaction transaction, List<SalesTransactionItem> items) {
        SalesTransactionItem item = new SalesTransactionItem();
        item.setSalesTransaction(transaction);

        ProductItem productItem = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new RuntimeException(MessageFormatUtil.format(MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(itemDto.getProductId()))));
        item.setProductItem(productItem);

        Inventory inventory = inventoryRepository.findById(itemDto.getInventoryId())
                .orElseThrow(() -> new RuntimeException(MessageFormatUtil.format(MSG_INVENTORY_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(itemDto.getInventoryId()))));

        int quantity = itemDto.getQuantity();
        BigDecimal pricePerUnit = inventory.getPricePerUnit();
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        item.setInventory(inventory);

        // Discounts & taxes
        BigDecimal discountPercent = inventory.getDiscountPercent() != null
                ? inventory.getDiscountPercent()
                : BigDecimal.ZERO;
        BigDecimal taxPercent = inventory.getTaxPercent() != null
                ? inventory.getTaxPercent()
                : BigDecimal.ZERO;

        // Subtotal = qty * price
        BigDecimal subtotal = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
        // Apply discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = subtotal.multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100));
            subtotal = subtotal.subtract(discountAmount);
        }

        // Apply tax
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (taxPercent.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = subtotal.multiply(taxPercent)
                    .divide(BigDecimal.valueOf(100));
            subtotal = subtotal.add(taxAmount);
        }

        // Set item details
        item.setQuantity(quantity);
        item.setPricePerUnit(pricePerUnit);
        item.setSubtotal(subtotal);
        item.setDiscountPercent(discountPercent);
        item.setDiscountAmount(discountAmount);
        item.setTaxPercent(taxPercent);
        item.setTaxAmount(taxAmount);

        // Add to total
        transaction.setTotalAmount(transaction.getTotalAmount().add(subtotal));
        transaction.setTotalDiscount(transaction.getTotalDiscount().add(discountAmount));
        transaction.setTotalTax(transaction.getTotalTax().add(taxAmount));
        items.add(item);
    }

    private String generateInvoiceNumber() {
        return "IMS-" + System.currentTimeMillis();
    }

    private String generateOrderNumber() {
        Long maxId = salesTransactionRepository.findMaxId();
        Long nextId = maxId + 1;
        return String.format("1%07d", nextId);
    }
}
