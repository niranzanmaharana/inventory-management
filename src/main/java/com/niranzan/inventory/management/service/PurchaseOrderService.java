package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.PurchaseItemDto;
import com.niranzan.inventory.management.dto.PurchaseOrderDto;
import com.niranzan.inventory.management.entity.ProductItem;
import com.niranzan.inventory.management.entity.PurchaseItem;
import com.niranzan.inventory.management.entity.PurchaseOrder;
import com.niranzan.inventory.management.entity.Supplier;
import com.niranzan.inventory.management.repository.ProductRepository;
import com.niranzan.inventory.management.repository.PurchaseOrderRepository;
import com.niranzan.inventory.management.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public void createPurchaseOrder(PurchaseOrderDto dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId()).orElseThrow();

        PurchaseOrder order = new PurchaseOrder();
        order.setPurchaseDate(Objects.isNull(dto.getPurchaseDate()) ? LocalDateTime.now() : dto.getPurchaseDate());

        order.setSupplier(supplier);

        Set<PurchaseItem> items = new HashSet<>();
        for (PurchaseItemDto itemDto : dto.getItems()) {
            ProductItem product = productRepository.findById(itemDto.getProductId()).orElseThrow();

            addPurchaseItems(itemDto, product, order, items);
        }

        order.setItems(items);
        order.setTotalAmount(items.stream()
                .map(PurchaseItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        purchaseOrderRepository.save(order);
    }

    private static void addPurchaseItems(PurchaseItemDto itemDto, ProductItem product, PurchaseOrder order, Set<PurchaseItem> items) {
        PurchaseItem item = new PurchaseItem();
        item.setProductItem(product);
        item.setQuantity(itemDto.getQuantity());
        item.setPricePerUnit(itemDto.getPricePerUnit());
        item.setExpiryDate(itemDto.getExpiryDate());

        BigDecimal subTotal = itemDto.getPricePerUnit()
                .multiply(BigDecimal.valueOf(itemDto.getQuantity()));
        item.setSubTotal(subTotal);
        item.setPurchaseOrder(order);

        // Update product quantity
        product.setQuantity(product.getQuantity() + itemDto.getQuantity());

        items.add(item);
    }
}
