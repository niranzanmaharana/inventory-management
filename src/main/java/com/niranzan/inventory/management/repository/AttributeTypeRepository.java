package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AttributeTypeRepository extends JpaRepository<AttributeType, Long> {
    boolean existsByAttributeNameIgnoreCase(String name);

    Set<AttributeType> findByIdIn(Set<Long> ids);
}
