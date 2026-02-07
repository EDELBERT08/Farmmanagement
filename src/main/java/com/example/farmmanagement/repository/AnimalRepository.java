package com.example.farmmanagement.repository;

import com.example.farmmanagement.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, String> {
}
