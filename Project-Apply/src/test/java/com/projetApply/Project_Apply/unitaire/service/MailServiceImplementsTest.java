package com.projetApply.Project_Apply.unitaire.service;

import com.projetApply.Project_Apply.service.MailServiceImplements;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailServiceImplementsTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailServiceImplements mailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    // Extraction récursive du texte
    private String extractAllText(Part part) throws Exception {
        Object content = part.getContent();
        if (content instanceof String s) {
            return s;
        }
        if (content instanceof Multipart mp) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mp.getCount(); i++) {
                sb.append(extractAllText(mp.getBodyPart(i)));
            }
            return sb.toString();
        }
        if (content instanceof Message nested) {
            return extractAllText(nested);
        }
        return "";
    }

    private String dumpRawMime(Message message) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        return baos.toString(StandardCharsets.UTF_8);
    }

    @Test
    void sendMail_shouldSendSuccessfully() throws Exception {
        mailService.sendMail("to@example.com", "from@example.com", "Subject", "Body");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        MimeMessage sent = captor.getValue();

        assertEquals("to@example.com",
                ((InternetAddress) sent.getRecipients(Message.RecipientType.TO)[0]).getAddress());
        assertEquals("from@example.com",
                ((InternetAddress) sent.getFrom()[0]).getAddress());
        assertEquals("Subject", sent.getSubject());

        String body = extractAllText(sent);
        if (!body.contains("Body")) {
            fail("Le contenu du mail ne contient pas 'Body'\n--- BODY ---\n" + body +
                    "\n--- RAW ---\n" + dumpRawMime(sent));
        }
    }

    @Test
    void sendMailWithAttachment_shouldSendSuccessfully() throws Exception {
        byte[] attachment = "fichier test".getBytes();

        mailService.sendMailWithAttachment("to@example.com", "from@example.com", "Subject", "Body", attachment,
                "test.txt");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        MimeMessage sent = captor.getValue();

        assertEquals("to@example.com",
                ((InternetAddress) sent.getRecipients(Message.RecipientType.TO)[0]).getAddress());
        assertEquals("from@example.com",
                ((InternetAddress) sent.getFrom()[0]).getAddress());
        assertEquals("Subject", sent.getSubject());

        String body = extractAllText(sent);
        if (!body.contains("Body")) {
            fail("Le contenu du mail avec PJ ne contient pas 'Body'\n--- BODY ---\n" + body +
                    "\n--- RAW ---\n" + dumpRawMime(sent));
        }
    }

}
