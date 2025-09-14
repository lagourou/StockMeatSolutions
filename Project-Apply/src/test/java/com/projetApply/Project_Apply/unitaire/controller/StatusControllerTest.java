package com.projetApply.Project_Apply.unitaire.controller;

import com.projetApply.Project_Apply.controller.StatusController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
public class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Test 1: Vérifier que l'endpoint /api/status renvoie un statut ok
    @Test
    @WithMockUser(username = "testuser", roles = "USER") // Simule un utilisateur authentifié
    void testStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk()) // Vérifier que le statut est 200 OK
                .andExpect(content().contentType("application/json")) // Vérifier le type de contenu
                .andExpect(jsonPath("$.status").value("ok")) // Vérifier le champ "status"
                .andExpect(jsonPath("$.environment").value("dev")) // Vérifier le champ "environment"
                .andExpect(jsonPath("$.message").value("Application is running")); // Vérifier le champ "message"
    }
}
