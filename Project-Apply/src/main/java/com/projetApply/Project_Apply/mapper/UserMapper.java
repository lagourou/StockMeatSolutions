package com.projetApply.Project_Apply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.model.User;

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
