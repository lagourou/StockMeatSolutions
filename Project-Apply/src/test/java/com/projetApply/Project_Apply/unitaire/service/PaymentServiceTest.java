package com.projetApply.Project_Apply.unitaire.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.*;
import com.projetApply.Project_Apply.service.*;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ScanRepository scanRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private List<ProductDTO> scannedProducts;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUsername("testUser");
        user.setPassword("password123");

        ProductDTO product1 = new ProductDTO();
        product1.setName("Entrecôte de boeuf");
        product1.setPrice(new BigDecimal("2.00"));
        product1.setBarcode("1234567890123");

        ProductDTO product2 = new ProductDTO();
        product2.setName("Côte de porc");
        product2.setPrice(new BigDecimal("2.50"));
        product2.setBarcode("9876543210987");

        scannedProducts = Arrays.asList(product1, product1, product2);
    }

    @Test
    void testProcessPaymentGeneratesPdfAndSendsMail() {

        Product mockProduct1 = new Product();
        mockProduct1.setBarcode("1234567890123");
        mockProduct1.setName("Entrecôte de boeuf");
        mockProduct1.setPrice(new BigDecimal("2.00"));
        mockProduct1.setQuantity(10);

        Product mockProduct2 = new Product();
        mockProduct2.setBarcode("9876543210987");
        mockProduct2.setName("Côte de porc");
        mockProduct2.setPrice(new BigDecimal("2.50"));
        mockProduct2.setQuantity(5);

        when(productRepository.findByBarcode("1234567890123")).thenReturn(Optional.of(mockProduct1));
        when(productRepository.findByBarcode("9876543210987")).thenReturn(Optional.of(mockProduct2));

        Payment mockPayment = new Payment();
        mockPayment.setId(17);
        mockPayment.setAmount(new BigDecimal("6.50"));
        mockPayment.setType(PaymentType.CARD);
        mockPayment.setPaymentDate(LocalDateTime.now());
        mockPayment.setEmployee(user);

        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        when(scanRepository.findByUser(user)).thenReturn(new ArrayList<>());

        byte[] mockPdf = new byte[] { 1, 2, 3 };
        when(invoiceService.generateInvoicePDF(mockPayment)).thenReturn(mockPdf);

        doNothing().when(mailService).sendMailWithAttachment(
                eq(user.getEmail()),
                anyString(),
                anyString(),
                anyString(),
                eq(mockPdf),
                anyString());

        byte[] result = paymentService.processPayment(user, PaymentType.CARD, scannedProducts);

        assertNotNull(result);
        assertEquals(3, result.length);
        verify(paymentRepository).save(any(Payment.class));
        verify(invoiceService).generateInvoicePDF(mockPayment);
        verify(mailService).sendMailWithAttachment(
                eq(user.getEmail()),
                anyString(),
                anyString(),
                anyString(),
                eq(mockPdf),
                anyString());
    }

    @Test
    void testProcessPaymentFailsWhenStockIsInsufficient() {
        Product mockProduct = new Product();
        mockProduct.setBarcode("1234567890123");
        mockProduct.setName("Entrecôte de boeuf");
        mockProduct.setPrice(new BigDecimal("2.00"));
        mockProduct.setQuantity(1);

        when(productRepository.findByBarcode("1234567890123")).thenReturn(Optional.of(mockProduct));

        Payment mockPayment = new Payment();
        mockPayment.setId(99);
        mockPayment.setAmount(BigDecimal.ZERO);
        mockPayment.setType(PaymentType.CARD);
        mockPayment.setPaymentDate(LocalDateTime.now());
        mockPayment.setEmployee(user);
        when(paymentRepository.save(any())).thenReturn(mockPayment);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setBarcode("1234567890123");
        productDTO.setName("Entrecôte de boeuf");
        productDTO.setPrice(new BigDecimal("2.00"));

        List<ProductDTO> scanned = Arrays.asList(productDTO, productDTO);

        assertThrows(IllegalStateException.class,
                () -> paymentService.processPayment(user, PaymentType.CARD, scanned));

    }

    @Test
    void testProcessPaymentFailsWhenProductNotFound() {
        when(productRepository.findByBarcode("000")).thenReturn(Optional.empty());

        Payment mockPayment = new Payment();
        mockPayment.setId(99);
        mockPayment.setAmount(BigDecimal.ZERO);
        mockPayment.setType(PaymentType.CARD);
        mockPayment.setPaymentDate(LocalDateTime.now());
        mockPayment.setEmployee(user);
        when(paymentRepository.save(any())).thenReturn(mockPayment);

        ProductDTO unknownProduct = new ProductDTO();
        unknownProduct.setBarcode("000");
        unknownProduct.setName("Produit inconnu");
        unknownProduct.setPrice(new BigDecimal("1.00"));

        List<ProductDTO> scanned = List.of(unknownProduct);

        assertThrows(ProductNotFoundException.class,
                () -> paymentService.processPayment(user, PaymentType.CARD, scanned));
    }

    @Test
    void testProcessPaymentUpdatesStockCorrectly() {
        Product mockProduct = new Product();
        mockProduct.setBarcode("1234567890123");
        mockProduct.setName("Entrecôte de boeuf");
        mockProduct.setPrice(new BigDecimal("2.00"));
        mockProduct.setQuantity(5);

        when(productRepository.findByBarcode("1234567890123")).thenReturn(Optional.of(mockProduct));
        when(paymentRepository.save(any())).thenReturn(new Payment());

        ProductDTO dto = new ProductDTO();
        dto.setBarcode("1234567890123");
        dto.setName("Entrecôte de boeuf");
        dto.setPrice(new BigDecimal("2.00"));

        paymentService.processPayment(user, PaymentType.CARD, List.of(dto, dto));

        assertEquals(3, mockProduct.getQuantity());
    }
    

}
