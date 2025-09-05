package com.projetApply.Project_Apply.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/forget-password")
    public String showForgotPassword() {

        return "forget-password";

    }

    @PostMapping("/forget-password")
    public String sendMailForgotPassword(@RequestParam("email") String email, Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("message", "Aucun utilisateur trouvé avec cet email.");
            return "forget-password";
        }

        User user = optionalUser.get();

        if (user.getResetToken() != null && user.getTokenExpiration().isAfter(LocalDateTime.now())) {

            model.addAttribute("message", "Un lien de réinitialisation est déjà actif. Vérifiez votre boîte mail.");
            return "forget-password";
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusHours(24);

        user.setResetToken(token);
        user.setTokenExpiration(expiration);
        userRepository.save(user);

        String resetLink = "https://www.stockmeatsolutions.site/reset-password?token=" + token;
        String subject = "Réinitialisation de votre mot de passe";
        String body = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                + "<div style='text-align: center;'><img src='https://www.stockmeatsolutions.site/images/logo-stock.png' alt='Logo StockMeat' style='width: 120px; height: auto; margin-bottom: 20px;' />"
                + "<h2 style='color: #007bff;'>Réinitialisation de votre mot de passe</h2>"
                + "<p>Bonjour " + user.getUsername() + ",</p>"
                + "<p>Vous avez demandé à réinitialiser votre mot de passe. Cliquez sur le bouton ci-dessous :</p>"
                + "<p><a href='" + resetLink
                + "' style='background-color: #007bff; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;'>Réinitialiser le mot de passe</a></p>"
                + "<p style='font-size: 12px; color: #888;'>Ce lien est valable pendant 24 heures.</p>"
                + "<hr><p style='font-size: 12px;'>StockMeat Solutions • Ne pas répondre à ce mail</p>"
                + "</div>";

        try {
            mailService.sendMail(email, "noreply@stockmeatsolutions.site", subject, body);
            model.addAttribute("message", "Mail envoyé avec succès !");
        } catch (Exception e) {
            model.addAttribute("message", "Échec de l'envoi du mail.");
        }

        System.out.println("Lien envoyé" + resetLink);
        return "forget-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        System.out.println("Token reçu" + token);

        Optional<User> optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            model.addAttribute("message", "Token invalide.");
            return "error";
        }

        User user = optionalUser.get();

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Le lien de réinitialisation a expiré.");
            return "error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String passwordEntry(@RequestParam("token") String token, @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword, Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Les mots de passe ne correspondent pas");
            model.addAttribute("token", token);
        }
        Optional<User> optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            model.addAttribute("message", "Token invalide");
            return "error";
        }

        User user = optionalUser.get();
        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Lien de réinitialisation a expiré");
            return "error";
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        model.addAttribute("message", "Mot de passe réinitialisé avec succès !");
        return "redirect:/login?resetSuccess";

    }

}
