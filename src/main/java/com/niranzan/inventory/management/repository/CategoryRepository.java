package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByParentIsNull();

    @Query("SELECT c FROM ProductCategory c WHERE c.parent.id = :categoryId")
    List<ProductCategory> findByParent(@Param("categoryId") Long categoryId);

    List<ProductCategory> findAllByIdNotIn(List<Long> ids);
}
