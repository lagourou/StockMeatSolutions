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

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

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

    public ProductDTO getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produit avec ID " + id + " introuvable."));
        return productMapper.toDTO(product);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public int getTotalStock() {
        return productRepository.findAll()
                .stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }

}
