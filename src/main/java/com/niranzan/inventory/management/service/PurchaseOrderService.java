package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.config.AppMessage;
import com.niranzan.inventory.management.dto.PurchaseItemDto;
import com.niranzan.inventory.management.dto.PurchaseOrderDto;
import com.niranzan.inventory.management.entity.Inventory;
import com.niranzan.inventory.management.entity.ProductItem;
import com.niranzan.inventory.management.entity.PurchaseOrder;
import com.niranzan.inventory.management.entity.PurchaseOrderItem;
import com.niranzan.inventory.management.entity.Supplier;
import com.niranzan.inventory.management.enums.AppConstants;
import com.niranzan.inventory.management.enums.PurchaseOrderStatus;
import com.niranzan.inventory.management.exceptions.InvalidFormDataException;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.mapper.PurchaseOrderItemMapper;
import com.niranzan.inventory.management.mapper.PurchaseOrderMapper;
import com.niranzan.inventory.management.repository.InventoryRepository;
import com.niranzan.inventory.management.repository.ProductRepository;
import com.niranzan.inventory.management.repository.PurchaseOrderItemRepository;
import com.niranzan.inventory.management.repository.PurchaseOrderRepository;
import com.niranzan.inventory.management.repository.SupplierRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.PurchaseOrderResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.niranzan.inventory.management.config.AppMessage.MSG_PO_IS_NOT_IN_DRAFT;
import static com.niranzan.inventory.management.config.AppMessage.MSG_PO_NOT_FOUND_WITH_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

    public List<PurchaseOrderResponse> findAll() {
        return purchaseOrderRepository.findAll().stream()
                .map(purchaseOrderMapper::toResponse)
                .toList();
    }


    @Transactional
    public String draftPurchaseOrder(PurchaseOrderDto purchaseOrderDto) {
        Supplier supplier = supplierRepository.findById(purchaseOrderDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageFormatUtil.format(AppMessage.MSG_SUPPLIER_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(purchaseOrderDto.getSupplierId()))));

        PurchaseOrder purchaseOrder = purchaseOrderDto.getId() != null
                ? purchaseOrderRepository.findById(purchaseOrderDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageFormatUtil.format(MSG_PO_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(purchaseOrderDto.getId()))))
                : new PurchaseOrder();

        purchaseOrder.setPurchaseDate(getOrDefault(purchaseOrderDto.getPurchaseDate(), LocalDate.now()));
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderNumber(purchaseOrderDto.getOrderNumber());
        purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
        purchaseOrder.setRemarks("");
        purchaseOrder.setBillFileUrl("PENDING");

        // Handle PO Items
        updatePurchaseOrderItems(purchaseOrder, purchaseOrderDto.getItems());

        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
        return saved.getStatus().getStatus();
    }

    @Transactional
    public String placePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PO_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));
        if (PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())) {
            purchaseOrder.setStatus(PurchaseOrderStatus.PLACED);
            PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
            return saved.getStatus().getStatus();
        }
        throw new InvalidFormDataException(MessageFormatUtil.format(MSG_PO_IS_NOT_IN_DRAFT.getMessage(), "PLACE", "DRAFT"));
    }

    @Transactional
    public String cancelPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PO_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));
        if (!PurchaseOrderStatus.RECEIVED.equals(purchaseOrder.getStatus())) {
            purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);
            PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
            return saved.getStatus().getStatus();
        }
        throw new InvalidFormDataException(MessageFormatUtil.format(MSG_PO_IS_NOT_IN_DRAFT.getMessage(), "CANCEL", "DRAFT"));
    }

    @Transactional
    public String receivePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PO_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));
        if (!PurchaseOrderStatus.PLACED.equals(purchaseOrder.getStatus())) {
            throw new InvalidFormDataException(MessageFormatUtil.format(MSG_PO_IS_NOT_IN_DRAFT.getMessage(), "RECEIVE", "PLACED"));
        }
        Set<Inventory> inventoryList = new HashSet<>();
        for (PurchaseOrderItem orderItem : purchaseOrder.getItems()) {
            Long productId = orderItem.getProductItem().getId();
            ProductItem productItem = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(AppMessage.MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(productId))));
            Inventory inventory = buildInventory(orderItem, productItem);
            inventoryList.add(inventory);
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
        if (!inventoryList.isEmpty()) {
            inventoryRepository.saveAll(inventoryList);
        }
        return saved.getStatus().getStatus();
    }

    private void updatePurchaseOrderItems(PurchaseOrder purchaseOrder, List<PurchaseItemDto> itemDtos) {
        List<PurchaseOrderItem> existingItems = purchaseOrder.getItems();

        // Remove items no longer present in the DTO
        existingItems.removeIf(existingItem ->
                itemDtos.stream().noneMatch(dto -> Objects.equals(dto.getId(), existingItem.getId()))
        );

        for (PurchaseItemDto itemDto : itemDtos) {
            PurchaseOrderItem item = existingItems.stream()
                    .filter(existing -> Objects.equals(existing.getId(), itemDto.getId()))
                    .findFirst()
                    .orElse(null);

            ProductItem productItem = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            MessageFormatUtil.format(AppMessage.MSG_PRODUCT_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(itemDto.getProductId()))));

            if (item == null) {
                item = new PurchaseOrderItem();
                item.setPurchaseOrder(purchaseOrder);
                item.setBatchCode(generateBatchCode());
                existingItems.add(item);
            }

            item.setProductItem(productItem);
            item.setQuantity(itemDto.getQuantity());
            item.setPricePerUnit(itemDto.getPricePerUnit());

            LocalDate expiry = getOrDefault(itemDto.getExpiryDate(),
                    LocalDate.parse(AppConstants.DEFAULT_EXPIRY_DATE.getValue()));
            item.setExpiryDate(expiry);

            item.setSubTotal(item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
    }

    private Inventory buildInventory(PurchaseOrderItem purchaseOrderItem, ProductItem productItem) {
        Inventory inventory = new Inventory();
        inventory.setProductItem(productItem);
        inventory.setPurchaseOrderItem(purchaseOrderItem);
        inventory.setReceivedDate(LocalDateTime.now());
        inventory.setReceivedQuantity(purchaseOrderItem.getQuantity());
        inventory.setAvailableQuantity(purchaseOrderItem.getQuantity());
        inventory.setPricePerUnit(purchaseOrderItem.getPricePerUnit());
        inventory.setExpiryDate(purchaseOrderItem.getExpiryDate());
        inventory.setBatchCode(purchaseOrderItem.getBatchCode());
        inventory.setEnabled(Boolean.TRUE);
        return inventory;
    }

    private String generateBatchCode() {
        String prefix = "BATCH";
        Long maxId = purchaseOrderItemRepository.findMaxId();
        long nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("%s-%04d", prefix, nextId);
    }

    private LocalDate getOrDefault(LocalDate providedDate, LocalDate defaultDate) {
        if (providedDate == null) {
            log.warn("Date not provided. Defaulting to {}", defaultDate);
            return defaultDate;
        }
        return providedDate;
    }

    public boolean findIfPurchaseOrderEditable(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PO_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(purchaseOrderId))));
        return PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus());
    }

    public PurchaseOrderDto prepareForEdit(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(MSG_PO_NOT_FOUND_WITH_ID.getMessage(), String.valueOf(id))));
        PurchaseOrderDto purchaseOrderDto = purchaseOrderMapper.toDto(purchaseOrder);

        List<PurchaseItemDto> purchaseItemDtoList = purchaseOrder.getItems().stream()
                .map(purchaseOrderItemMapper::toDto)
                .toList();

        purchaseOrderDto.setItems(purchaseItemDtoList);
        return purchaseOrderDto;
    }
}
