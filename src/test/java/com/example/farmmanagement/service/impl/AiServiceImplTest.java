package com.example.farmmanagement.service.impl;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.model.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AiServiceImplTest {

    private AiServiceImpl aiService;

    @BeforeEach
    public void setUp() {
        aiService = new AiServiceImpl();
    }

    @Test
    public void getFarmSummary_ShouldReturnNonEmptyString() {
        String summary = aiService.getFarmSummary();
        assertThat(summary).isNotNull();
        assertThat(summary).isNotEmpty();
    }

    @Test
    public void getCropInsights_WithNullCrop_ShouldReturnDefaultMessage() {
        String insight = aiService.getCropInsights(null);
        assertThat(insight).isEqualTo("No crop data available for analysis.");
    }

    @Test
    public void getCropInsights_WithValidCrop_ShouldReturnInsight() {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setType("Corn");
        crop.setField(new Field());

        String insight = aiService.getCropInsights(crop);
        assertThat(insight).isNotNull();
        assertThat(insight).isNotEmpty();
        assertThat(insight).isNotEqualTo("No crop data available for analysis.");
    }
}
