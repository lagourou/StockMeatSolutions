package com.projetApply.Project_Apply.model;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Représente un scan effectué par un utilisateur sur un produit.
 * 
 * Cette entité contient :
 * - l’utilisateur qui a scanné,
 * - le produit concerné,
 * - la date du scan,
 * - le paiement associé (si le produit a été acheté).
 */
@Entity
@Table(name = "scan")
@Data
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "date_scan", nullable = false)
    private Timestamp dateScan;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

}
