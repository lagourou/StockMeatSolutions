package com.projetApply.Project_Apply.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Représente les données d’un utilisateur à créer ou modifier.
 * 
 * Ce DTO contient :
 * - l’identifiant de l’utilisateur,
 * - le nom d’utilisateur (entre 8 et 20 caractères),
 * - l’email (doit être valide),
 * - le mot de passe (entre 6 et 32 caractères).
 * 
 * Utilisé pour les formulaires d’inscription, de modification ou d’authentification.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private int id;

    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 8, max = 20, message = "Le nom d'utilisateur doit contenir entre 8 et 20 caractères")
    private String username;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, max = 32, message = "Le mot de passe doit contenir entre 6 et 32 caractères")
    private String password;
}
