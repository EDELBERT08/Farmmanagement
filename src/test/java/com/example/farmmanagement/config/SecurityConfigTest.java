package com.example.farmmanagement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@org.springframework.boot.test.context.SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void securityHeaders_ShouldBePresent() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"));
    }

    @Test
    void csp_ShouldExcludeUnsafeInlineForScripts() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(header().string("Content-Security-Policy", containsString("script-src 'self'")))
                .andExpect(header().string("Content-Security-Policy",
                        not(containsString("script-src 'self' 'unsafe-inline'"))));
    }

    @Test
    void csp_ShouldAllowRequiredExternalSources() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(header().string("Content-Security-Policy", containsString("https://cdn.jsdelivr.net")))
                .andExpect(header().string("Content-Security-Policy", containsString("https://cdn.tailwindcss.com")))
                .andExpect(header().string("Content-Security-Policy", containsString("https://unpkg.com")))
                .andExpect(header().string("Content-Security-Policy", containsString("https://cdnjs.cloudflare.com")));
    }
}
