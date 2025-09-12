package com.projetApply.Project_Apply.unitaire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MailServiceImplementsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);
        user.setUsername("john");

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setUsername("john");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");

    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        userService.updateUser(userDTO, 1);
        verify(userRepository, times(1)).save(any(User.class));
    }

}
