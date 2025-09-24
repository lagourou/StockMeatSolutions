package com.projetApply.Project_Apply.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer le paiement des produits scannés.
 * 
 * Il permet de :
 * - afficher la page de paiement avec les produits scannés,
 * - calculer le montant total à payer,
 * - confirmer le paiement et générer la facture,
 * - vider la session après paiement.
 * 
 * Utilise :
 * - PaymentService pour traiter le paiement et envoyer la facture,
 * - UserRepository pour retrouver l’employé connecté,
 * - SessionAttributes pour garder les produits scannés en mémoire pendant la
 * session.
 */
@Controller
@RequestMapping("payment")
@RequiredArgsConstructor
@SessionAttributes("scannedProducts")
@Slf4j
public class PaymentController {

        private final UserRepository userRepository;
        private final PaymentService paymentService;

        @GetMapping()
        public String showPaymentPage(@ModelAttribute("scannedProducts") List<ProductDTO> scannedProducts,
                        Model model) {
                if (scannedProducts == null)
                        scannedProducts = new ArrayList<>();

                BigDecimal total = scannedProducts.stream()
                                .map(ProductDTO::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                model.addAttribute("totalAmount", total);
                model.addAttribute("products", scannedProducts);

                return "payment";
        }

        @PostMapping("/confirm")
        public String confirmPayment(@ModelAttribute("scannedProducts") List<ProductDTO> scannedProducts,
                        Model model, PaymentType paymentType, SessionStatus status) {
                try {
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        UserDetailsImplements userDetails = (UserDetailsImplements) auth.getPrincipal();
                        User employee = userRepository.findById(userDetails.getId())
                                        .orElseThrow(() -> new RuntimeException("Employé introuvable"));

                        paymentService.processPayment(employee, paymentType, scannedProducts);

                        status.setComplete();

                        model.addAttribute("message", "Facture envoyée avec succès !");
                        model.addAttribute("products", new ArrayList<>());
                        model.addAttribute("totalAmount", BigDecimal.ZERO);

                } catch (Exception e) {
                        log.error("Erreur paiement : {}", e.getMessage(), e);
                        model.addAttribute("message", "Échec du paiement : " + e.getMessage());
                }

                return "payment";
        }

        @ModelAttribute("scannedProducts")
        public List<ProductDTO> scannedProducts() {
                return new ArrayList<>();
        }
}
