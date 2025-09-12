package com.projetApply.Project_Apply.integration;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan(basePackages = "com.projetApply.Project_Apply.model")
public class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveAndFindProduct() {
        Product product = new Product();

        product.setName("Steak");
        product.setBarcode("1478529632587");
        product.setQuantity(4);
        product.setWeight(300);
        product.setPrice(new BigDecimal("2.50"));

        Product saved = productRepository.save(product);
        assertNotNull(saved.getId());

        List<Product> all = productRepository.findAll();
        assertFalse(all.isEmpty());
        assertEquals("Steak", all.get(0).getName());
    }
}
