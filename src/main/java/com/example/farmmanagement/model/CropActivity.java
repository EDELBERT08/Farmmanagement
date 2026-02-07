package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "crop_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    private String activityType; // e.g., "Spraying", "Fertilizing"
    private String details; // e.g., "Used NPK 17:17:17"

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate activityDate;
}
