package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByRoleName(String name);
}
