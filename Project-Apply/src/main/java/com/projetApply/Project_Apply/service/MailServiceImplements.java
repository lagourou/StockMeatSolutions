package com.projetApply.Project_Apply.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.projetApply.Project_Apply.exception.MailSendingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service qui gère l’envoi de mails, avec ou sans pièce jointe.
 * 
 * Cette classe permet de :
 * - envoyer un mail simple (texte, sujet, destinataire),
 * - envoyer un mail avec un fichier attaché (ex : facture PDF),
 * - gérer les erreurs d’envoi et les afficher dans les logs.
 * 
 * Elle utilise :
 * - JavaMailSender pour envoyer les mails,
 * - MimeMessageHelper pour configurer le contenu du message.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImplements implements MailService {

    private final JavaMailSender mailSender;

    /**
     * Envoie un mail simple à un destinataire.
     * 
     * @param to      adresse du destinataire
     * @param from    adresse de l’expéditeur
     * @param subject sujet du mail
     * @param body    contenu du mail (texte HTML ou brut)
     * @throws MailSendingException si l’envoi échoue
     */
    @Override
    public void sendMail(String to, String from, String subject, String body) {
        log.info("Préparation du mail : to={}, from={}, subject={}", to, from, subject);
        log.info("Mot de passe SMTP injecté : {}", System.getenv("SPRING_MAIL_PASSWORD"));

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

        } catch (MessagingException | MailException e) {
            log.error("Échec de l'envoi du mail à {}", to, e);

            throw new MailSendingException("Impossible d'envoyer le mail", e);
        }
    }

    /**
     * Envoie un mail avec une pièce jointe (ex : PDF).
     * 
     * @param to         adresse du destinataire
     * @param from       adresse de l’expéditeur
     * @param subject    sujet du mail
     * @param body       contenu du mail
     * @param attachment contenu du fichier à joindre (sous forme de tableau de
     *                   bytes)
     * @param filename   nom du fichier joint
     * @throws RuntimeException si l’envoi échoue
     */
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
