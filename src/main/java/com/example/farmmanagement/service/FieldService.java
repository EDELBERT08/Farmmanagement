package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FieldService {

    private final FieldRepository fieldRepository;

    @Autowired
    public FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    // Get only master farms (top-level)
    public List<Field> getMasterFarms() {
        return fieldRepository.findByParentFieldIsNull();
    }

    // Get subdivisions of a master farm
    public List<Field> getSubdivisions(Long masterFarmId) {
        return fieldRepository.findByParentFieldId(masterFarmId);
    }

    public Field saveField(Field field) {
        return fieldRepository.save(field);
    }

    public Optional<Field> getFieldById(Long id) {
        return fieldRepository.findById(id);
    }

    public Double calculateTotalLandArea() {
        return fieldRepository.findAll().stream()
                .filter(field -> field.getAreaSize() != null)
                .mapToDouble(Field::getAreaSize)
                .sum();
    }

    // Calculate total subdivision area for a master farm
    public Double calculateSubdivisionArea(Long masterFarmId) {
        return getSubdivisions(masterFarmId).stream()
                .filter(field -> field.getAreaSize() != null)
                .mapToDouble(Field::getAreaSize)
                .sum();
    }

    // Validate if a subdivision can be added
    public boolean canAddSubdivision(Long masterFarmId, Double subdivisionSize) {
        Optional<Field> masterFarm = getFieldById(masterFarmId);
        if (masterFarm.isEmpty() || subdivisionSize == null) {
            return false;
        }

        Double masterFarmSize = masterFarm.get().getAreaSize();
        if (masterFarmSize == null) {
            return false;
        }

        Double currentSubdivisionTotal = calculateSubdivisionArea(masterFarmId);
        return (currentSubdivisionTotal + subdivisionSize) <= masterFarmSize;
    }

    // Get remaining available space in master farm
    public Double getRemainingSpace(Long masterFarmId) {
        Optional<Field> masterFarm = getFieldById(masterFarmId);
        if (masterFarm.isEmpty() || masterFarm.get().getAreaSize() == null) {
            return 0.0;
        }

        Double masterFarmSize = masterFarm.get().getAreaSize();
        Double subdivisionTotal = calculateSubdivisionArea(masterFarmId);
        return masterFarmSize - subdivisionTotal;
    }

    public java.util.Map<String, Object> getFieldMetrics() {
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        metrics.put("phLevel", 5);
        metrics.put("temperature", 26);
        metrics.put("waterLevel", 1060);
        return metrics;
    }
}
