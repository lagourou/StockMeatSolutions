package com.projetApply.Project_Apply.unitaire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.projetApply.Project_Apply.dto.ProductDTO;
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

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1);
        product.setName("Steak");
    }

    @Test
    void testFindById() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(
                new ProductDTO(1, "Steak", "1485255645936", 10, 100, new BigDecimal("2.50"), "Stock disponible",
                        "Viande rouge"));
        ProductDTO result = productService.getProductById(1);
        assertEquals("Steak", result.getName());
    }

    @Test
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
        when(productMapper.toDTO(product)).thenReturn(
                new ProductDTO(1, "Steak", "1485255645936", 10, 100, new BigDecimal("2.50"), "Stock disponible",
                        "Viande rouge"));
        List<ProductDTO> products = productService.getAllProducts();
        assertFalse(products.isEmpty());
    }

    @Test
    void testSave() {
        when(productRepository.findByBarcode("1485755445736")).thenReturn(Optional.empty());
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(
                new ProductDTO(1, "Poulet", "1485755445736", 10, 300, new BigDecimal("1.50"), "Stock disponible",
                        "Volaille"));

        ProductDTO result = productService.addProduct("1485755445736", "Poulet", 10, 300, new BigDecimal("1.50"));
        assertEquals("Poulet", result.getName());
    }

}
