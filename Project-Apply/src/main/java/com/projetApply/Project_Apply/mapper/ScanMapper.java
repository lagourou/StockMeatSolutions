package com.projetApply.Project_Apply.mapper;

import org.springframework.stereotype.Component;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.model.Scan;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScanMapper {

    private final ProductMapper productMapper;

    public ScanDTO toDTO(Scan scan) {
        if (scan == null)
            return null;

        ProductDTO productDTO = productMapper.toDTO(scan.getProduct());

        return new ScanDTO(
                scan.getId(),
                scan.getUser().getId(),
                scan.getUser().getUsername(),
                scan.getProduct().getBarcode(),
                scan.getDateScan(),
                productDTO);
    }
}
