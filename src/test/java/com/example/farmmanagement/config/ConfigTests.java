package com.example.farmmanagement.config;

import com.example.farmmanagement.model.User;
import com.example.farmmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataSeeder dataSeeder;

    @Test
    void testInitData_AdminExists() throws Exception {
        // Set admin password property
        ReflectionTestUtils.setField(dataSeeder, "adminPassword", "admin123");

        User admin = new User();
        admin.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");

        CommandLineRunner runner = dataSeeder.initData(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository).save(admin);
    }

    @Test
    void testInitData_AdminNew() throws Exception {
        // Set admin password property
        ReflectionTestUtils.setField(dataSeeder, "adminPassword", "admin123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");

        CommandLineRunner runner = dataSeeder.initData(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository).save(any(User.class));
    }
}
