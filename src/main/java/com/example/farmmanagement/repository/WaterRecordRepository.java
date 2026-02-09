package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.WaterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterRecordRepository extends JpaRepository<WaterRecord, Long> {

    List<WaterRecord> findByFieldIdOrderByTestDateDesc(Long fieldId);

    List<WaterRecord> findByFieldId(Long fieldId);
}
