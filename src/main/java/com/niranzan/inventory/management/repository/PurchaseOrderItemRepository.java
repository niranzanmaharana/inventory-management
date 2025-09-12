package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    @Query("SELECT MAX(poi.id) FROM PurchaseOrderItem poi")
    Long findMaxId();

    @Query("""
                SELECT p FROM PurchaseOrderItem p
                WHERE (:startDate IS NULL OR p.purchaseOrder.purchaseDate >= :startDate)
                AND (:endDate IS NULL OR p.purchaseOrder.purchaseDate <= :endDate)
                AND (:supplierId IS NULL OR p.purchaseOrder.supplier.id = :supplierId)
                AND (:categoryId IS NULL OR p.productItem.category.id = :categoryId)
                ORDER BY p.purchaseOrder.purchaseDate DESC
            """)
    List<PurchaseOrderItem> findByFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("supplierId") Long supplierId,
            @Param("categoryId") Long categoryId
    );
}
