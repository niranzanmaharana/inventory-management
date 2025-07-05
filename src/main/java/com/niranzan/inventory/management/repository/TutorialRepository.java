package com.niranzan.inventory.management.repository;

import com.niranzan.inventory.management.entity.Tutorial;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {
    List<Tutorial> findByTitleContainingIgnoreCase(String keyword);

    @Query("UPDATE Tutorial t SET t.published = :published WHERE t.id = :id")
    @Modifying
    public void updatePublishedStatus(Integer id, boolean published);
}
