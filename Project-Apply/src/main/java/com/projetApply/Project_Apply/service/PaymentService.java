package com.projetApply.Project_Apply.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.PaymentRepository;
import com.projetApply.Project_Apply.repository.ProductRepository;
import com.projetApply.Project_Apply.repository.ScanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final ScanRepository scanRepository;
    private final InvoiceService invoiceService;
    private final MailService mailService;

    @Transactional
    public byte[] processPayment(User employee, PaymentType paymentType, List<ProductDTO> scannedProducts) {
        if (scannedProducts == null || scannedProducts.isEmpty()) {
            throw new IllegalArgumentException("Aucun produit à payer.");
        }

        BigDecimal total = scannedProducts.stream()
                .map(ProductDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Montant total du paiement : {} €", total);

        Payment payment = new Payment();
        payment.setAmount(total);
        payment.setType(paymentType);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("EN_ATTENTE");
        payment.setEmployee(employee);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Paiement enregistré avec ID : {}", savedPayment.getId());

        for (ProductDTO dto : scannedProducts) {
            Product product = productRepository.findByBarcode(dto.getBarcode())
                    .orElseThrow(() -> new ProductNotFoundException("Produit introuvable : " + dto.getName()));
            if (product.getQuantity() <= 0) {
                throw new IllegalStateException("Stock épuisé pour le produit : " + product.getName());
            }
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);
        }

        List<Scan> scans = scanRepository.findByUser(employee);
        for (Scan scan : scans) {
            boolean match = scannedProducts.stream()
                    .anyMatch(dto -> dto.getBarcode().equals(scan.getProduct().getBarcode()));
            if (match && scan.getPayment() == null) {
                scan.setPayment(savedPayment);
                scanRepository.save(scan);
            }
        }

        byte[] pdf = invoiceService.generateInvoicePDF(savedPayment);

        mailService.sendMailWithAttachment(
                employee.getEmail(),
                "noreply@stockmeatsolutions.site",
                "Votre facture",
                "Veuillez trouver ci-joint votre facture.",
                pdf,
                "facture_" + savedPayment.getId() + ".pdf");

        return pdf;
    }
}
