package com.example.farmmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location; // Description or coordinates
    private Double areaSize; // In acres

    // One field can have multiple crops over time, or currently
    @OneToMany(mappedBy = "field")
    @ToString.Exclude
    private List<Crop> crops;
}
