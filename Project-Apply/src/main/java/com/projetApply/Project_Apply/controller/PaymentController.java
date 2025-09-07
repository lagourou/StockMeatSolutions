package com.projetApply.Project_Apply.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.repository.PaymentRepository;
import com.projetApply.Project_Apply.repository.ProductRepository;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.InvoiceService;
import com.projetApply.Project_Apply.service.MailService;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.ScanRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("payment")
@RequiredArgsConstructor
@SessionAttributes("scannedProducts")
public class PaymentController {

        private final UserRepository userRepository;
        private final PaymentRepository paymentRepository;
        private final ScanRepository scanRepository;
        private final InvoiceService invoiceService;
        private final MailService mailService;
        private final ProductRepository productRepository;

        @GetMapping()
        public String showPaymentPage(@ModelAttribute("scannedProducts") List<ProductDTO> scannedProducts,
                        Model model) {

                if (scannedProducts == null) {
                        scannedProducts = new ArrayList<>();
                }

                BigDecimal total = scannedProducts
                                .stream()
                                .map(ProductDTO::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                model.addAttribute("totalAmount", total);
                model.addAttribute("products", scannedProducts);

                return "payment";

        }

        @PostMapping("/confirm")
        public String confirmPayment(@RequestParam("paymentType") PaymentType paymentType,
                        @ModelAttribute("scannedProducts") List<ProductDTO> scannedProducts, Model model) {

                if (scannedProducts == null || scannedProducts.isEmpty()) {
                        model.addAttribute("message", "Aucun produit à payer.");
                        model.addAttribute("products", new ArrayList<>());
                        model.addAttribute("totalAmount", BigDecimal.ZERO);
                        return "payment";
                }

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetailsImplements userDetails = (UserDetailsImplements) authentication.getPrincipal();
                int employeeId = userDetails.getId();

                BigDecimal total = scannedProducts.stream()
                                .map(ProductDTO::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                User employee = userRepository.findById(employeeId)
                                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

                Payment payment = new Payment();
                payment.setAmount(total);
                payment.setType(paymentType);
                payment.setPaymentDate(LocalDateTime.now());
                payment.setStatus("EN_ATTENTE");
                payment.setEmployee(employee);

                Payment savePayment = paymentRepository.save(payment);

                for (ProductDTO dto : scannedProducts) {
                        Product product = productRepository.findByBarcode(dto.getBarcode())
                                        .orElseThrow(() -> new ProductNotFoundException("Produit introuvable"));

                        if (product.getQuantity() <= 0) {
                                throw new IllegalStateException("Stock épuisé pour le produit : " + product.getName());
                        }
                        product.setQuantity(product.getQuantity() - 1);
                        productRepository.save(product);
                }

                List<Scan> scans = scanRepository.findByUser(employee);

                for (Scan scan : scans) {
                        String barcode = scan.getProduct().getBarcode();

                        boolean match = scannedProducts.stream().anyMatch(dto -> dto.getBarcode().equals(barcode));

                        if (match && scan.getPayment() == null) {
                                scan.setPayment(savePayment);
                                scanRepository.save(scan);
                        }
                }
                byte[] pdf = invoiceService.generateInvoicePDF(savePayment);

                String to = employee.getEmail();
                String subject = "Votre facture";
                String body = "Veuillez trouver ci-joint votre facture.";
                String filename = "facture_" + savePayment.getId() + ".pdf";

                try {
                        mailService.sendMailWithAttachment(to, "noreply@stockmeatsolutions.site", subject, body, pdf,
                                        filename);
                        model.addAttribute("message", "Facture envoyée avec succès !");
                } catch (Exception e) {
                        model.addAttribute("message", "Échec de l'envoi de la facture.");
                }

                model.addAttribute("products", new ArrayList<>());
                model.addAttribute("totalAmount", BigDecimal.ZERO);

                return "payment";

        }

        @ModelAttribute("scannedProducts")
        public List<ProductDTO> scannedProducts() {
                return new ArrayList<>();
        }

}
