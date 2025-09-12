package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.SalesTransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesTransactionRecordRepository extends JpaRepository<SalesTransactionItem, Long> {
}
