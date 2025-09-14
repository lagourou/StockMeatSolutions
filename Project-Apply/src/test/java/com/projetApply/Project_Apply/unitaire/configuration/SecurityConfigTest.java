package com.projetApply.Project_Apply.unitaire.configuration;

import com.projetApply.Project_Apply.configuration.SecurityConfig;
import com.projetApply.Project_Apply.configuration.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @SuppressWarnings("unused")
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testPublicPagesAccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                // Accepte JSON ou HTML selon le contexte
                .andExpect(result -> {
                    String type = result.getResponse().getContentType();
                    assertTrue(type != null && (type.contains("application/json") || type.contains("text/html")));
                });
    }

    @Test
    void testApiStatusAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String type = result.getResponse().getContentType();
                    assertTrue(type != null && (type.contains("application/json") || type.contains("text/html")));
                });
    }

    @Test
    void testProtectedPagesAccess() throws Exception {
        // Vérifie que les pages protégées redirigent vers /login
        mockMvc.perform(get("/profil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/modify-profil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
