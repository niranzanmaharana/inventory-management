package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.dto.SupplierDto;
import com.niranzan.inventory.management.entity.Supplier;
import com.niranzan.inventory.management.exceptions.ResourceNotFoundException;
import com.niranzan.inventory.management.repository.SupplierRepository;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private static final String SUPPLIER_NOT_FOUND_WITH_ID = "Supplier not found with id: {}";
    private final SupplierRepository supplierRepository;

    public List<SupplierDto> findAll() {
        return supplierRepository.findAll().stream().map(this::toDto).toList();
    }

    public SupplierDto getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(SUPPLIER_NOT_FOUND_WITH_ID, String.valueOf(id))));
    }

    public SupplierDto saveSupplier(SupplierDto supplierDto) {
        Supplier supplier = toEntity(supplierDto);
        return toDto(supplierRepository.save(supplier));
    }

    public SupplierDto updateSupplier(SupplierDto supplierDto) {
        Supplier supplier = toEntityForUpdate(supplierDto);
        return toDto(supplierRepository.save(supplier));
    }

    public Supplier toggleStatus(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(SUPPLIER_NOT_FOUND_WITH_ID, String.valueOf(id))));
        supplier.setEnabled(!supplier.isEnabled());
        return supplierRepository.save(supplier);
    }

    private Supplier toEntity(SupplierDto dto) {
        Supplier supplier = new Supplier();
        supplier.setId(dto.getId());
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setMobile(dto.getMobile());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setWebsite(dto.getWebsite());
        supplier.setPurchaseOrders(null);
        return supplier;
    }

    private Supplier toEntityForUpdate(SupplierDto dto) {
        Supplier supplier = supplierRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormatUtil.format(SUPPLIER_NOT_FOUND_WITH_ID, String.valueOf(dto.getId()))));
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setMobile(dto.getMobile());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setWebsite(dto.getWebsite());
        return supplier;
    }

    private SupplierDto toDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .enabled(supplier.isEnabled())
                .supplierName(supplier.getSupplierName())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .mobile(supplier.getMobile())
                .website(supplier.getWebsite())
                .build();
    }
}
