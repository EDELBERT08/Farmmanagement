package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.CropTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropTransactionRepository extends JpaRepository<CropTransaction, Long> {
    List<CropTransaction> findByCropId(Long cropId);
}
