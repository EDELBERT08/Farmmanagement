package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "soil_inputs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoilInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private InputType inputType;

    private String productName;
    private Double quantity;
    private String unit; // e.g., "kg", "liters", "bags"
    private String applicationMethod; // e.g., "Broadcast", "Drip", "Foliar"
    private Double cost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum InputType {
        FERTILIZER,
        MANURE,
        AMENDMENT // Lime, gypsum, etc.
    }
}
