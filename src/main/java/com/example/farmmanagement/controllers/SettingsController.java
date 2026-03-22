package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/settings")
    public String showSettings(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Settings");
        model.addAttribute("activePage", "settings");

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("user", user);

        return "settings";
    }

    @PostMapping("/settings/location")
    public String updateLocation(@RequestParam String city,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setCity(city);
        // Simple validation or clamp if needed
        user.setLatitude(latitude);
        user.setLongitude(longitude);

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Location updated successfully!");
        return "redirect:/settings";
    }

    @PostMapping("/settings/password")
    public String updatePassword(@RequestParam String newPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Password cannot be empty.");
        }

        return "redirect:/settings";
    }
}
