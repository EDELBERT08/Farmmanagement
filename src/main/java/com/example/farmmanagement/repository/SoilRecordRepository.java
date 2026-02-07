package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.SoilRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoilRecordRepository extends JpaRepository<SoilRecord, Long> {

    List<SoilRecord> findByFieldIdOrderByTestDateDesc(Long fieldId);

    List<SoilRecord> findByFieldId(Long fieldId);
}
