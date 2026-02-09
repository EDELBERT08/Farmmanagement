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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin")
    void showWeatherPage() throws Exception {
        User user = new User();
        user.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    @Test
    @WithMockUser
    void getWeatherApi() throws Exception {
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("temp", 25.0);
        when(weatherService.getWeather(anyDouble(), anyDouble())).thenReturn(mockData);

        mockMvc.perform(get("/api/weather")
                .param("lat", "-1.2")
                .param("lon", "36.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temp").value(25.0));
    }

    @Test
    void showWeatherPageUnauthenticated() throws Exception {
        mockMvc.perform(get("/weather"))
                .andExpect(status().is3xxRedirection()) // Spring Security redirects to login
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "userNoLoc")
    void showWeatherPageNoLocation() throws Exception {
        User user = new User();
        user.setUsername("userNoLoc");
        // Lat/Lon are null by default
        when(userRepository.findByUsername("userNoLoc")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attributeDoesNotExist("userLocation"));
    }
}
