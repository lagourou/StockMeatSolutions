package com.projetApply.Project_Apply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.model.Product;

/**
 * Interface utilisée pour convertir automatiquement entre Product et
 * ProductDTO.
 * 
 * Elle permet de :
 * - transformer un DTO en entité Product (pour enregistrer en base),
 * - transformer une entité Product en DTO (pour afficher ou transférer les
 * données).
 * 
 * Le champ "scans" est ignoré lors de la conversion vers l'entité, car il n'est
 * pas utile dans le DTO.
 * 
 * Cette interface utilise MapStruct pour générer le code de conversion
 * automatiquement.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "scans", ignore = true)
    Product toEntity(ProductDTO productDTO);

    ProductDTO toDTO(Product product);
}
