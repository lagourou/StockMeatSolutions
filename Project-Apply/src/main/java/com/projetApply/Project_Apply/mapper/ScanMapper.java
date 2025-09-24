package com.projetApply.Project_Apply.mapper;

import org.springframework.stereotype.Component;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.model.Scan;
import lombok.RequiredArgsConstructor;

/**
 * Classe utilitaire pour convertir un objet Scan en ScanDTO.
 * 
 * Elle permet de :
 * - transformer un scan (fait par un utilisateur sur un produit) en DTO,
 * - inclure les infos du produit scanné grâce à ProductMapper.
 * 
 * Utile pour afficher les données de scan dans l’interface ou les transmettre.
 */
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
