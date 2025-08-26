package com.projetApply.Project_Apply.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projetApply.Project_Apply.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByBarcode(String barcode);

}
