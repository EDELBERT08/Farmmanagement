package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.SoilInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoilInputRepository extends JpaRepository<SoilInput, Long> {

    List<SoilInput> findByFieldIdOrderByApplicationDateDesc(Long fieldId);

    List<SoilInput> findByFieldId(Long fieldId);

    @Query("SELECT SUM(si.cost) FROM SoilInput si WHERE si.field.id = ?1")
    Double calculateTotalCostByField(Long fieldId);
}
