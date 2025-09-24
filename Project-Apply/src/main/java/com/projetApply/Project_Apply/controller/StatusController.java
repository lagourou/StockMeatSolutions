package com.projetApply.Project_Apply.controller;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur utilisé uniquement en environnement de développement (profil
 * "dev").
 * Il permet de vérifier rapidement que l'application est bien lancée dans son
 * conteneur Docker.
 *
 * Ce contrôleur expose un endpoint REST accessible à l'adresse "/api/status".
 * Quand on appelle cette URL, il renvoie une réponse JSON contenant :
 * - le statut de l'application ("ok")
 * - le profil actif ("dev")
 * - un message de confirmation ("Application is running")
 *
 * Ce point de contrôle est utile pendant les phases de test ou de
 * configuration,
 * notamment pour s'assurer que le conteneur fonctionne correctement.
 *
 * Les logs indiquent quand le contrôleur est chargé et quand l'endpoint est
 * appelé.
 */

@Profile("dev")
@RestController
@RequestMapping("/api")
@Slf4j
public class StatusController {

    @GetMapping("/status")
    public Map<String, String> status() {
        log.info("Endpoint appelé /api/status appelé");
        return Map.of(
                "status", "ok",
                "environment",
                "dev",
                "message", "Application is running");
    }

    @PostConstruct
    public void init() {
        log.info("StatusController chargé avec le profil DEV");
    }

}
