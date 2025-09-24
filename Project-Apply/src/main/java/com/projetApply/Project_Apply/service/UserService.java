package com.projetApply.Project_Apply.service;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.exception.UserNotFoundException;
import com.projetApply.Project_Apply.mapper.UserMapper;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Service qui gère les opérations liées aux utilisateurs.
 * 
 * Cette classe permet de :
 * - récupérer un utilisateur par son ID ou son email,
 * - enregistrer un nouvel utilisateur ou mettre à jour un existant,
 * - supprimer un utilisateur,
 * - gérer la sécurité après une mise à jour.
 * 
 * Elle utilise :
 * - UserRepository pour accéder à la base de données,
 * - UserMapper pour convertir entre User et UserDTO,
 * - PasswordEncoder pour sécuriser les mots de passe.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Récupère un utilisateur à partir de son identifiant.
     * 
     * @param userId identifiant de l'utilisateur
     * @return l'utilisateur trouvé
     * @throws EntityNotFoundException si aucun utilisateur n'est trouvé
     */
    public User getUserById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        return user;
    }

    /**
     * Récupère un utilisateur à partir de son adresse email.
     * 
     * @param email adresse email de l'utilisateur
     * @return l'utilisateur trouvé
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException("Utilisateur avec cet email " + email + "introuvable"));
    }

    /**
     * Enregistre un utilisateur dans la base de données.
     * 
     * @param user l'utilisateur à enregistrer
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Enregistre un nouvel utilisateur à partir d'un DTO.
     * Le mot de passe est encodé avant l'enregistrement.
     * 
     * @param userDTO les données du nouvel utilisateur
     */
    public void saveNewUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Supprime un utilisateur à partir de son identifiant.
     * 
     * @param id identifiant de l'utilisateur à supprimer
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     */
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        userRepository.delete(user);
    }

    /**
     * Met à jour les informations d'un utilisateur existant.
     * Met aussi à jour l'authentification dans le contexte de sécurité.
     * 
     * @param updateUser les nouvelles données
     * @param id         identifiant de l'utilisateur à mettre à jour
     */
    public void updateUser(UserDTO updateUser, int id) {
        User user = getUserById(id);
        user = mergeUpdateUser(user, updateUser);
        saveUser(user);

        UserDetailsImplements updateDetails = new UserDetailsImplements(user);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updateDetails, updateDetails.getPassword(), updateDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);

    }

    /**
     * Fusionne les nouvelles données avec l'utilisateur existant.
     * Seules les valeurs non vides et différentes sont mises à jour.
     * 
     * @param existingUser  utilisateur actuel
     * @param updateUserDTO nouvelles données
     * @return utilisateur mis à jour
     */
    private User mergeUpdateUser(User existingUser, UserDTO updateUserDTO) {

        Optional.ofNullable(updateUserDTO.getUsername())
                .filter(username -> isValidUpdateUser(username, existingUser.getUsername()))
                .ifPresent(existingUser::setUsername);

        Optional.ofNullable(updateUserDTO.getEmail())
                .filter(email -> isValidUpdateUser(email, existingUser.getEmail()))
                .ifPresent(existingUser::setEmail);

        Optional.ofNullable(updateUserDTO.getPassword())
                .filter(password -> !password.isBlank())
                .map(passwordEncoder::encode)
                .ifPresent(existingUser::setPassword);

        return existingUser;
    }

    /**
     * Vérifie si une nouvelle valeur est valide pour la mise à jour.
     * 
     * @param newValue      nouvelle valeur proposée
     * @param existingValue valeur actuelle
     * @return true si la valeur est différente et non vide
     */
    private boolean isValidUpdateUser(String newValue, String existingValue) {
        return newValue != null && !newValue.isBlank() && !newValue.equals(existingValue);
    }

}
