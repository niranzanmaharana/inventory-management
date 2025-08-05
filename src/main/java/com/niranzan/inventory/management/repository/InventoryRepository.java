package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT MAX(i.id) FROM Inventory i")
    Long findMaxId();

    @Query("SELECT i FROM Inventory i WHERE i.purchaseOrderItem.purchaseOrder.id = :purchaseOrderId")
    List<Inventory> findByPurchaseOrderId(@Param("purchaseOrderId") Long purchaseOrderId);
}
