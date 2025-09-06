package com.projetApply.Project_Apply.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.repository.PaymentRepository;
import com.projetApply.Project_Apply.service.InvoiceService;
import com.projetApply.Project_Apply.service.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@SessionAttributes("scannedProducts")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final MailService mailService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/send-invoice")
    public String sendInvoice(@RequestParam("paymentId") int paymentId, Model model, SessionStatus status) {

        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);

        if (optionalPayment.isEmpty()) {
            model.addAttribute("message", "Paiement introuvable.");
            return "invoice";
        }

        Payment payment = optionalPayment.get();
        byte[] pdf = invoiceService.generateInvoicePDF(payment);

        String to = payment.getEmployee().getEmail();
        String subject = "Votre facture";
        String body = "Veuillez trouver ci-joint votre facture.";
        String filename = "facture_" + payment.getId() + ".pdf";

        try {
            mailService.sendMailWithAttachment(to, "noreply@stockmeatsolutions.site", subject, body, pdf, filename);
            model.addAttribute("message", "Facture envoyée avec succès !");
        } catch (Exception e) {
            model.addAttribute("message", "Échec de l'envoi de la facture.");
        }

        status.setComplete();
        return "invoice";
    }

}
