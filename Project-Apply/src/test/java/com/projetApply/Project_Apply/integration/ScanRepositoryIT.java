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

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan(basePackages = "com.projetApply.Project_Apply.model")
public class ScanRepositoryIT {

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveAndFindScan() {

        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("123");

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        Product product = new Product();
        product.setName("Steak");
        product.setBarcode("1478529632587");
        product.setQuantity(4);
        product.setWeight(300);
        product.setPrice(new java.math.BigDecimal("2.50"));
        Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct.getId());

        Scan scan = new Scan();
        scan.setUser(savedUser);
        scan.setProduct(savedProduct);
        scan.setDateScan(new Timestamp(System.currentTimeMillis()));

        Scan savedScan = scanRepository.save(scan);
        assertNotNull(savedScan.getId());

        Optional<Scan> found = scanRepository.findById(savedScan.getId());
        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getUser().getId());
        assertEquals(savedProduct.getId(), found.get().getProduct().getId());
    }
}
