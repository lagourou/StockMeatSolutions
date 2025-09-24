package com.projetApply.Project_Apply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.model.User;

/**
 * Interface utilisée pour convertir automatiquement entre User et UserDTO.
 * 
 * Elle permet de :
 * - transformer un DTO en entité User (pour enregistrer en base),
 * - transformer une entité User en DTO (pour afficher ou transférer les
 * données).
 * 
 * Certains champs sont ignorés lors de la conversion vers l’entité :
 * - id : généré automatiquement,
 * - scans : non inclus dans le DTO,
 * - resetToken et tokenExpiration : gérés séparément.
 * 
 * Cette interface utilise MapStruct pour générer le code de conversion
 * automatiquement.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scans", ignore = true)
    @Mapping(target = "tokenExpiration", ignore = true)
    @Mapping(target = "resetToken", ignore = true)
    User toEntity(UserDTO dto);

    UserDTO toDTO(User user);

}
