package com.projetApply.Project_Apply.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Représente un produit dans le stock.
 * 
 * Cette entité contient :
 * - le nom, le code-barres, le poids et le prix du produit,
 * - la quantité disponible en stock,
 * - le statut du stock (ex : "Stock faible", "Rupture de stock"),
 * - la catégorie (ex : "Viande rouge", "Volaille"),
 * - la liste des scans associés à ce produit.
 */
@Entity
@Table(name = "Product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "weight")
    private int weight;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    private String status;

    private String category;

    @OneToMany(mappedBy = "product")
    private List<Scan> scans;

}
