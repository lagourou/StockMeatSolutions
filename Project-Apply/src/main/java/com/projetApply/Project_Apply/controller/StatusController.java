package com.projetApply.Project_Apply.controller;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Profile("dev")
@RestController
@RequestMapping("/api")
@Slf4j
public class StatusController {

    @GetMapping
    public Map<String, String> status() {
        log.info("Endpoint appelé /api/status appelé");
        return Map.of(
                "status", "ok",
                "environment",
                "dev",
                "message", "Application is running");
    }

}
