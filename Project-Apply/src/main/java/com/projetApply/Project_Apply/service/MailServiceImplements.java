package com.projetApply.Project_Apply.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImplements implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(String to, String from, String subject, String body) {

        log.info("Préparation du mail : to={}, from={}, subject={}", to, from, subject);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(body, true);
            log.info("Message MIME configuré avec succès");

            log.info("Tentative d'envoi du mail via JavaMailSender...");
            mailSender.send(message);
            log.info("Mail envoyé avec succès à {}", to);
        } catch (MessagingException e) {

            log.error("Échec de l'envoi du mail à {}", to, e);
            throw new RuntimeException("Erreur lors de l'envoie du mail", e);
        }

    }

    @Override
    public void sendMailWithAttachment(String to, String from, String subject, String body, byte[] attachment,
            String filename) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setText(body);

            ByteArrayResource resource = new ByteArrayResource(attachment);
            helper.addAttachment(filename, resource);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi du mail avec pièce jointe", e);
        }
    }

}
