package com.projetApply.Project_Apply.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.repository.ScanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final ScanRepository scanRepository;

    public byte[] generateInvoicePDF(Payment payment) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Image logo = Image.getInstance("src/main/resources/images/logo-stock.png");
            logo.scaleAbsolute(120, 60);
            document.add(logo);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            Paragraph title = new Paragraph("Facture - StockMeat Solutions", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            String numeroFacture = String.format("F%04d-%06d", Year.now().getValue(), payment.getId());
            document.add(new Paragraph("Facture n° " + numeroFacture, infoFont));

            document.add(new Paragraph("Employé : " + payment.getEmployee().getEmail(), infoFont));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String date = payment.getPaymentDate().format(formatter);
            document.add(new Paragraph("Date : " + date, infoFont));
            document.add(new Paragraph("Mode de paiement : " + payment.getType(), infoFont));

            String paymentMessage;
            switch (payment.getType()) {
                case CARD ->
                    paymentMessage = "Votre règlement se fera par carte à l'accueil et vous pourrez récupérer vos viandes.";
                case CASH ->
                    paymentMessage = "Votre règlement se fera par espèces à l'accueil et vous pourrez récupérer vos viandes.";
                default -> paymentMessage = "Mode de paiement non reconnu.";
            }
            document.add(new Paragraph(paymentMessage, infoFont));
            document.add(new Paragraph(" "));

            Map<Product, Integer> productQuantity = new LinkedHashMap<>();

            List<Scan> scans = scanRepository.findByPayment(payment);
            for (Scan scan : scans) {
                Product produit = scan.getProduct();
                productQuantity.merge(produit, 1, Integer::sum);
            }

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            table.addCell(new Paragraph("Produit", headerFont));
            table.addCell(new Paragraph("Quantité", headerFont));
            table.addCell(new Paragraph("Prix Unitaire", headerFont));
            table.addCell(new Paragraph("Total", headerFont));

            for (Map.Entry<Product, Integer> entry : productQuantity.entrySet()) {
                Product p = entry.getKey();
                int quantity = entry.getValue();

                String unitPrice = String.format("%.2f €", p.getPrice().doubleValue());
                String total = String.format("%.2f €",
                        p.getPrice().multiply(BigDecimal.valueOf(quantity)).doubleValue());

                table.addCell(p.getName());
                table.addCell(String.valueOf(quantity));
                table.addCell(unitPrice);
                table.addCell(total);

            }

            document.add(table);

            document.add(new LineSeparator());
            document.add(new Paragraph("Montant total : " + payment.getAmount() + " €", totalFont));

            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph("StockMeat Solutions • Ne pas répondre à ce mail", infoFont));

            document.close();
            return baos.toByteArray();

        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur génération PDF", e);
        }
    }

}
