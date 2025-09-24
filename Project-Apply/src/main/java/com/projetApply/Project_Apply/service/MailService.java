package com.projetApply.Project_Apply.service;

/**
 * Interface pour l’envoi de mails dans l’application.
 * 
 * Elle définit deux méthodes :
 * - envoyer un mail simple,
 * - envoyer un mail avec une pièce jointe.
 * 
 * Cette interface est implémentée par MailServiceImplements.
 */
public interface MailService {
    /**
     * Envoie un mail simple avec un sujet et un contenu.
     * 
     * @param to      adresse du destinataire
     * @param from    adresse de l’expéditeur
     * @param subject sujet du mail
     * @param body    contenu du mail
     */
    void sendMail(String to, String from, String subject, String body);

    /**
     * Envoie un mail avec une pièce jointe.
     * 
     * @param to         adresse du destinataire
     * @param from       adresse de l’expéditeur
     * @param subject    sujet du mail
     * @param body       contenu du mail
     * @param attachment contenu du fichier à joindre (sous forme de tableau de
     *                   bytes)
     * @param filename   nom du fichier joint
     */
    void sendMailWithAttachment(String to, String from, String subject, String body, byte[] attachment,
            String filename);

}
