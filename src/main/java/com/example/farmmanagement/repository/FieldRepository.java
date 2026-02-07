package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    // Hierarchy queries
    List<Field> findByParentFieldIsNull(); // Get all master farms

    List<Field> findByParentFieldId(Long parentId); // Get subdivisions

    List<Field> findByIsMasterFarmTrue(); // Get master farms only
}
