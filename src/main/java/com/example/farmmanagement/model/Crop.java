// src/main/java/com/example/farmmanagement/model/Crop.java
package com.example.farmmanagement.model; // Adjust package if needed

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat; // Important for date binding

@Entity // Marks this class as a JPA entity, representing a database table
@Getter
@Setter
@ToString
@Table(name = "crop") // Specifies the name of the database table (optional, defaults to class name)
public class Crop {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures how the primary key is generated (auto-increment
                                                        // in most cases)
    private Long id; // Add a primary key field

    private String type;
    private Double landSize; // Use Double for potential decimals in hectares

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Helps Spring parse the date from the form
    private LocalDate plantingDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate harvestDate;

    private String pesticides;

    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;

    // --- Constructors ---
    public Crop() {
    }

    public Crop(String type, Double landSize, LocalDate plantingDate, LocalDate harvestDate, String pesticides) {
        this.type = type;
        this.landSize = landSize;
        this.plantingDate = plantingDate;
        this.harvestDate = harvestDate;
        this.pesticides = pesticides;
    }

}