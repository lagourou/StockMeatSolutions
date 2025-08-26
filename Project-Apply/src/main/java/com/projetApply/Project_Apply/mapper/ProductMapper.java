package com.projetApply.Project_Apply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "barcode", ignore = true)
    @Mapping(target = "scans", ignore = true)
    Product toEntity(ProductDTO productDTO);

    ProductDTO toDTO(Product product);
}
