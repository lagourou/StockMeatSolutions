package com.projetApply.Project_Apply.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.projetApply.Project_Apply.model.Payment;

@Service
public class InvoiceService {

    public byte[] generateInvoicePDF(Payment payment) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Facture n° " + payment.getId()));
            document.add(new Paragraph("Employé : " + payment.getEmployee()));
            document.add(new Paragraph("Date : " + payment.getPaymentDate()));
            document.add(new Paragraph("Montant : " + payment.getAmount() + " €"));
            document.add(new Paragraph("Mode de paiement : " + payment.getType()));

            document.close();
            return baos.toByteArray();

        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur génération PDF", e);
        }
    }

}
