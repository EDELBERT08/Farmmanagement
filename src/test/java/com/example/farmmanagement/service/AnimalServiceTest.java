package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Animal;
import com.example.farmmanagement.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void getAllAnimals_ShouldReturnAllAnimals() {
        // Given
        Animal animal1 = new Animal();
        animal1.setId(1L);
        animal1.setName("Bessie");
        animal1.setSpeciesBreed("Cow");

        Animal animal2 = new Animal();
        animal2.setId(2L);
        animal2.setName("Clucky");
        animal2.setSpeciesBreed("Chicken");

        when(animalRepository.findAll()).thenReturn(Arrays.asList(animal1, animal2));

        // When
        List<Animal> result = animalService.getAllAnimals();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSpeciesBreed()).isEqualTo("Cow");
        verify(animalRepository).findAll();
    }

    @Test
    void getAnimalById_WhenExists_ShouldReturnAnimal() {
        // Given
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setName("Billy");
        animal.setSpeciesBreed("Goat");
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        // When
        Optional<Animal> result = animalService.getAnimalById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSpeciesBreed()).isEqualTo("Goat");
        verify(animalRepository).findById(1L);
    }

    @Test
    void getAnimalById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(animalRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Animal> result = animalService.getAnimalById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(animalRepository).findById(999L);
    }

    @Test
    void saveAnimal_ShouldSaveAndReturnAnimal() {
        // Given
        Animal animal = new Animal();
        animal.setName("Dolly");
        animal.setSpeciesBreed("Sheep");
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        // When
        Animal result = animalService.saveAnimal(animal);

        // Then
        assertThat(result.getSpeciesBreed()).isEqualTo("Sheep");
        verify(animalRepository).save(animal);
    }

    @Test
    void deleteAnimal_ShouldCallRepository() {
        // When
        animalService.deleteAnimal(1L);

        // Then
        verify(animalRepository).deleteById(1L);
    }

    @Test
    void countAnimals_ShouldReturnCount() {
        // Given
        when(animalRepository.count()).thenReturn(42L);

        // When
        long result = animalService.countAnimals();

        // Then
        assertThat(result).isEqualTo(42L);
        verify(animalRepository).count();
    }
}
