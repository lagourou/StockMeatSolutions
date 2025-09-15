package com.projetApply.Project_Apply.unitaire.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.ScanRepository;
import com.projetApply.Project_Apply.service.InvoiceService;

@SpringBootTest
@ActiveProfiles("test")
class InvoiceServiceTest {

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ScanRepository scanRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void generateInvoicePDF_shouldContainPaymentInfo() throws Exception {
        // Arrange
        Payment payment = new Payment();
        payment.setId(1);
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setType(PaymentType.CARD);

        User employee = new User();
        employee.setEmail("employee@example.com");
        payment.setEmployee(employee);

        Product product = new Product();
        product.setName("Boeuf");
        product.setPrice(BigDecimal.valueOf(10));

        Scan scan = new Scan();
        scan.setProduct(product);
        scan.setPayment(payment);

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[] { product, 2L });

        when(scanRepository.findProductQuantitiesByPayment(payment))
                .thenReturn(mockResult);

        // Act
        byte[] pdfBytes = invoiceService.generateInvoicePDF(payment);

        // Assert - lecture avec OpenPDF
        try (PdfReader reader = new PdfReader(pdfBytes)) {
            StringBuilder allText = new StringBuilder();
            PdfTextExtractor extractor = new PdfTextExtractor(reader);

            int pages = reader.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                allText.append(extractor.getTextFromPage(i));
            }

            // Normalisation du texte pour éviter les faux négatifs
            String text = allText.toString();
            String normalized = text
                    .replace("\u00A0", " ") // espace insécable → espace normal
                    .replace(",", ".") // virgule → point
                    .replaceAll("\\s+", " ") // espaces multiples → un seul
                    .trim();

            System.out.println("=== TEXTE PDF EXTRAIT ===");
            System.out.println(normalized);

            assertTrue(normalized.contains("Facture - StockMeat Solutions"));
            assertTrue(normalized.contains("employee@example.com"));
            assertTrue(normalized.contains("Boeuf"));
            // Montants : on tolère plusieurs formats
            assertTrue(
                    normalized.contains("10.00 €") ||
                            normalized.contains("10.00€") ||
                            normalized.contains("10.00 EUR") ||
                            normalized.contains("10 €"));
            assertTrue(
                    normalized.contains("100 €") ||
                            normalized.contains("100€") ||
                            normalized.contains("100 EUR"));
        }
    }
}
