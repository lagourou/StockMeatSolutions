package com.projetApply.Project_Apply.unitaire.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.projetApply.Project_Apply.controller.ProductController;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.ProductService;
import com.projetApply.Project_Apply.service.ScanService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(controllers = ProductController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@Import(ProductControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ScanService scanService;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class TestConfig {
        @SuppressWarnings("unused")
        @Bean
        ProductService productService() {
            return Mockito.mock(ProductService.class);
        }

        @SuppressWarnings("unused")
        @Bean
        ScanService scanService() {
            return Mockito.mock(ScanService.class);
        }

        @SuppressWarnings("unused")
        @Bean
        UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
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
                    protected void renderMergedOutputModel(
                            Map<String, Object> model,
                            HttpServletRequest request,
                            HttpServletResponse response) {
                    }
                };
            };
        }
    }

    @Test
    void testShowForm() throws Exception {
        mockMvc.perform(get("/products/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"));
    }

    @Test
    void testAddOrUpdateProduct() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setId(5);
        when(productService.addProduct(anyString(), anyString(), anyInt(), anyInt(), any(BigDecimal.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/products/add")
                .param("codeBarre", "123456")
                .param("nom", "Produit Test")
                .param("quantite", "10")
                .param("poids", "500")
                .param("prix", "9.99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/5"));

        verify(productService).addProduct(eq("123456"), eq("Produit Test"), eq(10), eq(500),
                eq(new BigDecimal("9.99")));
    }

    @Test
    void testListProducts_success() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(productService.getAllProducts()).thenReturn(List.of(new ProductDTO()));
        when(productService.getTotalStock()).thenReturn(100);
        when(scanService.getScanDTOsByUser(1)).thenReturn(List.of(new ScanDTO()));

        Principal principal = () -> "john";

        mockMvc.perform(get("/products").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products", "stockTotal", "scanTotal"));

        verify(productService).updateAllStatuses();
        verify(scanService).getScanDTOsByUser(1);
    }

    @Test
    void testListProducts_userNotFound() throws Exception {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        Principal principal = () -> "john";

        mockMvc.perform(get("/products").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("user/notfound"))
                .andExpect(model().attributeExists("error"));

        verify(userRepository, atLeastOnce()).findByUsername("john");

    }

    @Test
    void testRemoveProduct() throws Exception {
        mockMvc.perform(post("/products/remove")
                .param("codeBarre", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/"));

        verify(productService).removeProduct("123456");
    }

    @Test
    void testUpdateStatus() throws Exception {
        mockMvc.perform(post("/products/refresh-status"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService, atLeastOnce()).updateAllStatuses();
    }
}
