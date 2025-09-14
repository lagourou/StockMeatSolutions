package com.projetApply.Project_Apply.unitaire.controller;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.controller.ProfilController;
import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.mapper.UserMapper;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractView;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfilController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@Import(ProfilControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @SuppressWarnings("unused")
        UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        @SuppressWarnings("unused")
        UserMapper userMapper() {
            return Mockito.mock(UserMapper.class);
        }

        @Bean
        @SuppressWarnings("unused")
        ViewResolver viewResolver() {
            return (viewName, locale) -> {
                if (viewName != null && viewName.startsWith("redirect:")) {
                    return null;
                }
                return new AbstractView() {
                    @Override
                    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                            HttpServletResponse response) {
                    }
                };
            };
        }
    }

    @BeforeEach
    @SuppressWarnings("unused")
    void setupSecurityContext() {
        User user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("pass");

        UserDetailsImplements userDetails = new UserDetailsImplements(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testProfil_withAuthenticatedUser() throws Exception {
        UserDTO dto = new UserDTO();
        when(userService.getUserById(1)).thenReturn(new User());
        when(userMapper.toDTO(any(User.class))).thenReturn(dto);

        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("user"));

        verify(userService).getUserById(1);
        verify(userMapper).toDTO(any(User.class));
    }

    @Test
    void testProfil_withoutAuthenticatedUser() throws Exception {

        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/profil"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testModifyUser_withValidationErrors() throws Exception {
        mockMvc.perform(post("/modify-profil")
                .flashAttr("user", new UserDTO())
                .param("username", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"));
    }

    @Test
    void testModifyUser_success() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("john_doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/modify-profil")
                .flashAttr("user", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil"));

        verify(userService).updateUser(eq(dto), eq(1));
    }

}
