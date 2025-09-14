package com.projetApply.Project_Apply.unitaire.controller;

import com.projetApply.Project_Apply.controller.MailController;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MailControllerTest {

    @Mock
    private MailService mailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private MailController mailController;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void showForgotPassword_returnsView() {
        String view = mailController.showForgotPassword();
        assertEquals("forget-password", view);
    }

    @Test
    void sendMailForgotPassword_shouldShowErrorIfUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        String view = mailController.sendMailForgotPassword("test@example.com", model);

        verify(model).addAttribute("message", "Aucun utilisateur trouvé avec cet email.");
        assertEquals("forget-password", view);
    }

    @Test
    void sendMailForgotPassword_shouldShowWarningIfTokenStillValid() {
        User user = new User();
        user.setUsername("TestUser");
        user.setResetToken("token");
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String view = mailController.sendMailForgotPassword("test@example.com", model);

        verify(model).addAttribute("message", "Un lien de réinitialisation est déjà actif. Vérifiez votre boîte mail.");
        assertEquals("forget-password", view);
    }

    @Test
    void sendMailForgotPassword_shouldSendMailSuccessfully() {
        User user = new User();
        user.setUsername("TestUser");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String view = mailController.sendMailForgotPassword("test@example.com", model);

        verify(mailService).sendMail(
                eq("test@example.com"),
                eq("noreply@stockmeatsolutions.site"),
                contains("Réinitialisation"),
                contains("Réinitialiser le mot de passe"));
        verify(model).addAttribute("message", "Mail envoyé avec succès !");
        assertEquals("forget-password", view);
    }

    @Test
    void sendMailForgotPassword_shouldHandleMailExceptionGracefully() {
        User user = new User();
        user.setUsername("TestUser");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Erreur SMTP"))
                .when(mailService)
                .sendMail(anyString(), anyString(), anyString(), anyString());

        String view = mailController.sendMailForgotPassword("test@example.com", model);

        verify(model).addAttribute("message", "Échec de l'envoi du mail.");
        assertEquals("forget-password", view);
    }

    @Test
    void showResetPasswordForm_shouldReturnErrorForInvalidToken() {
        when(userRepository.findByResetToken("invalid")).thenReturn(Optional.empty());

        String view = mailController.showResetPasswordForm("invalid", model);

        verify(model).addAttribute("message", "Token invalide.");
        assertEquals("error", view);
    }

    @Test
    void showResetPasswordForm_shouldReturnErrorIfTokenExpired() {
        User user = new User();
        user.setTokenExpiration(LocalDateTime.now().minusHours(1));

        when(userRepository.findByResetToken("token")).thenReturn(Optional.of(user));

        String view = mailController.showResetPasswordForm("token", model);

        verify(model).addAttribute("message", "Le lien de réinitialisation a expiré.");
        assertEquals("error", view);
    }

    @Test
    void showResetPasswordForm_shouldShowResetForm() {
        User user = new User();
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetToken("token")).thenReturn(Optional.of(user));

        String view = mailController.showResetPasswordForm("token", model);

        verify(model).addAttribute("token", "token");
        assertEquals("reset-password", view);
    }

    @Test
    void passwordEntry_shouldShowErrorIfPasswordsDoNotMatch() {
        String view = mailController.passwordEntry("token", "pass1", "pass2", model);
        verify(model).addAttribute("message", "Les mots de passe ne correspondent pas");
        verify(model).addAttribute("token", "token");
    }

    @Test
    void passwordEntry_shouldReturnErrorIfTokenInvalid() {
        when(userRepository.findByResetToken("invalid")).thenReturn(Optional.empty());

        String view = mailController.passwordEntry("invalid", "pass", "pass", model);

        verify(model).addAttribute("message", "Token invalide");
        assertEquals("error", view);
    }

    @Test
    void passwordEntry_shouldReturnErrorIfTokenExpired() {
        User user = new User();
        user.setTokenExpiration(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByResetToken("token")).thenReturn(Optional.of(user));

        String view = mailController.passwordEntry("token", "pass", "pass", model);

        verify(model).addAttribute("message", "Lien de réinitialisation a expiré");
        assertEquals("error", view);
    }

    @Test
    void passwordEntry_shouldResetPasswordSuccessfully() {
        User user = new User();
        user.setTokenExpiration(LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetToken("token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        String view = mailController.passwordEntry("token", "pass", "pass", model);

        assertEquals("redirect:/login?resetSuccess", view);
        verify(userRepository).save(
                argThat(savedUser -> savedUser.getResetToken() == null && savedUser.getPassword().equals("encoded")));
    }
}
