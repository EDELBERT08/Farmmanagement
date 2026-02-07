package com.example.farmmanagement.config;

import com.example.farmmanagement.model.Role;
import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("Checking for existing users...");
            User admin = userRepository.findByUsername("admin").orElse(new User());
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            // Default Location: Nairobi, Kenya
            admin.setCity("Nairobi");
            admin.setLatitude(-1.2921);
            admin.setLongitude(36.8219);
            userRepository.save(admin);
            System.out.println("ADMIN user updated/created: username=admin");
        };
    }
}
