package com.projetApply.Project_Apply.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;
import com.projetApply.Project_Apply.mapper.ProductMapper;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service qui gère les opérations liées aux produits.
 * 
 * Cette classe permet de :
 * - ajouter ou mettre à jour un produit,
 * - calculer et mettre à jour le statut du stock (faible, moyen, etc.),
 * - supprimer un produit,
 * - récupérer un produit par son ID ou son code-barres,
 * - obtenir la liste complète des produits,
 * - calculer le stock total.
 * 
 * Elle utilise :
 * - ProductRepository pour accéder aux données en base,
 * - ProductMapper pour transformer les objets en format DTO.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    /**
     * Ajoute un nouveau produit ou met à jour un produit existant.
     * Met aussi à jour le statut du stock et la catégorie selon le nom.
     * 
     * @param barcode  code-barres du produit
     * @param name     nom du produit
     * @param quantite quantité à ajouter
     * @param weight   poids du produit
     * @param price    prix du produit
     * @return le produit en format DTO
     */
    public ProductDTO addProduct(String barcode, String name, int quantite, int weight, BigDecimal price) {
        Product product = productRepository.findByBarcode(barcode).orElse(null);

        if (product == null) {
            product = new Product();
            product.setBarcode(barcode);
            product.setName(name);
            product.setQuantity(quantite);
            product.setWeight(weight);
            product.setPrice(price);
        } else {
            product.setQuantity(product.getQuantity() + quantite);

            if (name != null && !name.isBlank()) {
                product.setName(name);
            }
            if (weight != 0 && price.compareTo(BigDecimal.ZERO) != 0) {
                product.setWeight(weight);
                product.setPrice(price);
            }
        }

        if (product.getQuantity() <= 0) {
            product.setStatus("Rupture de stock");
        } else if (product.getQuantity() <= 5) {
            product.setStatus("Stock faible");
        } else if (product.getQuantity() <= 15) {
            product.setStatus("Stock moyen");
        } else {
            product.setStatus("Stock disponible");
        }

        if (name != null) {
            String lowername = name.toLowerCase();
            if (lowername.contains("bœuf")) {
                product.setCategory("Viande rouge");
            } else if (lowername.contains("poulet")) {
                product.setCategory("Volaille");
            } else if (lowername.contains("porc")) {
                product.setCategory("Viande blanche");
            } else {
                product.setCategory("Autre");
            }
        }

        productRepository.save(product);
        return productMapper.toDTO(product);
    }

    /**
     * Met à jour le statut de stock pour tous les produits.
     * (Ex : "Rupture de stock", "Stock faible", etc.)
     */
    public void updateAllStatuses() {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            if (product.getQuantity() <= 0) {
                product.setStatus("Rupture de stock");
            } else if (product.getQuantity() <= 5) {
                product.setStatus("Stock faible");
            } else if (product.getQuantity() <= 15) {
                product.setStatus("Stock moyen");
            } else {
                product.setStatus("Stock disponible");
            }

            productRepository.save(product);
        }
    }

    /**
     * Supprime un produit en fonction de son code-barres.
     * Vérifie que le stock n’est pas déjà épuisé.
     * 
     * @param barcode code-barres du produit
     * @return le produit supprimé en format DTO
     * @throws ProductNotFoundException si le produit n’existe pas
     * @throws IllegalStateException    si le stock est déjà à zéro
     */
    public ProductDTO removeProduct(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(
                        () -> new ProductNotFoundException("Produit avec code-barres " + barcode + " introuvable."));

        if (product.getQuantity() <= 0) {
            throw new IllegalStateException("Stock épuisé pour le Produit : " + product.getName());
        }

        productRepository.save(product);

        return productMapper.toDTO(product);
    }

    /**
     * Récupère un produit à partir de son identifiant.
     * 
     * @param id identifiant du produit
     * @return le produit en format DTO
     * @throws ProductNotFoundException si le produit n’existe pas
     */
    public ProductDTO getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produit avec ID " + id + " introuvable."));
        return productMapper.toDTO(product);
    }

    /**
     * Récupère tous les produits enregistrés dans la base.
     * 
     * @return liste des produits en format DTO
     */
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcule le stock total de tous les produits.
     * 
     * @return somme des quantités de tous les produits
     */
    public int getTotalStock() {
        return productRepository.findAll()
                .stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

}
