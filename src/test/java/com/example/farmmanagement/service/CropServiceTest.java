package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.repository.CropRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropServiceTest {

    @Mock
    private CropRepository cropRepository;

    @InjectMocks
    private CropService cropService;

    @Test
    void getAllCrops_ShouldReturnAllCrops() {
        // Given
        Crop crop1 = createCrop(1L, "Maize", LocalDate.now().plusDays(30));
        Crop crop2 = createCrop(2L, "Wheat", LocalDate.now().plusDays(60));
        when(cropRepository.findAll()).thenReturn(Arrays.asList(crop1, crop2));

        // When
        List<Crop> result = cropService.getAllCrops();

        // Then
        assertThat(result).hasSize(2);
        verify(cropRepository).findAll();
    }

    @Test
    void getCropById_ShouldReturnCrop() {
        // Given
        Crop crop = createCrop(1L, "Beans", LocalDate.now());
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));

        // When
        Optional<Crop> result = cropService.getCropById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo("Beans");
    }

    @Test
    void saveCrop_ShouldSaveAndReturn() {
        // Given
        Crop crop = createCrop(null, "Rice", LocalDate.now());
        when(cropRepository.save(any(Crop.class))).thenReturn(crop);

        // When
        Crop result = cropService.saveCrop(crop);

        // Then
        assertThat(result.getType()).isEqualTo("Rice");
        verify(cropRepository).save(crop);
    }

    @Test
    void deleteCrop_ShouldCallRepository() {
        // When
        cropService.deleteCrop(1L);

        // Then
        verify(cropRepository).deleteById(1L);
    }

    @Test
    void countCrops_ShouldReturnCount() {
        // Given
        when(cropRepository.count()).thenReturn(25L);

        // When
        long result = cropService.countCrops();

        // Then
        assertThat(result).isEqualTo(25L);
    }

    @Test
    void getCropTypeDistribution_ShouldGroupByType() {
        // Given
        Crop crop1 = createCrop(1L, "Maize", LocalDate.now());
        Crop crop2 = createCrop(2L, "Maize", LocalDate.now());
        Crop crop3 = createCrop(3L, "Wheat", LocalDate.now());
        when(cropRepository.findAll()).thenReturn(Arrays.asList(crop1, crop2, crop3));

        // When
        Map<String, Long> result = cropService.getCropTypeDistribution();

        // Then
        assertThat(result).containsEntry("Maize", 2L);
        assertThat(result).containsEntry("Wheat", 1L);
    }

    @Test
    void getUpcomingHarvests_ShouldReturnFutureCrops() {
        // Given
        Crop pastCrop = createCrop(1L, "Maize", LocalDate.now().minusDays(10));
        Crop futureCrop1 = createCrop(2L, "Wheat", LocalDate.now().plusDays(5));
        Crop futureCrop2 = createCrop(3L, "Beans", LocalDate.now().plusDays(10));
        when(cropRepository.findAll()).thenReturn(Arrays.asList(pastCrop, futureCrop1, futureCrop2));

        // When
        List<Crop> result = cropService.getUpcomingHarvests();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getHarvestDate()).isBefore(result.get(1).getHarvestDate());
    }

    @Test
    void calculateTotalYield_ShouldReturnMockValue() {
        // When
        String result = cropService.calculateTotalYield();

        // Then
        assertThat(result).isEqualTo("450 tons");
    }

    @Test
    void getGrowthStatus_ShouldReturnMockValue() {
        // When
        String result = cropService.getGrowthStatus();

        // Then
        assertThat(result).isEqualTo("85%");
    }

    @Test
    void getHarvestStatistics_ShouldReturnStats() {
        // When
        Map<String, Integer> result = cropService.getHarvestStatistics();

        // Then
        assertThat(result).containsKey("Wasted");
        assertThat(result).containsKey("Planted");
        assertThat(result).containsKey("Collected");
    }

    @Test
    void getCropGrowthData_ShouldReturnGrowthData() {
        // When
        List<Integer> result = cropService.getCropGrowthData();

        // Then
        assertThat(result).hasSize(7);
    }

    private Crop createCrop(Long id, String type, LocalDate harvestDate) {
        Crop crop = new Crop();
        crop.setId(id);
        crop.setType(type);
        crop.setHarvestDate(harvestDate);
        return crop;
    }
}
