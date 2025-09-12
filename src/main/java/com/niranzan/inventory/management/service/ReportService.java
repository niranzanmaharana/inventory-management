package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PurchaseOrderRepository purchaseOrderRepository;
}
