package com.example.farmmanagement.service;

import com.example.farmmanagement.model.SoilInput;
import com.example.farmmanagement.model.SoilRecord;
import com.example.farmmanagement.model.WaterRecord;
import com.example.farmmanagement.repository.SoilInputRepository;
import com.example.farmmanagement.repository.SoilRecordRepository;
import com.example.farmmanagement.repository.WaterRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SoilWaterService {

    private final SoilRecordRepository soilRecordRepository;
    private final WaterRecordRepository waterRecordRepository;
    private final SoilInputRepository soilInputRepository;

    @Autowired
    public SoilWaterService(
            SoilRecordRepository soilRecordRepository,
            WaterRecordRepository waterRecordRepository,
            SoilInputRepository soilInputRepository) {
        this.soilRecordRepository = soilRecordRepository;
        this.waterRecordRepository = waterRecordRepository;
        this.soilInputRepository = soilInputRepository;
    }

    // ===== Soil Records =====
    public List<SoilRecord> getSoilRecordsByField(Long fieldId) {
        return soilRecordRepository.findByFieldIdOrderByTestDateDesc(fieldId);
    }

    public SoilRecord saveSoilRecord(SoilRecord soilRecord) {
        return soilRecordRepository.save(soilRecord);
    }

    public Optional<SoilRecord> getSoilRecordById(Long id) {
        return soilRecordRepository.findById(id);
    }

    public Double getAverageSoilPH(Long fieldId) {
        List<SoilRecord> records = soilRecordRepository.findByFieldId(fieldId);
        return records.stream()
                .filter(r -> r.getPH() != null)
                .mapToDouble(SoilRecord::getPH)
                .average()
                .orElse(0.0);
    }

    // ===== Water Records =====
    public List<WaterRecord> getWaterRecordsByField(Long fieldId) {
        return waterRecordRepository.findByFieldIdOrderByTestDateDesc(fieldId);
    }

    public WaterRecord saveWaterRecord(WaterRecord waterRecord) {
        return waterRecordRepository.save(waterRecord);
    }

    public Optional<WaterRecord> getWaterRecordById(Long id) {
        return waterRecordRepository.findById(id);
    }

    public Double getAverageWaterPH(Long fieldId) {
        List<WaterRecord> records = waterRecordRepository.findByFieldId(fieldId);
        return records.stream()
                .filter(r -> r.getPH() != null)
                .mapToDouble(WaterRecord::getPH)
                .average()
                .orElse(0.0);
    }

    // ===== Soil Inputs =====
    public List<SoilInput> getSoilInputsByField(Long fieldId) {
        return soilInputRepository.findByFieldIdOrderByApplicationDateDesc(fieldId);
    }

    public SoilInput saveSoilInput(SoilInput soilInput) {
        return soilInputRepository.save(soilInput);
    }

    public Optional<SoilInput> getSoilInputById(Long id) {
        return soilInputRepository.findById(id);
    }

    public Double getTotalInputCost(Long fieldId) {
        Double cost = soilInputRepository.calculateTotalCostByField(fieldId);
        return cost != null ? cost : 0.0;
    }

    // Calculate average soil pH across all fields
    public Double calculateAverageSoilPH() {
        List<SoilRecord> allRecords = soilRecordRepository.findAll();
        return allRecords.stream()
                .filter(r -> r.getPH() != null)
                .mapToDouble(SoilRecord::getPH)
                .average()
                .orElse(0.0);
    }

    // ===== Analytics & Aggregations =====
    public Map<String, Object> getFieldSoilWaterSummary(Long fieldId) {
        Map<String, Object> summary = new HashMap<>();

        // Soil data
        List<SoilRecord> soilRecords = getSoilRecordsByField(fieldId);
        summary.put("totalSoilTests", soilRecords.size());
        summary.put("avgSoilPH", getAverageSoilPH(fieldId));
        summary.put("latestSoilRecord", soilRecords.isEmpty() ? null : soilRecords.get(0));

        // Water data
        List<WaterRecord> waterRecords = getWaterRecordsByField(fieldId);
        summary.put("totalWaterTests", waterRecords.size());
        summary.put("avgWaterPH", getAverageWaterPH(fieldId));
        summary.put("latestWaterRecord", waterRecords.isEmpty() ? null : waterRecords.get(0));

        // Input data
        List<SoilInput> inputs = getSoilInputsByField(fieldId);
        summary.put("totalInputs", inputs.size());
        summary.put("totalInputCost", getTotalInputCost(fieldId));

        return summary;
    }

    // Get chart data for soil pH trends over time
    public List<Map<String, Object>> getSoilPHTrendData(Long fieldId) {
        List<SoilRecord> records = soilRecordRepository.findByFieldIdOrderByTestDateDesc(fieldId);
        return records.stream()
                .filter(r -> r.getPH() != null && r.getTestDate() != null)
                .map(r -> {
                    Map<String, Object> point = new HashMap<>();
                    point.put("date", r.getTestDate().toString());
                    point.put("pH", r.getPH());
                    return point;
                })
                .toList();
    }

    // Get chart data for nutrient levels
    public Map<String, Double> getLatestNutrientLevels(Long fieldId) {
        List<SoilRecord> records = soilRecordRepository.findByFieldIdOrderByTestDateDesc(fieldId);
        if (records.isEmpty()) {
            return Map.of("nitrogen", 0.0, "phosphorus", 0.0, "potassium", 0.0);
        }

        SoilRecord latest = records.get(0);
        Map<String, Double> nutrients = new HashMap<>();
        nutrients.put("nitrogen", latest.getNitrogen() != null ? latest.getNitrogen() : 0.0);
        nutrients.put("phosphorus", latest.getPhosphorus() != null ? latest.getPhosphorus() : 0.0);
        nutrients.put("potassium", latest.getPotassium() != null ? latest.getPotassium() : 0.0);
        return nutrients;
    }
}
