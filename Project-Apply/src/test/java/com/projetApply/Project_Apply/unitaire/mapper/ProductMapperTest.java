package com.projetApply.Project_Apply.unitaire.mapper;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.mapper.ProductMapper;
import com.projetApply.Project_Apply.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class ProductMapperTest {

    private final ProductMapper mapper = ProductMapper.INSTANCE;

    @Test
    void toEntity_shouldMapAllFieldsExceptScans() {
        // Arrange
        ProductDTO dto = new ProductDTO();
        dto.setId(1);
        dto.setName("Test Product");
        dto.setBarcode("133588");
        dto.setQuantity(9);
        dto.setWeight(100);
        dto.setPrice(new BigDecimal("1.50"));
        dto.setStatus("Produit disponible");
        dto.setCategory("Category");

        // Act
        Product entity = mapper.toEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getBarcode(), entity.getBarcode());
        assertEquals(dto.getQuantity(), entity.getQuantity());
        assertEquals(dto.getWeight(), entity.getWeight());
        assertEquals(dto.getPrice(), entity.getPrice());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getCategory(), entity.getCategory());
        assertNull(entity.getScans(), "Le champ scans doit être ignoré");
    }

    @Test
    void toDTO_shouldMapAllFields() {
        // Arrange
        Product entity = new Product();
        entity.setId(2);
        entity.setName("Another Product");
        entity.setBarcode("585832");
        entity.setQuantity(0);
        entity.setWeight(100);
        entity.setPrice(new BigDecimal(1.50));
        entity.setCategory("Another Category");
        entity.setStatus("Rupture de Stock");
        // Act
        ProductDTO dto = mapper.toDTO(entity);

        // Assert
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getBarcode(), dto.getBarcode());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getQuantity(), dto.getQuantity());
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getCategory(), dto.getCategory());
        assertEquals(entity.getWeight(), dto.getWeight());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toDTO_shouldReturnNull_whenEntityIsNull() {
        assertNull(mapper.toDTO(null));
    }
}
