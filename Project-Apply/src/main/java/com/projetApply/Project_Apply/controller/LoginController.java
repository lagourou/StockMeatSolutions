package com.projetApply.Project_Apply.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer l’accès à la page de connexion.
 * 
 * Il permet de :
 * - afficher la page de login via "/login",
 * - rediriger la racine "/" vers "/login".
 * 
 * Utile pour diriger les utilisateurs vers l’authentification dès l’arrivée sur
 * le site.
 */
@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String login() {
        log.info("Accès à la page de connexion");
        return "login";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

}
