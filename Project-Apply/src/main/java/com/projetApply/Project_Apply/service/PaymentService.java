package com.projetApply.Project_Apply.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

/**
 * Service qui gère le processus de paiement d’un utilisateur.
 * 
 * Cette classe permet de :
 * - calculer le montant total à payer selon les produits scannés,
 * - vérifier le stock disponible pour chaque produit,
 * - enregistrer le paiement en base,
 * - associer les scans au paiement,
 * - générer une facture PDF,
 * - envoyer la facture par mail à l’utilisateur.
 * 
 * Elle utilise :
 * - les repositories pour accéder aux produits, paiements et scans,
 * - InvoiceService pour créer la facture,
 * - MailService pour envoyer le mail avec la facture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final ScanRepository scanRepository;
    private final InvoiceService invoiceService;
    private final MailService mailService;

    /**
     * Traite le paiement d’un utilisateur pour une liste de produits scannés.
     * 
     * Étapes :
     * - vérifie que la liste n’est pas vide,
     * - regroupe les produits par code-barres et compte les quantités,
     * - calcule le montant total,
     * - vérifie le stock disponible,
     * - enregistre le paiement,
     * - met à jour les quantités en stock,
     * - associe les scans au paiement,
     * - génère une facture PDF,
     * - envoie la facture par mail.
     * 
     * @param employee        utilisateur qui effectue le paiement
     * @param paymentType     type de paiement (ex : carte, espèces…)
     * @param scannedProducts liste des produits scannés
     * @return la facture en format PDF (tableau de bytes)
     * @throws IllegalArgumentException si la liste de produits est vide
     * @throws ProductNotFoundException si un produit n’est pas trouvé
     * @throws IllegalStateException    si le stock est insuffisant
     */
    @Transactional
    public byte[] processPayment(User employee, PaymentType paymentType, List<ProductDTO> scannedProducts) {
        if (scannedProducts == null || scannedProducts.isEmpty()) {
            throw new IllegalArgumentException("Aucun produit à payer.");
        }

        Map<String, Long> qtyByBarcode = scannedProducts.stream()
                .collect(Collectors.groupingBy(ProductDTO::getBarcode, Collectors.counting()));

        Map<String, Product> productsMap = new HashMap<>();

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Long> entry : qtyByBarcode.entrySet()) {
            Product product = productRepository.findByBarcode(entry.getKey())
                    .orElseThrow(() -> new ProductNotFoundException("Produit introuvable"));

            productsMap.put(entry.getKey(), product);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        Payment payment = new Payment();
        payment.setType(paymentType);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setEmployee(employee);
        payment.setAmount(total);
        log.info("Montant total du paiement : {} €", total);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Paiement enregistré avec ID : {}", savedPayment.getId());

        for (Map.Entry<String, Long> entry : qtyByBarcode.entrySet()) {
            Product product = productsMap.get(entry.getKey());
            if (product.getQuantity() < entry.getValue()) {
                throw new IllegalStateException("Stock insuffisant pour : " + product.getName());
            }
            product.setQuantity(product.getQuantity() - entry.getValue().intValue());
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
