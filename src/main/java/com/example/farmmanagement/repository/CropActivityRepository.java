package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.CropActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropActivityRepository extends JpaRepository<CropActivity, Long> {
    List<CropActivity> findByCropId(Long cropId);
    // Force recompile
}
