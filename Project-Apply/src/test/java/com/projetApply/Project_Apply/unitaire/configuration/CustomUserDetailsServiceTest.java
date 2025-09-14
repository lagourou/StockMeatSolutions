package com.projetApply.Project_Apply.unitaire.configuration;

import com.projetApply.Project_Apply.configuration.CustomUserDetailsService;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result, "UserDetails ne doit pas être null");
        assertEquals("encodedPassword", result.getPassword(), "Le mot de passe doit correspondre");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown@example.com"));
        verify(userRepository).findByEmail("unknown@example.com");
    }
}
