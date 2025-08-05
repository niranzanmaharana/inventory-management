package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    @Query("SELECT MAX(poi.id) FROM PurchaseOrderItem poi")
    Long findMaxId();
}
