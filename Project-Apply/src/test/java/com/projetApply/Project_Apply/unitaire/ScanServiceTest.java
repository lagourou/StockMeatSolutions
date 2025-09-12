package com.projetApply.Project_Apply.unitaire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.ProductRepository;
import com.projetApply.Project_Apply.repository.ScanRepository;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.ScanService;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.mapper.ScanMapper;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ScanServiceTest {

    @Mock
    private ScanRepository scanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ScanMapper scanMapper;

    @InjectMocks
    private ScanService scanService;

    private Scan scan;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setUsername("testUser");

        product = new Product();
        product.setBarcode("9876543210987");
        product.setName("Côte de porc");

        scan = new Scan();
        scan.setId(1);
        scan.setUser(user);
        scan.setProduct(product);
        scan.setDateScan(new Timestamp(System.currentTimeMillis()));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findByBarcode("9876543210987")).thenReturn(Optional.of(product));

    }

    @Test
    void testFindById() {
        when(scanRepository.findById(1)).thenReturn(Optional.of(scan));
        when(scanMapper.toDTO(scan)).thenReturn(new ScanDTO(
                scan.getId(),
                user.getId(),
                user.getUsername(),
                product.getBarcode(),
                scan.getDateScan(),
                null));

        Optional<Scan> found = scanRepository.findById(1);
        assertTrue(found.isPresent());
        assertEquals("testUser", found.get().getUser().getUsername());
        assertEquals("9876543210987", found.get().getProduct().getBarcode());
    }

    @Test
    void testSaveScanDTOUsingService() {
        when(scanRepository.save(any(Scan.class))).thenReturn(scan);
        when(scanMapper.toDTO(scan)).thenReturn(new ScanDTO(
                scan.getId(),
                user.getId(),
                user.getUsername(),
                product.getBarcode(),
                scan.getDateScan(),
                null));

        ScanDTO result = scanService.saveScanDTO(1, "9876543210987");
        assertEquals("testUser", result.getUserName());
        assertEquals("9876543210987", result.getCodeBarre());
        assertNotNull(result.getDateScan());
    }
}
