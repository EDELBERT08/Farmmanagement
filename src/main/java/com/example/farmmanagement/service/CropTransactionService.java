package com.example.farmmanagement.service;

import com.example.farmmanagement.model.CropTransaction;
import com.example.farmmanagement.repository.CropTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropTransactionService {

    private final CropTransactionRepository transactionRepository;

    @Autowired
    public CropTransactionService(CropTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CropTransaction saveTransaction(CropTransaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<CropTransaction> getTransactionsByCropId(Long cropId) {
        return transactionRepository.findByCropId(cropId);
    }

    public Double calculateTotalExpenses(Long cropId) {
        return getTransactionsByCropId(cropId).stream()
                .filter(t -> t.getTransactionType() == CropTransaction.TransactionType.EXPENSE)
                .mapToDouble(CropTransaction::getAmount)
                .sum();
    }

    public Double calculateTotalIncome(Long cropId) {
        return getTransactionsByCropId(cropId).stream()
                .filter(t -> t.getTransactionType() == CropTransaction.TransactionType.INCOME)
                .mapToDouble(CropTransaction::getAmount)
                .sum();
    }

    public Double calculateGlobalTotalExpenses() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getTransactionType() == CropTransaction.TransactionType.EXPENSE)
                .mapToDouble(CropTransaction::getAmount)
                .sum();
    }

    public Double calculateGlobalTotalIncome() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getTransactionType() == CropTransaction.TransactionType.INCOME)
                .mapToDouble(CropTransaction::getAmount)
                .sum();
    }
}
