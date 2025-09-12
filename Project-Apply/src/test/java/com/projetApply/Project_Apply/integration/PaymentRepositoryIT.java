package com.projetApply.Project_Apply.integration;

import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.ProductRepository;
import com.projetApply.Project_Apply.repository.ScanRepository;
import com.projetApply.Project_Apply.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan(basePackages = "com.projetApply.Project_Apply.model")
public class PaymentRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScanRepository scanRepository;

    @Test
    void testSaveAndFindProductWithScan() {

        User user = new User();
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setPassword("123");
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        Product product = new Product();
        product.setName("Steak");
        product.setBarcode("1478529632587");
        product.setQuantity(4);
        product.setWeight(300);
        product.setPrice(new BigDecimal("2.50"));
        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct.getId());

        Scan scan = new Scan();
        scan.setUser(savedUser);
        scan.setProduct(savedProduct);
        scan.setDateScan(new Timestamp(System.currentTimeMillis()));
        Scan savedScan = scanRepository.save(scan);
        assertNotNull(savedScan.getId());

        List<Product> allProducts = productRepository.findAll();
        assertFalse(allProducts.isEmpty());
        assertEquals("Steak", allProducts.get(0).getName());

        List<Scan> allScans = scanRepository.findAll();
        assertFalse(allScans.isEmpty());
        assertEquals(savedProduct.getId(), allScans.get(0).getProduct().getId());
        assertEquals(savedUser.getId(), allScans.get(0).getUser().getId());
    }
}
