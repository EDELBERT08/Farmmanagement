package com.example.farmmanagement.controllers;

import com.example.farmmanagement.service.WeatherService;
import com.example.farmmanagement.repository.UserRepository;
import com.example.farmmanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private UserRepository userRepository;

    // View for the Weather Page
    @GetMapping("/weather")
    public String showWeatherPage(Model model, org.springframework.security.core.Authentication authentication) {
        model.addAttribute("pageTitle", "Weather");
        model.addAttribute("activePage", "weather");

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
                model.addAttribute("userLocation", user);
            }
        }

        return "weather";
    }

    // API Endpoint for frontend to fetch data
    @GetMapping("/api/weather")
    @ResponseBody
    public Map<String, Object> getWeather(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.getWeather(lat, lon);
    }
}
