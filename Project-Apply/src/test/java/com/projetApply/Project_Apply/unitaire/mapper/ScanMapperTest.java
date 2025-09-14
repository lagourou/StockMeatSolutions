package com.projetApply.Project_Apply.unitaire.mapper;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.mapper.ProductMapper;
import com.projetApply.Project_Apply.mapper.ScanMapper;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;

public class ScanMapperTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ScanMapper scanMapper;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        productMapper = mock(ProductMapper.class);
        scanMapper = new ScanMapper(productMapper);
    }

    @Test
    void testToDTO_shouldMapCorrectly() {
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");

        Product product = new Product();
        product.setBarcode("9876543210987");

        Scan scan = new Scan();
        scan.setId(10);
        scan.setUser(user);
        scan.setProduct(product);
        scan.setDateScan(new Timestamp(System.currentTimeMillis()));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setBarcode("9876543210987");
        productDTO.setName("Côte de porc");

        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ScanDTO dto = scanMapper.toDTO(scan);

        assertNotNull(dto);
        assertEquals(10, dto.getId());
        assertEquals(1, dto.getUserId());
        assertEquals("testUser", dto.getUserName());
        assertEquals("9876543210987", dto.getCodeBarre());
        assertEquals(productDTO, dto.getProduct());
        assertNotNull(dto.getDateScan());
    }

    @Test
    void testToDTO_shouldReturnNullIfScanIsNull() {
        ScanDTO dto = scanMapper.toDTO(null);
        assertNull(dto);
    }
}
