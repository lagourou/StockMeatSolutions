package com.projetApply.Project_Apply.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer l’inscription des nouveaux utilisateurs.
 * 
 * Il permet de :
 * - afficher le formulaire d’inscription via "/register",
 * - valider les données saisies et enregistrer l’utilisateur,
 * - vérifier que l’email n’est pas déjà utilisé,
 * - encoder le mot de passe avant de le sauvegarder.
 * 
 * Utilise :
 * - UserRepository pour enregistrer l’utilisateur,
 * - PasswordEncoder pour sécuriser le mot de passe,
 * - les annotations Spring pour valider le formulaire.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String register(Model model) {

        log.info("Accès à la page d'inscription");
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDto,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            log.warn("Erreur dans le formulaire d'inscription");
            model.addAttribute("user", userDto);
            return "register";
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {

            log.warn("Email déjà utilisé : {}", userDto.getEmail());
            model.addAttribute("error", "Email déjà utilisé");
            return "register";
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);

        log.info("Nouvel utilisateur inscrit : {}", userDto.getEmail());
        model.addAttribute("success", "Inscription réussie");
        return "redirect:/login";
    }

}
