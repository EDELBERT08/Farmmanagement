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
}
