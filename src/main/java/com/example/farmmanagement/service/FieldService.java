package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FieldService {

    private final FieldRepository fieldRepository;

    @Autowired
    public FieldService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    public Field saveField(Field field) {
        return fieldRepository.save(field);
    }

    public Optional<Field> getFieldById(Long id) {
        return fieldRepository.findById(id);
    }
}
