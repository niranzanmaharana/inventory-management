package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT MAX(c.id) FROM Customer c")
    Long findMaxId();
    List<Customer> findByFirstNameContainingIgnoreCaseOrMobileContaining(String name, String mobile);
}
