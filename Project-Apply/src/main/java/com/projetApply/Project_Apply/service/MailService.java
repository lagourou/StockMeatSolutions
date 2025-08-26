package com.projetApply.Project_Apply.service;

public interface MailService {

    void sendMail(String to, String from, String subject, String body);

    void sendMailWithAttachment(String to, String from, String subject, String body, byte[] attachment,
            String filename);

}
