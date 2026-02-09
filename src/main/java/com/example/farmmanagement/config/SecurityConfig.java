package com.example.farmmanagement.config;

import com.example.farmmanagement.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .headers(headers -> {
                    headers.contentSecurityPolicy(csp -> csp
                            .policyDirectives("default-src 'self'; " +
                                    "script-src 'self' 'unsafe-eval' https://cdn.jsdelivr.net https://cdn.tailwindcss.com https://unpkg.com https://cdnjs.cloudflare.com; "
                                    +
                                    "style-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com https://fonts.googleapis.com https://cdnjs.cloudflare.com https://unpkg.com; "
                                    +
                                    "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com; " +
                                    "connect-src 'self' https://geocoding-api.open-meteo.com; " +
                                    "img-src 'self' data: https://tile.openstreetmap.org https://a.tile.openstreetmap.org https://b.tile.openstreetmap.org https://c.tile.openstreetmap.org;"));
                    headers.permissionsPolicy(permissions -> permissions
                            .policy("geolocation=(self), microphone=(), camera=(), payment=(), usb=()"));
                    headers.frameOptions(frame -> frame.deny());
                    headers.xssProtection(xss -> xss.headerValue(
                            org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/error")
                        .permitAll() // Public
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only endpoints
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true) // Redirect to dashboard after login
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
