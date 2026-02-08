package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "admin")
    void showSettings_ShouldReturnSettingsPage() throws Exception {
        // Given
        User user = createUser("admin", "Nairobi");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("pageTitle", "Settings"))
                .andExpect(model().attribute("activePage", "settings"));

        verify(userRepository).findByUsername("admin");
    }

    @Test
    @WithMockUser(username = "admin")
    void updateLocation_ShouldUpdateUserLocation() throws Exception {
        // Given
        User user = createUser("admin", "Nairobi");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/settings/location")
                .with(csrf())
                .param("city", "Mombasa")
                .param("latitude", "-4.043740")
                .param("longitude", "39.668205"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(flash().attribute("success", "Location updated successfully!"));

        verify(userRepository).findByUsername("admin");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "admin")
    void updatePassword_WithValidPassword_ShouldUpdatePassword() throws Exception {
        // Given
        User user = createUser("admin", "Nairobi");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/settings/password")
                .with(csrf())
                .param("newPassword", "newSecurePassword123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(flash().attribute("success", "Password updated successfully!"));

        verify(passwordEncoder).encode("newSecurePassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "admin")
    void updatePassword_WithEmptyPassword_ShouldReturnError() throws Exception {
        // Given
        User user = createUser("admin", "Nairobi");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/settings/password")
                .with(csrf())
                .param("newPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(flash().attribute("error", "Password cannot be empty."));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "admin")
    void updatePassword_WithWhitespacePassword_ShouldReturnError() throws Exception {
        // Given
        User user = createUser("admin", "Nairobi");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // Record the number of times encode was called before the test
        reset(passwordEncoder);

        // When & Then
        mockMvc.perform(post("/settings/password")
                .with(csrf())
                .param("newPassword", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings"))
                .andExpect(flash().attribute("error", "Password cannot be empty."));

        // Should not encode empty/whitespace passwords
        verify(passwordEncoder, never()).encode(anyString());
    }

    private User createUser(String username, String city) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setCity(city);
        user.setLatitude(-1.286389);
        user.setLongitude(36.817223);
        return user;
    }
}
