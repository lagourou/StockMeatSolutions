package com.projetApply.Project_Apply.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.projetApply.Project_Apply.model.PaymentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les données d’un paiement à afficher ou transférer.
 * 
 * Ce DTO contient :
 * - l’identifiant du paiement,
 * - le montant payé,
 * - le type de paiement (carte ou espèces),
 * - la date du paiement,
 * - le statut du paiement (ex : validé, en attente),
 * - l’identifiant de l’employé qui a validé le paiement.
 * 
 * Utilisé pour envoyer ou recevoir les infos de paiement sans exposer l’entité
 * complète.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private int id;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private PaymentType type;

    private LocalDateTime paymentDate;

    @NotNull
    private String status;

    @Min(1)
    private int employeeId;

}
