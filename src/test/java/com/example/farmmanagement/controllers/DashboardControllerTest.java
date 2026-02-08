package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.Animal;
import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.service.AiService;
import com.example.farmmanagement.service.AnimalService;
import com.example.farmmanagement.service.CropService;
import com.example.farmmanagement.service.CropTransactionService;
import com.example.farmmanagement.service.CropActivityService;
import com.example.farmmanagement.service.FieldService;
import com.example.farmmanagement.service.SoilWaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CropService cropService;

    @MockBean
    private AnimalService animalService;

    @MockBean
    private FieldService fieldService;

    @MockBean
    private AiService aiService;

    @MockBean
    private CropTransactionService transactionService;

    @MockBean
    private CropActivityService activityService;

    @MockBean
    private SoilWaterService soilWaterService;

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void showHomePage_ShouldReturnIndexView() throws Exception {
        given(cropService.countCrops()).willReturn(10L);
        given(animalService.countAnimals()).willReturn(5L);
        given(aiService.getFarmSummary()).willReturn("Farm is good.");
        given(transactionService.calculateGlobalTotalExpenses()).willReturn(100.0);
        given(transactionService.calculateGlobalTotalIncome()).willReturn(200.0);
        given(cropService.getCropTypeDistribution()).willReturn(Collections.emptyMap());

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("totalCrops", "totalAnimals", "farmInsights", "globalExpense",
                        "globalIncome", "cropDistribution"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void showCropManagementPage_ShouldReturnCropView() throws Exception {
        given(cropService.getAllCrops()).willReturn(Arrays.asList(new Crop(), new Crop()));
        given(fieldService.getAllFields()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/crop"))
                .andExpect(status().isOk())
                .andExpect(view().name("crop-management"))
                .andExpect(model().attributeExists("crops", "fields", "newCrop"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addCrop_ShouldRedirectToCropPage() throws Exception {
        mockMvc.perform(post("/crop/add")
                .param("type", "Corn")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/crop"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void showAnimalManagementPage_ShouldReturnAnimalView() throws Exception {
        given(animalService.getAllAnimals()).willReturn(Arrays.asList(new Animal()));

        mockMvc.perform(get("/animal"))
                .andExpect(status().isOk())
                .andExpect(view().name("animal-management"))
                .andExpect(model().attributeExists("animals", "newAnimal"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addAnimal_ShouldRedirectToAnimalPage() throws Exception {
        mockMvc.perform(post("/animal/add")
                .param("species", "Cow")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animal"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void showFieldManagementPage_ShouldReturnFieldView() throws Exception {
        given(fieldService.getAllFields()).willReturn(Arrays.asList(new Field()));

        mockMvc.perform(get("/field"))
                .andExpect(status().isOk())
                .andExpect(view().name("field-management"))
                .andExpect(model().attributeExists("fields", "newField"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addField_ShouldRedirectToFieldPage() throws Exception {
        mockMvc.perform(post("/field/add")
                .param("name", "North Field")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/field"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void showProduceManagementPage_ShouldReturnManagementView() throws Exception {
        mockMvc.perform(get("/produce"))
                .andExpect(status().isOk())
                .andExpect(view().name("management-page"))
                .andExpect(model().attribute("pageTitle", "Produce Management"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void viewCropDetails_WithValidId_ShouldReturnCropDetailsView() throws Exception {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setType("Wheat");

        given(cropService.getCropById(1L)).willReturn(java.util.Optional.of(crop));
        given(transactionService.getTransactionsByCropId(1L)).willReturn(Collections.emptyList());
        given(transactionService.calculateTotalExpenses(1L)).willReturn(50.0);
        given(transactionService.calculateTotalIncome(1L)).willReturn(150.0);
        given(activityService.getActivitiesByCropId(1L)).willReturn(Collections.emptyList());
        given(aiService.getCropInsights(crop)).willReturn("Crop is healthy");

        mockMvc.perform(get("/crop/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("crop-details"))
                .andExpect(model().attributeExists("crop", "transactions", "totalExpense", "totalIncome", "activities",
                        "cropInsights"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void viewCropDetails_WithInvalidId_ShouldRedirectToCropPage() throws Exception {
        given(cropService.getCropById(999L)).willReturn(java.util.Optional.empty());

        mockMvc.perform(get("/crop/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/crop"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addTransaction_ShouldSaveAndRedirectToCropDetails() throws Exception {
        Crop crop = new Crop();
        crop.setId(1L);
        given(cropService.getCropById(1L)).willReturn(java.util.Optional.of(crop));

        mockMvc.perform(post("/crop/1/transaction/add")
                .param("amount", "100")
                .param("type", "EXPENSE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/crop/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addActivity_ShouldSaveAndRedirectToCropDetails() throws Exception {
        Crop crop = new Crop();
        crop.setId(1L);
        given(cropService.getCropById(1L)).willReturn(java.util.Optional.of(crop));

        mockMvc.perform(post("/crop/1/activity/add")
                .param("description", "Watering")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/crop/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void viewFieldDetails_WithValidId_ShouldReturnFieldDetailsView() throws Exception {
        Field field = new Field();
        field.setId(1L);
        field.setName("North Field");

        given(fieldService.getFieldById(1L)).willReturn(java.util.Optional.of(field));
        given(soilWaterService.getSoilRecordsByField(1L)).willReturn(Collections.emptyList());
        given(soilWaterService.getWaterRecordsByField(1L)).willReturn(Collections.emptyList());
        given(soilWaterService.getSoilInputsByField(1L)).willReturn(Collections.emptyList());
        given(soilWaterService.getFieldSoilWaterSummary(1L)).willReturn(Collections.emptyMap());
        given(soilWaterService.getSoilPHTrendData(1L)).willReturn(Collections.emptyList());
        given(soilWaterService.getLatestNutrientLevels(1L)).willReturn(Collections.emptyMap());

        mockMvc.perform(get("/field/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("field-details"))
                .andExpect(model().attributeExists("field", "soilRecords", "waterRecords", "soilInputs"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void viewFieldDetails_WithInvalidId_ShouldRedirectToFieldPage() throws Exception {
        given(fieldService.getFieldById(999L)).willReturn(java.util.Optional.empty());

        mockMvc.perform(get("/field/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/field"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addSoilRecord_ShouldSaveAndRedirectToFieldDetails() throws Exception {
        Field field = new Field();
        field.setId(1L);
        given(fieldService.getFieldById(1L)).willReturn(java.util.Optional.of(field));

        mockMvc.perform(post("/field/1/soil-record/add")
                .param("ph", "6.5")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/field/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addWaterRecord_ShouldSaveAndRedirectToFieldDetails() throws Exception {
        Field field = new Field();
        field.setId(1L);
        given(fieldService.getFieldById(1L)).willReturn(java.util.Optional.of(field));

        mockMvc.perform(post("/field/1/water-record/add")
                .param("amount", "50")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/field/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void addSoilInput_ShouldSaveAndRedirectToFieldDetails() throws Exception {
        Field field = new Field();
        field.setId(1L);
        given(fieldService.getFieldById(1L)).willReturn(java.util.Optional.of(field));

        mockMvc.perform(post("/field/1/soil-input/add")
                .param("type", "Fertilizer")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/field/1"));
    }
}
