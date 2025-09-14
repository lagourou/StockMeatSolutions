package com.projetApply.Project_Apply.unitaire.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.exception.UserNotFoundException;
import com.projetApply.Project_Apply.mapper.UserMapper;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.UserService;

import jakarta.persistence.EntityNotFoundException;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("oldPassword");

        userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("newPassword");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.toEntity(userDTO)).thenReturn(user);

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

    @Test
    void testFindById_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testFindById_userNotFound_shouldThrowException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(99));
    }

    @Test
    void testGetUserByEmail_success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        User result = userService.getUserByEmail("john@example.com");
        assertEquals("john", result.getUsername());
    }

    @Test
    void testGetUserByEmail_userNotFound_shouldThrowException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("unknown@example.com"));
    }

    @Test
    void testSaveUser_success() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.saveNewUser(userDTO);
        verify(userRepository).save(any(User.class));

        assertEquals("john", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testSave_success() {
        when(userRepository.save(user)).thenReturn(user);
        userService.saveUser(user);
        verify(userRepository).save(user);

        assertEquals("john", user.getUsername());
    }

    @Test
    void testDeleteUser_success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        userService.deleteUser(1);
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_userNotFound_shouldThrowException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(99));
    }

}
