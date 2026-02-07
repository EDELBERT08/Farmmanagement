package com.example.farmmanagement.service;

import com.example.farmmanagement.model.CropActivity;
import com.example.farmmanagement.repository.CropActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropActivityService {

    private final CropActivityRepository activityRepository;

    @Autowired
    public CropActivityService(CropActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public CropActivity saveActivity(CropActivity activity) {
        return activityRepository.save(activity);
    }

    public List<CropActivity> getActivitiesByCropId(Long cropId) {
        return activityRepository.findByCropId(cropId);
    }
}
