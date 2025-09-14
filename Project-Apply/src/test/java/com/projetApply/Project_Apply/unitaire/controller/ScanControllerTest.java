package com.projetApply.Project_Apply.unitaire.controller;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.controller.ScanController;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;
import com.projetApply.Project_Apply.service.ScanService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ScanController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@Import(ScanControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class ScanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScanService scanService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @SuppressWarnings("unused")
        ScanService scanService() {
            return Mockito.mock(ScanService.class);
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

        com.projetApply.Project_Apply.model.User user = new com.projetApply.Project_Apply.model.User();
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
    void testGetScansByUser() throws Exception {
        when(scanService.getScanDTOsByUser(1)).thenReturn(List.of(new ScanDTO()));

        mockMvc.perform(get("/scans/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("scans/user"))
                .andExpect(model().attributeExists("scans"));

        verify(scanService, atLeastOnce()).getScanDTOsByUser(1);
    }

    @Test
    void testGetScansByProduct() throws Exception {
        when(scanService.getScanDTOsByProduct("123")).thenReturn(List.of(new ScanDTO()));

        mockMvc.perform(get("/scans/product/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("scans/product"))
                .andExpect(model().attributeExists("scans"));

        verify(scanService, atLeastOnce()).getScanDTOsByProduct("123");
    }

    @Test
    void testGetShowScanPage() throws Exception {
        mockMvc.perform(get("/scans"))
                .andExpect(status().isOk())
                .andExpect(view().name("scans"));
    }

    @Test
    void testSaveScan_success() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(5);

        ScanDTO scanDTO = new ScanDTO();
        scanDTO.setProduct(product);

        when(scanService.saveScanDTO(eq(1), eq("123"))).thenReturn(scanDTO);

        mockMvc.perform(post("/scans/save")
                .param("barcode", "123")
                .flashAttr("scannedProducts", new ArrayList<>()))
                .andExpect(status().isOk())
                .andExpect(view().name("scans"))
                .andExpect(model().attributeExists("successMessage"));

        verify(scanService, atLeastOnce()).saveScanDTO(1, "123");
    }

    @Test
    void testSaveScan_outOfStock() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(0);

        ScanDTO scanDTO = new ScanDTO();
        scanDTO.setProduct(product);

        when(scanService.saveScanDTO(eq(1), eq("123"))).thenReturn(scanDTO);

        mockMvc.perform(post("/scans/save")
                .param("barcode", "123")
                .flashAttr("scannedProducts", new ArrayList<>()))
                .andExpect(status().isOk())
                .andExpect(view().name("scans"))
                .andExpect(model().attributeExists("warningMessage"));

        verify(scanService, atLeastOnce()).saveScanDTO(1, "123");
    }

    @Test
    void testSaveScan_productNotFound() throws Exception {
        when(scanService.saveScanDTO(eq(1), eq("123"))).thenThrow(new ProductNotFoundException("Not found"));

        mockMvc.perform(post("/scans/save")
                .param("barcode", "123")
                .flashAttr("scannedProducts", new ArrayList<>()))
                .andExpect(status().isOk())
                .andExpect(view().name("scans"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(scanService, atLeastOnce()).saveScanDTO(1, "123");
    }

    @Test
    void testSaveScan_unexpectedException() throws Exception {
        when(scanService.saveScanDTO(eq(1), eq("123"))).thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(post("/scans/save")
                .param("barcode", "123")
                .flashAttr("scannedProducts", new ArrayList<>()))
                .andExpect(status().isOk())
                .andExpect(view().name("scans"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(scanService, atLeastOnce()).saveScanDTO(1, "123");
    }

    @Test
    void testResetScannedProducts() throws Exception {
        mockMvc.perform(post("/scans/reset"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/scans"));
    }

}
