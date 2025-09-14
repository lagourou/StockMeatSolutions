package com.projetApply.Project_Apply.unitaire.controller;

import com.projetApply.Project_Apply.controller.RegisterController;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void getRegister_shouldReturnRegisterViewWithUserAttribute() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void postRegister_shouldReturnRegisterView_whenValidationErrors() throws Exception {
        // Pas de username → erreur de validation
        mockMvc.perform(post("/register")
                .param("email", "test@example.com")
                .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void postRegister_shouldReturnRegisterView_whenEmailAlreadyExists() throws Exception {
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/register")
                .param("username", "validUser") // conforme aux contraintes
                .param("email", "existing@example.com") // email valide
                .param("password", "password123")) // conforme aux contraintes
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));

        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void postRegister_shouldRedirectToLogin_whenValidData() throws Exception {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");

        mockMvc.perform(post("/register")
                .param("username", "validUser") // conforme aux contraintes
                .param("email", "new@example.com") // email valide
                .param("password", "password123")) // conforme aux contraintes
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("validUser");
        assertThat(captor.getValue().getEmail()).isEqualTo("new@example.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("encodedPass");
    }
}
