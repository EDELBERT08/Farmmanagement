package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import com.example.farmmanagement.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin")
    void showWeatherPage_WithUserLocation_ShouldIncludeUserLocation() throws Exception {
        // Given
        User user = createUserWithLocation("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attributeExists("userLocation"))
                .andExpect(model().attribute("pageTitle", "Weather"))
                .andExpect(model().attribute("activePage", "weather"));

        verify(userRepository).findByUsername("admin");
    }

    @Test
    @WithMockUser(username = "testuser")
    void showWeatherPage_WithoutUserLocation_ShouldNotIncludeUserLocation() throws Exception {
        // Given
        User user = createUserWithoutLocation("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attributeDoesNotExist("userLocation"))
                .andExpect(model().attribute("pageTitle", "Weather"));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @WithMockUser(username = "admin")
    void showWeatherPage_UserNotFound_ShouldStillRenderPage() throws Exception {
        // Given
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attributeDoesNotExist("userLocation"));
    }

    @Test
    @WithMockUser
    void getWeather_ShouldReturnWeatherData() throws Exception {
        // Given
        Map<String, Object> weatherData = createMockWeatherData();
        when(weatherService.getWeather(anyDouble(), anyDouble())).thenReturn(weatherData);

        // When & Then
        mockMvc.perform(get("/api/weather")
                .param("lat", "-1.286389")
                .param("lon", "36.817223"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temp").value(25.0))
                .andExpect(jsonPath("$.humidity").value(60))
                .andExpect(jsonPath("$.weatherMain").value("Clouds"));

        verify(weatherService).getWeather(-1.286389, 36.817223);
    }

    @Test
    @WithMockUser
    void getWeather_WithDifferentCoordinates_ShouldReturnWeatherData() throws Exception {
        // Given
        Map<String, Object> weatherData = createMockWeatherData();
        when(weatherService.getWeather(anyDouble(), anyDouble())).thenReturn(weatherData);

        // When & Then
        mockMvc.perform(get("/api/weather")
                .param("lat", "-4.043740")
                .param("lon", "39.668205"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temp").exists())
                .andExpect(jsonPath("$.humidity").exists());

        verify(weatherService).getWeather(-4.043740, 39.668205);
    }

    private User createUserWithLocation(String username) {
        User user = new User();
        user.setUsername(username);
        user.setCity("Nairobi");
        user.setLatitude(-1.286389);
        user.setLongitude(36.817223);
        return user;
    }

    private User createUserWithoutLocation(String username) {
        User user = new User();
        user.setUsername(username);
        // No latitude/longitude set
        return user;
    }

    private Map<String, Object> createMockWeatherData() {
        Map<String, Object> data = new HashMap<>();
        data.put("temp", 25.0);
        data.put("humidity", 60);
        data.put("windSpeed", 10.0);
        data.put("weatherMain", "Clouds");
        data.put("weatherDescription", "few clouds");
        data.put("sunrise", "06:00");
        data.put("sunset", "18:30");
        return data;
    }
}
