package com.example.farmmanagement.service;

import com.example.farmmanagement.model.Crop;

public interface AiService {
    String getFarmSummary();

    String getCropInsights(Crop crop);
}
