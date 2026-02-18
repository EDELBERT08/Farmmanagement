package com.example.farmmanagement.repository;


import com.example.farmmanagement.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    // Add custom query methods if needed (e.g., findByType(String type))
}