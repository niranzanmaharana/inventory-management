package com.niranzan.inventory.management.repository;

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
}
