package com.example.farmmanagement.service;

import com.example.farmmanagement.model.CropActivity;
import com.example.farmmanagement.repository.CropActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropActivityServiceTest {

    @Mock
    private CropActivityRepository activityRepository;

    @InjectMocks
    private CropActivityService cropActivityService;

    private CropActivity activityFuture;
    private CropActivity activityPast;

    @BeforeEach
    void setUp() {
        activityFuture = new CropActivity();
        activityFuture.setId(1L);
        activityFuture.setActivityDate(LocalDate.now().plusDays(5));
        activityFuture.setActivityType("Harvest");

        activityPast = new CropActivity();
        activityPast.setId(2L);
        activityPast.setActivityDate(LocalDate.now().minusDays(5));
        activityPast.setActivityType("Planting");
    }

    @Test
    void saveActivity() {
        when(activityRepository.save(any(CropActivity.class))).thenReturn(activityFuture);
        CropActivity saved = cropActivityService.saveActivity(activityFuture);
        assertNotNull(saved);
        assertEquals("Harvest", saved.getActivityType());
    }

    @Test
    void getActivitiesByCropId() {
        when(activityRepository.findByCropId(1L)).thenReturn(Collections.singletonList(activityFuture));
        List<CropActivity> result = cropActivityService.getActivitiesByCropId(1L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUpcomingTasks() {
        when(activityRepository.findAll()).thenReturn(Arrays.asList(activityFuture, activityPast));
        List<CropActivity> upcoming = cropActivityService.getUpcomingTasks();
        
        assertEquals(1, upcoming.size());
        assertEquals(activityFuture.getId(), upcoming.get(0).getId());
    }

    @Test
    void getUpcomingTasks_Empty() {
        when(activityRepository.findAll()).thenReturn(Collections.singletonList(activityPast));
        List<CropActivity> upcoming = cropActivityService.getUpcomingTasks();
        assertTrue(upcoming.isEmpty());
    }
}
