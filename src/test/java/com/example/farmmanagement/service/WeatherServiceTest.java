package com.example.farmmanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // Inject RestTemplate mock
        ReflectionTestUtils.setField(weatherService, "restTemplate", restTemplate);
    }

    @Test
    void getWeather_WithNoApiKey_ShouldReturnMockWeather() {
        // Given
        ReflectionTestUtils.setField(weatherService, "apiKey", "demo");
        double lat = -1.286389;
        double lon = 36.817223;

        // When
        Map<String, Object> result = weatherService.getWeather(lat, lon);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("temp")).isEqualTo(25.0);
        assertThat(result.get("humidity")).isEqualTo(60);
        assertThat(result.get("weatherMain")).isEqualTo("Clouds");
        assertThat(result.get("weatherDescription")).isEqualTo("few clouds");
        verify(restTemplate, never()).getForEntity(anyString(), any(), anyDouble(), anyDouble(), anyString());
    }

    @Test
    void getWeather_WithValidApiKey_ShouldReturnProcessedWeather() {
        // Given
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        double lat = -1.286389;
        double lon = 36.817223;

        Map<String, Object> mockApiResponse = createMockApiResponse();
        when(restTemplate.getForEntity(anyString(), eq(Map.class), eq(lat), eq(lon), eq("test-api-key")))
                .thenReturn(ResponseEntity.ok(mockApiResponse));

        // When
        Map<String, Object> result = weatherService.getWeather(lat, lon);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("temp")).isEqualTo(22.5);
        assertThat(result.get("humidity")).isEqualTo(65);
        assertThat(result.get("windSpeed")).isEqualTo(5.5);
        assertThat(result.get("weatherMain")).isEqualTo("Clear");
        assertThat(result.get("weatherDescription")).isEqualTo("clear sky");
        verify(restTemplate).getForEntity(anyString(), eq(Map.class), eq(lat), eq(lon), eq("test-api-key"));
    }

    @Test
    void getWeather_WhenApiReturnsNull_ShouldReturnMockWeather() {
        // Given
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        double lat = -1.286389;
        double lon = 36.817223;

        when(restTemplate.getForEntity(anyString(), eq(Map.class), anyDouble(), anyDouble(), anyString()))
                .thenReturn(ResponseEntity.ok(null));

        // When
        Map<String, Object> result = weatherService.getWeather(lat, lon);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("temp")).isEqualTo(25.0); // Mock weather value
    }

    @Test
    void getWeather_WhenApiThrowsException_ShouldReturnMockWeather() {
        // Given
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
        double lat = -1.286389;
        double lon = 36.817223;

        when(restTemplate.getForEntity(anyString(), eq(Map.class), anyDouble(), anyDouble(), anyString()))
                .thenThrow(new RestClientException("API Error"));

        // When
        Map<String, Object> result = weatherService.getWeather(lat, lon);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("temp")).isEqualTo(25.0); // Mock weather value
        assertThat(result.get("weatherMain")).isEqualTo("Clouds");
    }

    @Test
    void getWeather_WithEmptyApiKey_ShouldReturnMockWeather() {
        // Given
        ReflectionTestUtils.setField(weatherService, "apiKey", "");
        double lat = -1.286389;
        double lon = 36.817223;

        // When
        Map<String, Object> result = weatherService.getWeather(lat, lon);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("temp")).isEqualTo(25.0);
        verify(restTemplate, never()).getForEntity(anyString(), any(), anyDouble(), anyDouble(), anyString());
    }

    private Map<String, Object> createMockApiResponse() {
        Map<String, Object> response = new HashMap<>();

        // Main weather data
        Map<String, Object> main = new HashMap<>();
        main.put("temp", 22.5);
        main.put("humidity", 65);
        main.put("temp_max", 25.0);
        main.put("temp_min", 20.0);
        response.put("main", main);

        // Wind data
        Map<String, Object> wind = new HashMap<>();
        wind.put("speed", 5.5);
        response.put("wind", wind);

        // Weather description
        Map<String, Object> weather = new HashMap<>();
        weather.put("id", 800);
        weather.put("main", "Clear");
        weather.put("description", "clear sky");
        response.put("weather", Collections.singletonList(weather));

        // Sys data (sunrise/sunset)
        Map<String, Object> sys = new HashMap<>();
        sys.put("sunrise", 1610000000L);
        sys.put("sunset", 1610043600L);
        response.put("sys", sys);

        return response;
    }
}
