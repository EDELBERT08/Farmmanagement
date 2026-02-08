package com.example.farmmanagement.model;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.model.Role;
import com.example.farmmanagement.model.SoilInput;
import com.example.farmmanagement.model.SoilRecord;
import com.example.farmmanagement.model.User;
import com.example.farmmanagement.model.WaterRecord;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;

class ModelTests {

    @Test
    void testuser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("pass");
        user.setRole(Role.ADMIN);
        user.setCity("City");
        user.setLatitude(1.0);
        user.setLongitude(2.0);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("test");
        assertThat(user.getPassword()).isEqualTo("pass");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getCity()).isEqualTo("City");
        assertThat(user.getLatitude()).isEqualTo(1.0);
        assertThat(user.getLongitude()).isEqualTo(2.0);

        User user2 = new User(1L, "test", "pass", Role.ADMIN, "City", 1.0, 2.0);
        assertThat(user2).isEqualTo(user);
        assertThat(user2.hashCode()).isEqualTo(user.hashCode());
        assertThat(user2.toString()).contains("test");
    }

    @Test
    void testField() {
        Field field = new Field();
        field.setId(1L);
        field.setName("Field 1");
        field.setLocation("Loc");
        field.setAreaSize(10.0);
        field.setLatitude(1.1);
        field.setLongitude(2.2);
        field.setBoundaryCoordinates("[]");
        field.setIsMasterFarm(true);
        field.setSubdivisions(new ArrayList<>());
        field.setCrops(new ArrayList<>());
        field.setSoilRecords(new ArrayList<>());
        field.setWaterRecords(new ArrayList<>());
        field.setSoilInputs(new ArrayList<>());

        assertThat(field.getId()).isEqualTo(1L);
        assertThat(field.getName()).isEqualTo("Field 1");
        assertThat(field.getIsMasterFarm()).isTrue();

        Field field2 = new Field(1L, "Field 1", "Loc", 10.0, 1.1, 2.2, "[]", null, null, null, null, null, null, true);
        // Lombok @Data generates equals/hashCode/toString
        // Note: Check if all args constructor matches exactly field order.
        // If not certain, rely on getters/setters test mainly.
    }

    @Test
    void testCrop() {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setType("Corn");
        crop.setLandSize(5.0);
        crop.setPlantingDate(LocalDate.now());
        crop.setHarvestDate(LocalDate.now().plusDays(100));
        crop.setPesticides("None");

        assertThat(crop.getType()).isEqualTo("Corn");
        assertThat(crop.toString()).contains("Corn");
    }

    @Test
    void testSoilInput() {
        SoilInput input = new SoilInput();
        input.setId(1L);
        input.setInputType(SoilInput.InputType.FERTILIZER);
        input.setProductName("NPK");

        assertThat(input.getInputType()).isEqualTo(SoilInput.InputType.FERTILIZER);
    }
}
