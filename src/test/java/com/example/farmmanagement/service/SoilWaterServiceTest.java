package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.model.SoilInput;
import com.example.farmmanagement.model.SoilRecord;
import com.example.farmmanagement.model.WaterRecord;
import com.example.farmmanagement.repository.SoilInputRepository;
import com.example.farmmanagement.repository.SoilRecordRepository;
import com.example.farmmanagement.repository.WaterRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoilWaterServiceTest {

    @Mock
    private SoilRecordRepository soilRecordRepository;

    @Mock
    private WaterRecordRepository waterRecordRepository;

    @Mock
    private SoilInputRepository soilInputRepository;

    @InjectMocks
    private SoilWaterService soilWaterService;

    private Field field;
    private SoilRecord soilRecord;
    private WaterRecord waterRecord;
    private SoilInput soilInput;

    @BeforeEach
    void setUp() {
        field = new Field();
        field.setId(1L);
        field.setName("Test Field");

        soilRecord = new SoilRecord();
        soilRecord.setId(1L);
        soilRecord.setField(field);
        soilRecord.setTestDate(LocalDate.now());
        soilRecord.setPH(6.5);
        soilRecord.setNitrogen(10.0);
        soilRecord.setPhosphorus(20.0);
        soilRecord.setPotassium(30.0);

        waterRecord = new WaterRecord();
        waterRecord.setId(1L);
        waterRecord.setField(field);
        waterRecord.setTestDate(LocalDate.now());
        waterRecord.setPH(7.0);

        soilInput = new SoilInput();
        soilInput.setId(1L);
        soilInput.setField(field);
        soilInput.setInputType(SoilInput.InputType.FERTILIZER);
        soilInput.setCost(100.0);
    }

    // --- Soil Record Tests ---

    @Test
    void getSoilRecordsByField() {
        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(soilRecord));
        List<SoilRecord> results = soilWaterService.getSoilRecordsByField(1L);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(soilRecordRepository, times(1)).findByFieldIdOrderByTestDateDesc(1L);
    }

    @Test
    void saveSoilRecord() {
        when(soilRecordRepository.save(any(SoilRecord.class))).thenReturn(soilRecord);
        SoilRecord saved = soilWaterService.saveSoilRecord(soilRecord);
        assertNotNull(saved);
        assertEquals(6.5, saved.getPH());
    }

    @Test
    void getSoilRecordById() {
        when(soilRecordRepository.findById(1L)).thenReturn(Optional.of(soilRecord));
        Optional<SoilRecord> result = soilWaterService.getSoilRecordById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getAverageSoilPH() {
        SoilRecord r1 = new SoilRecord();
        r1.setPH(6.0);
        SoilRecord r2 = new SoilRecord();
        r2.setPH(7.0);
        when(soilRecordRepository.findByFieldId(1L)).thenReturn(Arrays.asList(r1, r2));

        Double avg = soilWaterService.getAverageSoilPH(1L);
        assertEquals(6.5, avg);
    }

    @Test
    void getAverageSoilPH_Empty() {
        when(soilRecordRepository.findByFieldId(1L)).thenReturn(Collections.emptyList());
        Double avg = soilWaterService.getAverageSoilPH(1L);
        assertEquals(0.0, avg);
    }

    @Test
    void getAverageSoilPH_NullValues() {
        SoilRecord r1 = new SoilRecord();
        r1.setPH(null);
        when(soilRecordRepository.findByFieldId(1L)).thenReturn(Collections.singletonList(r1));
        Double avg = soilWaterService.getAverageSoilPH(1L);
        assertEquals(0.0, avg);
    }

    // --- Water Record Tests ---

    @Test
    void getWaterRecordsByField() {
        when(waterRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(waterRecord));
        List<WaterRecord> results = soilWaterService.getWaterRecordsByField(1L);
        assertFalse(results.isEmpty());
    }

    @Test
    void saveWaterRecord() {
        when(waterRecordRepository.save(any(WaterRecord.class))).thenReturn(waterRecord);
        WaterRecord saved = soilWaterService.saveWaterRecord(waterRecord);
        assertNotNull(saved);
    }

    @Test
    void getWaterRecordById() {
        when(waterRecordRepository.findById(1L)).thenReturn(Optional.of(waterRecord));
        Optional<WaterRecord> result = soilWaterService.getWaterRecordById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void getAverageWaterPH() {
        WaterRecord r1 = new WaterRecord();
        r1.setPH(6.5);
        WaterRecord r2 = new WaterRecord();
        r2.setPH(7.5);
        when(waterRecordRepository.findByFieldId(1L)).thenReturn(Arrays.asList(r1, r2));
        assertEquals(7.0, soilWaterService.getAverageWaterPH(1L));
    }

    @Test
    void getAverageWaterPH_Empty() {
        when(waterRecordRepository.findByFieldId(1L)).thenReturn(Collections.emptyList());
        assertEquals(0.0, soilWaterService.getAverageWaterPH(1L));
    }

    // --- Soil Input Tests ---

    @Test
    void getSoilInputsByField() {
        when(soilInputRepository.findByFieldIdOrderByApplicationDateDesc(1L)).thenReturn(Collections.singletonList(soilInput));
        List<SoilInput> results = soilWaterService.getSoilInputsByField(1L);
        assertFalse(results.isEmpty());
    }

    @Test
    void saveSoilInput() {
        when(soilInputRepository.save(any(SoilInput.class))).thenReturn(soilInput);
        SoilInput saved = soilWaterService.saveSoilInput(soilInput);
        assertNotNull(saved);
    }

    @Test
    void getSoilInputById() {
        when(soilInputRepository.findById(1L)).thenReturn(Optional.of(soilInput));
        Optional<SoilInput> result = soilWaterService.getSoilInputById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void getTotalInputCost() {
        when(soilInputRepository.calculateTotalCostByField(1L)).thenReturn(150.0);
        Double cost = soilWaterService.getTotalInputCost(1L);
        assertEquals(150.0, cost);
    }

    @Test
    void getTotalInputCost_Null() {
        when(soilInputRepository.calculateTotalCostByField(1L)).thenReturn(null);
        Double cost = soilWaterService.getTotalInputCost(1L);
        assertEquals(0.0, cost);
    }

    // --- Analytics Tests ---

    @Test
    void calculateAverageSoilPH_Global() {
        SoilRecord r1 = new SoilRecord();
        r1.setPH(6.0);
        SoilRecord r2 = new SoilRecord();
        r2.setPH(8.0);
        when(soilRecordRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
        assertEquals(7.0, soilWaterService.calculateAverageSoilPH());
    }

    @Test
    void getFieldSoilWaterSummary() {
        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(soilRecord));
        when(waterRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(waterRecord));
        when(soilInputRepository.findByFieldIdOrderByApplicationDateDesc(1L)).thenReturn(Collections.singletonList(soilInput));
        
        // Mock sub-calls for averages/costs if necessary, or let them use the mocked repository responses
        // valid since getSoilRecordsByField calls repository
        when(soilRecordRepository.findByFieldId(1L)).thenReturn(Collections.singletonList(soilRecord));
        when(waterRecordRepository.findByFieldId(1L)).thenReturn(Collections.singletonList(waterRecord));
        when(soilInputRepository.calculateTotalCostByField(1L)).thenReturn(100.0);

        Map<String, Object> summary = soilWaterService.getFieldSoilWaterSummary(1L);
        
        assertEquals(1, summary.get("totalSoilTests"));
        assertEquals(1, summary.get("totalWaterTests"));
        assertEquals(1, summary.get("totalInputs"));
        assertEquals(100.0, summary.get("totalInputCost"));
    }

    @Test
    void getSoilPHTrendData() {
        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(soilRecord));
        List<Map<String, Object>> trend = soilWaterService.getSoilPHTrendData(1L);
        assertFalse(trend.isEmpty());
        assertEquals(6.5, trend.get(0).get("pH"));
    }

    @Test
    void getLatestNutrientLevels() {
        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(soilRecord));
        Map<String, Double> nutrients = soilWaterService.getLatestNutrientLevels(1L);
        assertEquals(10.0, nutrients.get("nitrogen"));
        assertEquals(20.0, nutrients.get("phosphorus"));
        assertEquals(30.0, nutrients.get("potassium"));
    }

    @Test
    void getLatestNutrientLevels_Empty() {
        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.emptyList());
        Map<String, Double> nutrients = soilWaterService.getLatestNutrientLevels(1L);
        assertEquals(0.0, nutrients.get("nitrogen"));
    }

    @Test
    void getAverageSoilPH_IgnoredNulls() {
        SoilRecord r1 = new SoilRecord();
        r1.setPH(6.0);
        SoilRecord r2 = new SoilRecord();
        r2.setPH(null); // Should be ignored
        when(soilRecordRepository.findByFieldId(1L)).thenReturn(Arrays.asList(r1, r2));

        Double avg = soilWaterService.getAverageSoilPH(1L);
        assertEquals(6.0, avg);
    }

    @Test
    void getAverageWaterPH_IgnoredNulls() {
        WaterRecord r1 = new WaterRecord();
        r1.setPH(8.0);
        WaterRecord r2 = new WaterRecord();
        r2.setPH(null); // Should be ignored
        when(waterRecordRepository.findByFieldId(1L)).thenReturn(Arrays.asList(r1, r2));

        Double avg = soilWaterService.getAverageWaterPH(1L);
        assertEquals(8.0, avg);
    }

    @Test
    void calculateAverageSoilPH_IgnoredNulls() {
        SoilRecord r1 = new SoilRecord();
        r1.setPH(5.0);
        SoilRecord r2 = new SoilRecord();
        r2.setPH(null); // Should be ignored
        when(soilRecordRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        Double avg = soilWaterService.calculateAverageSoilPH();
        assertEquals(5.0, avg);
    }

    @Test
    void getFieldSoilWaterSummary_EmptyLists() {
        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.emptyList());
        when(waterRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.emptyList());
        when(soilInputRepository.findByFieldIdOrderByApplicationDateDesc(1L)).thenReturn(Collections.emptyList());
        
        // Mock sub-calls for averages/costs to avoid NPE if implementation changes or defaults
        when(soilRecordRepository.findByFieldId(1L)).thenReturn(Collections.emptyList());
        when(waterRecordRepository.findByFieldId(1L)).thenReturn(Collections.emptyList());
        when(soilInputRepository.calculateTotalCostByField(1L)).thenReturn(null);

        Map<String, Object> summary = soilWaterService.getFieldSoilWaterSummary(1L);
        
        assertEquals(0, summary.get("totalSoilTests"));
        assertNull(summary.get("latestSoilRecord"));
        assertEquals(0, summary.get("totalWaterTests"));
        assertNull(summary.get("latestWaterRecord"));
        assertEquals(0, summary.get("totalInputs"));
    }

    @Test
    void getSoilPHTrendData_FiltersNulls() {
        SoilRecord r1 = new SoilRecord();
        r1.setTestDate(LocalDate.now());
        r1.setPH(6.0);

        SoilRecord r2 = new SoilRecord();
        r2.setTestDate(null); // Should filter out
        r2.setPH(7.0);

        SoilRecord r3 = new SoilRecord();
        r3.setTestDate(LocalDate.now());
        r3.setPH(null); // Should filter out

        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Arrays.asList(r1, r2, r3));

        List<Map<String, Object>> trend = soilWaterService.getSoilPHTrendData(1L);

        assertEquals(1, trend.size());
        assertEquals(6.0, trend.get(0).get("pH"));
    }

    @Test
    void getLatestNutrientLevels_NullValuesDefaultToZero() {
        SoilRecord r1 = new SoilRecord();
        r1.setNitrogen(null);
        r1.setPhosphorus(null);
        r1.setPotassium(null);

        when(soilRecordRepository.findByFieldIdOrderByTestDateDesc(1L)).thenReturn(Collections.singletonList(r1));

        Map<String, Double> nutrients = soilWaterService.getLatestNutrientLevels(1L);

        assertEquals(0.0, nutrients.get("nitrogen"));
        assertEquals(0.0, nutrients.get("phosphorus"));
        assertEquals(0.0, nutrients.get("potassium"));
    }
}
