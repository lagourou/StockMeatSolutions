package com.projetApply.Project_Apply.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EntityScan(basePackages = "com.projetApply.Project_Apply.model")
@ActiveProfiles("test")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testSaveAndFindUser() {
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("12345");

        User saved = userRepository.save(user);
        assertNotNull(saved.getId());

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());
    }

    @Test
    void testInsertRawSQL() {
        // Force Hibernate à créer la table
        User dummy = new User();
        dummy.setUsername("dummy");
        dummy.setEmail("dummy@example.com");
        dummy.setPassword("xxx");
        userRepository.save(dummy);

        // Maintenant l'insertion SQL brute fonctionne
        entityManager.createNativeQuery(
                "INSERT INTO users (email, password, username) VALUES ('a@b.com', 'pass', 'test')")
                .executeUpdate();

        // Vérification
        List<?> results = entityManager.createNativeQuery(
                "SELECT * FROM users WHERE username='test'").getResultList();
        assertFalse(results.isEmpty());
    }
}
