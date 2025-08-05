package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Inventory, Long> {
}
