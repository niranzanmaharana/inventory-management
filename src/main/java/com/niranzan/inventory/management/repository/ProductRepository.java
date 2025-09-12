package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.dto.ProductBatchDto;
import com.niranzan.inventory.management.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductItem, Long> {
    @Query("SELECT p FROM ProductItem p WHERE p.category.id = :categoryId")
    List<ProductItem> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT MAX(p.id) FROM ProductItem p")
    Long findMaxId();

    @Query("""
                SELECT new com.niranzan.inventory.management.dto.ProductBatchDto(
                    inv.id, pi.id, pi.productName, pi.productCode as amount,
                    cat.categoryName, inv.batchCode, inv.pricePerUnit,
                    inv.receivedQuantity, inv.availableQuantity,
                    inv.receivedDate, inv.expiryDate
                )
                FROM ProductItem pi
                JOIN pi.category cat
                JOIN Inventory inv ON inv.productItem.id = pi.id
                WHERE lower(pi.productName) LIKE lower(concat('%', :keyword, '%'))
                   OR lower(pi.productCode) LIKE lower(concat('%', :keyword, '%'))
            """)
    List<ProductBatchDto> searchProductsWithBatch(@Param("keyword") String keyword);
}
