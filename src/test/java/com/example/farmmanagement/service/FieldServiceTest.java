package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.repository.FieldRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    @Mock
    private FieldRepository fieldRepository;

    @InjectMocks
    private FieldService fieldService;

    @Test
    void getAllFields_ShouldReturnAllFields() {
        // Given
        Field field1 = createField(1L, "Field A", 10.0, null);
        Field field2 = createField(2L, "Field B", 5.0, null);
        when(fieldRepository.findAll()).thenReturn(Arrays.asList(field1, field2));

        // When
        List<Field> result = fieldService.getAllFields();

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    void getMasterFarms_ShouldReturnOnlyParentFields() {
        // Given
        Field masterFarm = createField(1L, "Master Farm", 20.0, null);
        when(fieldRepository.findByParentFieldIsNull()).thenReturn(Collections.singletonList(masterFarm));

        // When
        List<Field> result = fieldService.getMasterFarms();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Master Farm");
        verify(fieldRepository).findByParentFieldIsNull();
    }

    @Test
    void getSubdivisions_ShouldReturnSubdivisions() {
        // Given
        Field subdivision = createField(2L, "Subdivision", 5.0, 1L);
        when(fieldRepository.findByParentFieldId(1L)).thenReturn(Collections.singletonList(subdivision));

        // When
        List<Field> result = fieldService.getSubdivisions(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Subdivision");
    }

    @Test
    void saveField_ShouldSaveAndReturn() {
        // Given
        Field field = createField(null, "New Field", 8.0, null);
        when(fieldRepository.save(any(Field.class))).thenReturn(field);

        // When
        Field result = fieldService.saveField(field);

        // Then
        assertThat(result.getName()).isEqualTo("New Field");
        verify(fieldRepository).save(field);
    }

    @Test
    void getFieldById_ShouldReturnField() {
        // Given
        Field field = createField(1L, "Test Field", 10.0, null);
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));

        // When
        Optional<Field> result = fieldService.getFieldById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Field");
    }

    @Test
    void calculateTotalLandArea_ShouldSumAllAreas() {
        // Given
        Field field1 = createField(1L, "Field 1", 10.0, null);
        Field field2 = createField(2L, "Field 2", 15.0, null);
        Field field3 = createField(3L, "Field 3", 5.0, null);
        when(fieldRepository.findAll()).thenReturn(Arrays.asList(field1, field2, field3));

        // When
        Double result = fieldService.calculateTotalLandArea();

        // Then
        assertThat(result).isEqualTo(30.0);
    }

    @Test
    void calculateSubdivisionArea_ShouldSumSubdivisions() {
        // Given
        Field sub1 = createField(2L, "Sub 1", 3.0, 1L);
        Field sub2 = createField(3L, "Sub 2", 5.0, 1L);
        when(fieldRepository.findByParentFieldId(1L)).thenReturn(Arrays.asList(sub1, sub2));

        // When
        Double result = fieldService.calculateSubdivisionArea(1L);

        // Then
        assertThat(result).isEqualTo(8.0);
    }

    @Test
    void canAddSubdivision_WhenSpaceAvailable_ShouldReturnTrue() {
        // Given
        Field masterFarm = createField(1L, "Master", 20.0, null);
        Field existingSub = createField(2L, "Existing", 8.0, 1L);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(masterFarm));
        when(fieldRepository.findByParentFieldId(1L)).thenReturn(Collections.singletonList(existingSub));

        // When
        boolean result = fieldService.canAddSubdivision(1L, 10.0);

        // Then
        assertThat(result).isTrue(); // 8 + 10 = 18, which is <= 20
    }

    @Test
    void canAddSubdivision_WhenNoSpaceAvailable_ShouldReturnFalse() {
        // Given
        Field masterFarm = createField(1L, "Master", 20.0, null);
        Field existingSub = createField(2L, "Existing", 15.0, 1L);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(masterFarm));
        when(fieldRepository.findByParentFieldId(1L)).thenReturn(Collections.singletonList(existingSub));

        // When
        boolean result = fieldService.canAddSubdivision(1L, 10.0);

        // Then
        assertThat(result).isFalse(); // 15 + 10 = 25, which is > 20
    }

    @Test
    void canAddSubdivision_WhenMasterFarmNotFound_ShouldReturnFalse() {
        // Given
        when(fieldRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = fieldService.canAddSubdivision(999L, 5.0);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void getRemainingSpace_ShouldCalculateCorrectly() {
        // Given
        Field masterFarm = createField(1L, "Master", 100.0, null);
        Field sub1 = createField(2L, "Sub 1", 30.0, 1L);
        Field sub2 = createField(3L, "Sub 2", 25.0, 1L);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(masterFarm));
        when(fieldRepository.findByParentFieldId(1L)).thenReturn(Arrays.asList(sub1, sub2));

        // When
        Double result = fieldService.getRemainingSpace(1L);

        // Then
        assertThat(result).isEqualTo(45.0); // 100 - (30 + 25) = 45
    }

    @Test
    void getRemainingSpace_WhenFieldNotFound_ShouldReturnZero() {
        // Given
        when(fieldRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Double result = fieldService.getRemainingSpace(999L);

        // Then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void getFieldMetrics_ShouldReturnMetrics() {
        // When
        Map<String, Object> result = fieldService.getFieldMetrics();

        // Then
        assertThat(result).containsKeys("phLevel", "temperature", "waterLevel");
        assertThat(result.get("phLevel")).isEqualTo(5);
    }

    private Field createField(Long id, String name, Double areaSize, Long parentId) {
        Field field = new Field();
        field.setId(id);
        field.setName(name);
        field.setAreaSize(areaSize);
        if (parentId != null) {
            Field parent = new Field();
            parent.setId(parentId);
            field.setParentField(parent);
        }
        return field;
    }
}
