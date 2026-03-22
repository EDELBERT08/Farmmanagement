package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "soil_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoilRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate testDate;

    // Soil composition metrics
    private Double pH;
    private Double nitrogen; // N percentage
    private Double phosphorus; // P percentage
    private Double potassium; // K percentage
    private Double organicMatter; // Percentage
    private String texture; // e.g., "Sandy loam", "Clay"

    @Column(columnDefinition = "TEXT")
    private String notes;
}
