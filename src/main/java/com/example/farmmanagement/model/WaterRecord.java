package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "water_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate testDate;

    // Water quality metrics
    private Double pH;
    private Double salinity; // ppt (parts per thousand)
    private Double turbidity; // NTU (Nephelometric Turbidity Units)
    private String source; // e.g., "Well", "River", "Municipal"
    private Double quantity; // Liters or gallons

    @Column(columnDefinition = "TEXT")
    private String notes;
}
