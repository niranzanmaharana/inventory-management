package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByEmail(String email);
    UserProfile findByMobile(String mobile);
    UserProfile findByUsername(String username);
}
