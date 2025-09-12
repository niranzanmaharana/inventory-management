package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.SalesTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesTransactionRepository extends JpaRepository<SalesTransaction, Long> {
    @Query("SELECT COALESCE(MAX(s.id), 0) FROM SalesTransaction s")
    Long findMaxId();

    Optional<SalesTransaction> findByInvoiceNumber(String invoiceNumber);

    Optional<SalesTransaction> findByOrderNumber(String invoiceNumber);

    @Query("SELECT st FROM SalesTransaction st WHERE st.customer.id = :customerId")
    List<SalesTransaction> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT st FROM SalesTransaction st WHERE st.transactionDateTime >= :startDate AND st.transactionDateTime <= :endDate ORDER BY st.transactionDateTime DESC")
    List<SalesTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
