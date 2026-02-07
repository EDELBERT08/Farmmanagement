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

    // Geolocation fields for mapping
    private Double latitude;
    private Double longitude;

    // Boundary coordinates for polygon mapping (stored as JSON string)
    @Column(columnDefinition = "TEXT")
    private String boundaryCoordinates;

    // One field can have multiple crops over time, or currently
    @OneToMany(mappedBy = "field")
    @ToString.Exclude
    private List<Crop> crops;

    // Relationships to soil and water records
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SoilRecord> soilRecords;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<WaterRecord> waterRecords;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SoilInput> soilInputs;

    // Farm subdivision hierarchy
    @ManyToOne
    @JoinColumn(name = "parent_field_id")
    @ToString.Exclude
    private Field parentField;

    @OneToMany(mappedBy = "parentField", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Field> subdivisions;

    // Flag to identify master farms vs subdivisions
    private Boolean isMasterFarm = false;
}
