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

    // Add other business logic methods here
}
