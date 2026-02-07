package com.example.farmmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class WeatherService {

    // OpenWeatherMap API - more accurate than Open-Meteo
    // Get free API key from: https://home.openweathermap.org/api_keys
    @Value("${openweather.api.key:demo}")
    private String apiKey;

    private final String CURRENT_WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}&units=metric";
    private final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={apiKey}&units=metric&cnt=40";

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getWeather(double lat, double lon) {
        try {
            // If no API key configured, return mock data with warning
            if (apiKey == null || apiKey.equals("demo") || apiKey.isEmpty()) {
                System.out.println("⚠️ WARNING: No OpenWeatherMap API key configured. Using mock data.");
                System.out.println("📌 Get a free API key at: https://home.openweathermap.org/api_keys");
                return getMockWeather();
            }

            // Fetch current weather
            ResponseEntity<Map> currentResponse = restTemplate.getForEntity(
                    CURRENT_WEATHER_URL, Map.class, lat, lon, apiKey);

            if (currentResponse.getBody() == null) {
                return getMockWeather();
            }

            return processOpenWeatherData(currentResponse.getBody());

        } catch (Exception e) {
            System.err.println("❌ Weather API Error: " + e.getMessage());
            e.printStackTrace();
            return getMockWeather();
        }
    }

    private Map<String, Object> processOpenWeatherData(Map<String, Object> rawData) {
        Map<String, Object> processed = new HashMap<>();

        try {
            // Main weather data
            Map<String, Object> main = (Map<String, Object>) rawData.get("main");
            Map<String, Object> wind = (Map<String, Object>) rawData.get("wind");
            Map<String, Object> sys = (Map<String, Object>) rawData.get("sys");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) rawData.get("weather");

            // Current conditions
            if (main != null) {
                processed.put("temp", main.get("temp"));
                processed.put("humidity", main.get("humidity"));
                processed.put("maxTemp", main.get("temp_max"));
                processed.put("minTemp", main.get("temp_min"));
            }

            if (wind != null) {
                processed.put("windSpeed", wind.get("speed"));
            }

            // Weather condition
            if (weatherList != null && !weatherList.isEmpty()) {
                Map<String, Object> weather = weatherList.get(0);
                processed.put("weatherCode", weather.get("id")); // OpenWeather ID
                processed.put("weatherMain", weather.get("main")); // e.g., "Clouds", "Rain"
                processed.put("weatherDescription", weather.get("description")); // e.g., "scattered clouds"
            }

            // Sunrise/Sunset
            if (sys != null) {
                Long sunrise = getLongValue(sys.get("sunrise"));
                Long sunset = getLongValue(sys.get("sunset"));
                if (sunrise != null)
                    processed.put("sunrise", formatUnixTime(sunrise));
                if (sunset != null)
                    processed.put("sunset", formatUnixTime(sunset));
            }

            // Mock UV index (OpenWeather doesn't provide in free tier)
            processed.put("uvIndex", 5.0);
            processed.put("isDay", 1);

            System.out
                    .println("✅ Weather data: " + processed.get("temp") + "°C, " + processed.get("weatherDescription"));

        } catch (Exception e) {
            System.err.println("Error processing weather data: " + e.getMessage());
        }

        return processed;
    }

    private Long getLongValue(Object value) {
        if (value == null)
            return null;
        if (value instanceof Long)
            return (Long) value;
        if (value instanceof Integer)
            return ((Integer) value).longValue();
        return null;
    }

    private String formatUnixTime(long unixTime) {
        java.time.Instant instant = java.time.Instant.ofEpochSecond(unixTime);
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.ofInstant(
                instant, java.time.ZoneId.systemDefault());
        return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }

    private Map<String, Object> getMockWeather() {
        Map<String, Object> mock = new HashMap<>();
        mock.put("temp", 25.0);
        mock.put("humidity", 60);
        mock.put("windSpeed", 10.0);
        mock.put("weatherCode", 801); // Few clouds
        mock.put("weatherMain", "Clouds");
        mock.put("weatherDescription", "few clouds");
        mock.put("isDay", 1);
        mock.put("sunrise", "06:00");
        mock.put("sunset", "18:30");
        mock.put("uvIndex", 5.0);
        mock.put("maxTemp", 28.0);
        mock.put("minTemp", 18.0);
        return mock;
    }
}
