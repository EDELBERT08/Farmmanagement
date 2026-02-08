package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.Role;
import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LabourManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listEmployees() throws Exception {
        User user = new User();
        user.setUsername("emp1");
        user.setRole(Role.EMPLOYEE);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/admin/labour"))
                .andExpect(status().isOk())
                .andExpect(view().name("labour-management"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addEmployee() throws Exception {
        when(userRepository.findByUsername("newemp")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/admin/labour/add")
                        .with(csrf())
                        .param("username", "newemp")
                        .param("password", "pass")
                        .param("role", "EMPLOYEE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE") // Should be forbidden
    void listEmployees_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/labour"))
                .andExpect(status().isForbidden());
    }
}
