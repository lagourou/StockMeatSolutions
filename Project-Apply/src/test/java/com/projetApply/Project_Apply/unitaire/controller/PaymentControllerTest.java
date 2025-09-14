package com.projetApply.Project_Apply.unitaire.controller;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.controller.PaymentController;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.PaymentService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class)
@Import(PaymentControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @TestConfiguration
    static class TestConfig {

        @SuppressWarnings("unused")
        @Bean
        UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @SuppressWarnings("unused")
        @Bean
        PaymentService paymentService() {
            return Mockito.mock(PaymentService.class);
        }

        @SuppressWarnings("unused")
        @Bean
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
        user.setUsername("johnD");
        user.setEmail("johnD@example.com");
        user.setPassword("JDpassword");

        UserDetailsImplements userDetails = new UserDetailsImplements(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testShowPaymentPage_withProducts() throws Exception {
        List<ProductDTO> products = new ArrayList<>();
        ProductDTO p1 = new ProductDTO();
        p1.setPrice(new BigDecimal("10.50"));
        products.add(p1);

        mockMvc.perform(get("/payment").flashAttr("scannedProducts", products))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attributeExists("totalAmount", "products"))
                .andExpect(model().attribute("totalAmount", new BigDecimal("10.50")));
    }

    @Test
    void testShowPaymentPage_noProducts() throws Exception {
        mockMvc.perform(get("/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attribute("totalAmount", BigDecimal.ZERO));
    }

    @Test
    void testConfirmPayment_success() throws Exception {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        List<ProductDTO> products = new ArrayList<>();
        ProductDTO p1 = new ProductDTO();
        p1.setPrice(new BigDecimal("5.00"));
        products.add(p1);

        mockMvc.perform(post("/payment/confirm")
                .flashAttr("scannedProducts", products)
                .param("paymentType", PaymentType.CASH.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attributeExists("message"));

        verify(paymentService).processPayment(eq(user), eq(PaymentType.CASH), eq(products));
    }

    @Test
    void testConfirmPayment_userNotFound() throws Exception {

        reset(userRepository, paymentService);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(post("/payment/confirm")
                .flashAttr("scannedProducts", new ArrayList<>())
                .param("paymentType", PaymentType.CARD.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attributeExists("message"));

        verify(paymentService, never()).processPayment(any(), any(), any());
    }

    @Test
    void testConfirmPayment_exceptionThrown() throws Exception {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Erreur paiement")).when(paymentService)
                .processPayment(any(), any(), any());

        mockMvc.perform(post("/payment/confirm")
                .flashAttr("scannedProducts", new ArrayList<>())
                .param("paymentType", PaymentType.CARD.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attributeExists("message"));

        verify(paymentService).processPayment(any(), any(), any());
    }

    @Test
    void testScannedProducts_returnsEmptyList() {
        PaymentController controller = new PaymentController(userRepository, paymentService);
        List<ProductDTO> result = controller.scannedProducts();
        assertNotNull(result);
        assertTrue(result.isEmpty(), "La liste retournée doit être vide");
    }

}
