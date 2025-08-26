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

import org.springframework.web.bind.annotation.PostMapping;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.repository.PaymentRepository;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.model.PaymentType;
import com.projetApply.Project_Apply.model.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("payment")
@RequiredArgsConstructor
@SessionAttributes("scannedProducts")
public class PaymentController {

        private final UserRepository userRepository;
        private final PaymentRepository paymentRepository;

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
        public String confirmPayment(@RequestParam PaymentType paymentType,
                        @ModelAttribute("scannedProducts") List<ProductDTO> scannedProducts, Model model) {

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

                return "redirect:/send-invoice?paymentId=" + savePayment.getId();
        }

}
