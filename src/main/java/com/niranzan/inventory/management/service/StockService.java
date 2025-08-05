package com.niranzan.inventory.management.service;

import com.niranzan.inventory.management.mapper.InventoryMapper;
import com.niranzan.inventory.management.repository.StockRepository;
import com.niranzan.inventory.management.view.response.StockItemResponseView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final InventoryMapper inventoryMapper;

    public Set<StockItemResponseView> findAll() {
        return stockRepository.findAll()
                .stream().map(inventoryMapper::toResponse).collect(Collectors.toSet());
    }
}
