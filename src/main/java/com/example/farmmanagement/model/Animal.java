package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "animals")
@Data // Generates Getters, Setters, ToString, EqualsAndHashCode,
      // RequiredArgsConstructor
@NoArgsConstructor // Generates a no-args constructor (Required for JPA/Spring)
@AllArgsConstructor // Generates a constructor with all arguments
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String uniqueID; // uniqueID is currently a String. If auto-increment (IDENTITY) failure occurs,
                             // consider changing to Long or UUID.

    private String name;
    private String speciesBreed;
    private String sex;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    private String sourceOrigin;
    private String vaccinationHistory;
    private String deworming;
    private String illnessesInjuries;
    private String medicationAdministration;
    private String veterinaryVisits;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate matingDate;

    private String sireId;
    private String damId;
    private String pregnancyStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate estimatedDueDate;

    private String birthingInfo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate weaningDate;

    private String milkProduction;
    private Double eggProduction;
    private String feedType;
    private String feedingSchedule;
    private String waterIntake;
    private String dietChanges;
}
