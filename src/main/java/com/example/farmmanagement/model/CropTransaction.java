package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "crop_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // EXPENSE or INCOME

    private Double amount;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate transactionDate;

    public enum TransactionType {
        EXPENSE, INCOME
    }
}
