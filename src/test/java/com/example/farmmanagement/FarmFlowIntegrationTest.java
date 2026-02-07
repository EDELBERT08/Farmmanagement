package com.example.farmmanagement;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.model.CropActivity;
import com.example.farmmanagement.model.CropTransaction;
import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.repository.CropActivityRepository;
import com.example.farmmanagement.repository.CropRepository;
import com.example.farmmanagement.repository.CropTransactionRepository;
import com.example.farmmanagement.repository.FieldRepository;
import com.example.farmmanagement.service.CropTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.transaction.annotation.Transactional
public class FarmFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CropTransactionRepository transactionRepository;

    @Autowired
    private CropActivityRepository activityRepository;

    @Autowired
    private CropTransactionService transactionService;

    @Test
    public void testFullFarmManagementFlow() throws Exception {
        // 1. Create a Field
        Field field = new Field();
        String uniqueFieldName = "Test Acre " + System.currentTimeMillis();
        field.setName(uniqueFieldName);
        field.setAreaSize(10.0);
        field.setLocation("North Side");
        Field savedField = fieldRepository.save(field);

        assertThat(savedField.getId()).isNotNull();
        assertThat(fieldRepository.findById(savedField.getId())).isPresent();

        // 2. Add a Crop assigned to that Field
        Crop crop = new Crop();
        crop.setType("Corn Test");
        crop.setField(savedField);
        crop.setLandSize(5.0);
        crop.setPlantingDate(LocalDate.now().minusDays(30));
        Crop savedCrop = cropRepository.save(crop);

        assertThat(savedCrop.getId()).isNotNull();
        assertThat(cropRepository.findById(savedCrop.getId())).isPresent();

        // 3. Log an Activity (e.g., Planting)
        CropActivity activity = new CropActivity();
        activity.setCrop(savedCrop);
        activity.setActivityType("Planting");
        activity.setActivityDate(LocalDate.now().minusDays(30));
        activity.setDetails("Initial planting");
        activityRepository.save(activity);

        assertThat(activityRepository.findByCropId(savedCrop.getId())).hasSizeGreaterThanOrEqualTo(1);

        // 4. Add Financial Transactions (Expense & Income)
        CropTransaction expense = new CropTransaction();
        expense.setCrop(savedCrop);
        expense.setTransactionType(CropTransaction.TransactionType.EXPENSE);
        expense.setAmount(500.0); // Spent $500
        expense.setTransactionDate(LocalDate.now().minusDays(25));
        transactionRepository.save(expense);

        CropTransaction income = new CropTransaction();
        income.setCrop(savedCrop);
        income.setTransactionType(CropTransaction.TransactionType.INCOME);
        income.setAmount(1200.0); // Earned $1200
        income.setTransactionDate(LocalDate.now());
        transactionRepository.save(income);

        // Verify Calculations (Specific to this crop)
        Double cropExpense = transactionService.calculateTotalExpenses(savedCrop.getId());
        Double cropIncome = transactionService.calculateTotalIncome(savedCrop.getId());

        assertThat(cropExpense).isEqualTo(500.0);
        assertThat(cropIncome).isEqualTo(1200.0);

        // 5. Verify Dashboard Analytics via Controller Endpoint
        // We verify that the page loads and contains our new data (indirectly)
        // Since global totals depend on existing data, we just check for HTTP 200 and
        // model attributes presence
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("totalCrops"))
                .andExpect(model().attributeExists("globalExpense"))
                .andExpect(model().attributeExists("globalIncome"))
                .andExpect(model().attributeExists("globalProfit"));
    }
}
