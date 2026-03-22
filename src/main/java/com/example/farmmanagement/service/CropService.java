package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.repository.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CropService {

    private final CropRepository cropRepository;

    @Autowired
    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    public Optional<Crop> getCropById(Long id) {
        return cropRepository.findById(id);
    }

    public Crop saveCrop(Crop crop) {
        return cropRepository.save(crop);
    }

    public void deleteCrop(Long id) {
        cropRepository.deleteById(id);
    }

    public long countCrops() {
        return cropRepository.count();
    }

    public java.util.Map<String, Long> getCropTypeDistribution() {
        return cropRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(Crop::getType, java.util.stream.Collectors.counting()));
    }

    public List<Crop> getUpcomingHarvests() {
        // Return crops with harvest date in the future, sorted by date
        return cropRepository.findAll().stream()
                .filter(c -> c.getHarvestDate() != null && c.getHarvestDate().isAfter(java.time.LocalDate.now()))
                .sorted(java.util.Comparator.comparing(Crop::getHarvestDate))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
    }

    // Mock Data Methods for Dashboard
    public String calculateTotalYield() {
        return "450 tons"; // Mock
    }

    public String getGrowthStatus() {
        return "85%"; // Mock
    }

    public java.util.Map<String, Integer> getHarvestStatistics() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("Wasted", 30);
        stats.put("Planted", 200);
        stats.put("Collected", 170);
        return stats;
    }

    public java.util.List<Integer> getCropGrowthData() {
        return java.util.Arrays.asList(1, 2, 3, 3, 4, 4, 5); // Mock growth per day
    }

    // Add other business logic methods here
}
