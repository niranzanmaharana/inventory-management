package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByParentIsNull();          // Top-level

    List<ProductCategory> findByParentId(Long parentId); // Subcategories

    List<ProductCategory> findByIdNot(Long excludeId);
}
