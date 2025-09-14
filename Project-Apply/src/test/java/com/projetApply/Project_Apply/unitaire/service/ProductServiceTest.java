package com.projetApply.Project_Apply.unitaire.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;
import com.projetApply.Project_Apply.mapper.ProductMapper;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.repository.ProductRepository;
import com.projetApply.Project_Apply.service.ProductService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Bœuf");
        product.setBarcode("123456789");
        product.setQuantity(20);
        product.setWeight(100);
        product.setPrice(new BigDecimal("5.00"));
    }

    @Test
    void testAddProduct_existingProduct_updateFields() {
        Product existing = new Product();
        existing.setBarcode("123456789");
        existing.setQuantity(5);
        existing.setName("Ancien nom");
        existing.setPrice(new BigDecimal("2.00"));
        existing.setWeight(50);

        when(productRepository.findByBarcode("123456789")).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);
        when(productMapper.toDTO(any())).thenReturn(new ProductDTO());

        ProductDTO result = productService.addProduct("123456789", "Bœuf", 10, 100, new BigDecimal("5.00"));
        assertNotNull(result);
        verify(productRepository).save(existing);
    }

    @Test
    void testAddProduct_categoryAutre() {
        when(productRepository.findByBarcode("000")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDTO(any())).thenReturn(new ProductDTO());

        ProductDTO result = productService.addProduct("000", "Mystère", 10, 100, new BigDecimal("1.00"));
        assertNotNull(result);
        verify(productRepository).save(any());
    }

    @Test
    void testAddProduct_stockFaible() {
        when(productRepository.findByBarcode("111")).thenReturn(Optional.empty());
        product.setQuantity(3);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDTO(any())).thenReturn(new ProductDTO());

        ProductDTO result = productService.addProduct("111", "Porc", 3, 100, new BigDecimal("2.00"));
        assertNotNull(result);
    }

    @Test
    void testRemoveProduct_success() {
        product.setQuantity(5);
        when(productRepository.findByBarcode("123")).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toDTO(any())).thenReturn(new ProductDTO());

        ProductDTO result = productService.removeProduct("123");
        assertNotNull(result);
    }

    @Test
    void testRemoveProduct_notFound() {
        when(productRepository.findByBarcode("999")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.removeProduct("999"));
    }

    @Test
    void testRemoveProduct_stockEmpty() {
        product.setQuantity(0);
        when(productRepository.findByBarcode("123")).thenReturn(Optional.of(product));
        assertThrows(IllegalStateException.class, () -> productService.removeProduct("123"));
    }

    @Test
    void testUpdateAllStatuses() {
        Product p1 = new Product();
        p1.setQuantity(0);
        Product p2 = new Product();
        p2.setQuantity(3);
        Product p3 = new Product();
        p3.setQuantity(10);
        Product p4 = new Product();
        p4.setQuantity(20);

        List<Product> products = Arrays.asList(p1, p2, p3, p4);
        when(productRepository.findAll()).thenReturn(products);

        productService.updateAllStatuses();
        verify(productRepository, times(4)).save(any());
    }

    @Test
    void testGetTotalStock() {
        Product p1 = new Product();
        p1.setQuantity(10);
        Product p2 = new Product();
        p2.setQuantity(20);
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        int total = productService.getTotalStock();
        assertEquals(30, total);
    }
}
