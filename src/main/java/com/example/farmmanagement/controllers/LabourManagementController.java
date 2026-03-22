package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.Role;
import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/labour")
public class LabourManagementController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LabourManagementController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listEmployees(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", Role.values());
        model.addAttribute("pageTitle", "Labour Management");
        model.addAttribute("activePage", "labour");
        return "labour-management";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute("newUser") User user, RedirectAttributes redirectAttributes) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists!");
                return "redirect:/admin/labour";
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set default role if not specified
            if (user.getRole() == null) {
                user.setRole(Role.EMPLOYEE);
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Employee added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add employee: " + e.getMessage());
        }

        return "redirect:/admin/labour";
    }

    @PostMapping("/{id}/edit")
    public String editEmployee(@PathVariable Long id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam Role role,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update username if changed
            if (!user.getUsername().equals(username)) {
                if (userRepository.findByUsername(username).isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Username already exists!");
                    return "redirect:/admin/labour";
                }
                user.setUsername(username);
            }

            // Update password if provided
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            // Update role
            user.setRole(role);

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update employee: " + e.getMessage());
        }

        return "redirect:/admin/labour";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Prevent deleting the last admin
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .count();

            if (user.getRole() == Role.ADMIN && adminCount <= 1) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete the last admin user!");
                return "redirect:/admin/labour";
            }

            userRepository.delete(user);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete employee: " + e.getMessage());
        }

        return "redirect:/admin/labour";
    }

    @PostMapping("/{id}/change-role")
    public String changeEmployeeRole(@PathVariable Long id,
            @RequestParam Role role,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Prevent changing the last admin to employee
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .count();

            if (user.getRole() == Role.ADMIN && adminCount <= 1 && role == Role.EMPLOYEE) {
                redirectAttributes.addFlashAttribute("error", "Cannot change the last admin to employee!");
                return "redirect:/admin/labour";
            }

            user.setRole(role);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Employee role updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update role: " + e.getMessage());
        }

        return "redirect:/admin/labour";
    }
}
