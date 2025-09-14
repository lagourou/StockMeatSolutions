package com.projetApply.Project_Apply.unitaire.mapper;

import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.mapper.UserMapper;
import com.projetApply.Project_Apply.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void toEntity_shouldMapDtoToEntity() {
        UserDTO dto = new UserDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("secret");

        User entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("secret", entity.getPassword());

        // Adaptation selon le type de id
        assertEquals(0, entity.getId());
        assertNull(entity.getScans());
    }

    @Test
    void toDTO_shouldMapEntityToDto() {
        User entity = new User();
        entity.setEmail("test@example.com");
        entity.setPassword("secret");

        UserDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("secret", dto.getPassword());
    }
}
