package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.model.CropTransaction;
import com.example.farmmanagement.repository.CropTransactionRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropTransactionServiceTest {

    @Mock
    private CropTransactionRepository transactionRepository;

    @InjectMocks
    private CropTransactionService cropTransactionService;

    private Crop crop;
    private CropTransaction incomeTransaction;
    private CropTransaction expenseTransaction;

    @BeforeEach
    void setUp() {
        crop = new Crop();
        crop.setId(1L);

        incomeTransaction = new CropTransaction();
        incomeTransaction.setId(1L);
        incomeTransaction.setCrop(crop);
        incomeTransaction.setTransactionType(CropTransaction.TransactionType.INCOME);
        incomeTransaction.setAmount(1000.0);
        incomeTransaction.setTransactionDate(LocalDate.now());

        expenseTransaction = new CropTransaction();
        expenseTransaction.setId(2L);
        expenseTransaction.setCrop(crop);
        expenseTransaction.setTransactionType(CropTransaction.TransactionType.EXPENSE);
        expenseTransaction.setAmount(500.0);
        expenseTransaction.setTransactionDate(LocalDate.now());
    }

    @Test
    void saveTransaction() {
        when(transactionRepository.save(any(CropTransaction.class))).thenReturn(incomeTransaction);
        CropTransaction saved = cropTransactionService.saveTransaction(incomeTransaction);
        assertNotNull(saved);
        assertEquals(1000.0, saved.getAmount());
    }

    @Test
    void getTransactionsByCropId() {
        when(transactionRepository.findByCropId(1L)).thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));
        List<CropTransaction> result = cropTransactionService.getTransactionsByCropId(1L);
        assertEquals(2, result.size());
    }

    @Test
    void calculateTotalIncome() {
        when(transactionRepository.findByCropId(1L)).thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));
        Double income = cropTransactionService.calculateTotalIncome(1L);
        assertEquals(1000.0, income);
    }

    @Test
    void calculateTotalExpenses() {
        when(transactionRepository.findByCropId(1L)).thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));
        Double expenses = cropTransactionService.calculateTotalExpenses(1L);
        assertEquals(500.0, expenses);
    }

    @Test
    void calculateGlobalTotalIncome() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));
        Double income = cropTransactionService.calculateGlobalTotalIncome();
        assertEquals(1000.0, income);
    }

    @Test
    void calculateGlobalTotalExpenses() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));
        Double expenses = cropTransactionService.calculateGlobalTotalExpenses();
        assertEquals(500.0, expenses);
    }
}
