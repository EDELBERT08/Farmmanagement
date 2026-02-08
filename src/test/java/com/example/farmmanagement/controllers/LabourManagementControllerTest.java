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
    void addEmployee_Success() throws Exception {
        when(userRepository.findByUsername("newemp")).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/labour/add")
                        .with(csrf())
                        .param("username", "newemp")
                        .param("password", "pass")
                        .param("role", "EMPLOYEE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attribute("success", "Employee added successfully!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addEmployee_UsernameExists() throws Exception {
        User existing = new User();
        existing.setUsername("existing");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/admin/labour/add")
                .with(csrf())
                .param("username", "existing")
                .param("password", "pass")
                .param("role", "EMPLOYEE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attribute("error", "Username already exists!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editEmployee_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldname");
        user.setRole(Role.EMPLOYEE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Simulate no conflict for new name
        when(userRepository.findByUsername("newname")).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/labour/1/edit")
                .with(csrf())
                .param("username", "newname")
                .param("password", "newpass")
                .param("role", "ADMIN")) // Use valid role
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attribute("success", "Employee updated successfully!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_Success() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("todelete");
        user.setRole(Role.EMPLOYEE);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/labour/2/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attribute("success", "Employee deleted successfully!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_LastAdmin_Failure() throws Exception {
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(admin)); // Only 1 admin

        mockMvc.perform(post("/admin/labour/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attribute("error", "Cannot delete the last admin user!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeRole_Success() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setRole(Role.EMPLOYEE);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/labour/3/change-role")
                .with(csrf())
                .param("role", "ADMIN")) // Use valid role
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/labour"))
                .andExpect(flash().attribute("success", "Employee role updated successfully!"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE") // Should be forbidden
    void listEmployees_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/labour"))
                .andExpect(status().isForbidden());
    }
}
