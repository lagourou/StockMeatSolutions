package com.projetApply.Project_Apply.mapper;

import com.projetApply.Project_Apply.dto.PaymentDTO;
import com.projetApply.Project_Apply.model.Payment;
import com.projetApply.Project_Apply.model.User;

/**
 * Classe utilitaire pour convertir les objets Payment.
 * 
 * Elle permet de :
 * - transformer un objet Payment (entité) en PaymentDTO (données à afficher ou
 * transférer),
 * - reconstruire un objet Payment à partir d’un DTO et d’un utilisateur.
 * 
 * Utile pour séparer les données internes (base de données) des données
 * visibles ou envoyées.
 */
public class PaymentMapper {
    /**
     * Convertit un objet Payment en PaymentDTO.
     * 
     * @param payment l’objet Payment à convertir
     * @return un DTO contenant les infos du paiement
     */
    public static PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setType(payment.getType());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setEmployeeId(payment.getEmployee().getId());
        return dto;
    }

    /**
     * Reconstruit un objet Payment à partir d’un DTO et d’un utilisateur.
     * 
     * @param dto      les données du paiement
     * @param employee l’utilisateur qui a effectué le paiement
     * @return un objet Payment prêt à être enregistré
     */
    public static Payment toEntity(PaymentDTO dto, User employee) {
        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setAmount(dto.getAmount());
        payment.setType(dto.getType());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setEmployee(employee);
        return payment;
    }

}
