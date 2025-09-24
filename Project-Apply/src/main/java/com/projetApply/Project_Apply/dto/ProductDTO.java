package com.projetApply.Project_Apply.dto;

import java.math.BigDecimal;

import org.springframework.format.annotation.NumberFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les données d’un produit à afficher ou transférer.
 * 
 * Ce DTO contient :
 * - le nom, le code-barres, le poids et le prix du produit,
 * - la quantité en stock,
 * - le statut du stock (ex : "Stock faible"),
 * - la catégorie du produit (ex : "Viande rouge").
 * 
 * Utilisé pour afficher les produits ou les envoyer dans des formulaires.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private int id;

    private String name;

    private String barcode;

    private int quantity;

    private int weight;

    @DecimalMin(value = "0.1")
    @Digits(integer = 10, fraction = 2)
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private BigDecimal price;

    private String status;

    private String category;
}
