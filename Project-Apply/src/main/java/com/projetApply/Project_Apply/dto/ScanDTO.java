package com.projetApply.Project_Apply.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les données d’un scan effectué par un utilisateur.
 * 
 * Ce DTO contient :
 * - l’identifiant du scan,
 * - l’identifiant et le nom de l’utilisateur,
 * - le code-barres du produit scanné,
 * - la date du scan,
 * - les infos du produit scanné (sous forme de ProductDTO).
 * 
 * Utilisé pour afficher l’historique des scans ou les associer à un paiement.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanDTO {

    private int id;

    private int userId;

    private String userName;

    private String codeBarre;

    private Timestamp dateScan;

    private ProductDTO product;

}
