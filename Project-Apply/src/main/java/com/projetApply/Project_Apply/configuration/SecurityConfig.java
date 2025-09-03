package com.projetApply.Project_Apply.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

        private final CustomUserDetailsService customUserDetailsService;

        @PostConstruct
        public void checkProfile() {
                log.info("🔍 Profil actif: {}", System.getProperty("spring.profiles.active"));
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                log.info("Initialisation du mot de passe encoder");
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
                        throws Exception {
                log.info("Configuration de l'Authentication Manager");
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                                .passwordEncoder(passwordEncoder);

                return authenticationManagerBuilder.build();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring().requestMatchers("/api/status");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                log.info("Définition des règles de sécurité");

                return http
                                .authorizeHttpRequests(auth -> auth
                                                // Pages publiques
                                                .requestMatchers("/", "/login", "/register", "/forget-password",
                                                                "/reset-password")
                                                .permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                                .requestMatchers("/api/status").permitAll()
                                                // Pages nécessitant une authentification
                                                .requestMatchers(
                                                                "/profil", "/modify-profil",
                                                                "/form", "/add", "/remove", "/{id}",
                                                                "/user/**", "/products/**", "/save",
                                                                "/send-invoice", "/scans/**", "/payment/**")
                                                .authenticated()

                                                // Tout le reste est protégé
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/profil", true) // redirection après login
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .csrf(csrf -> csrf.disable())
                                .build();
        }

}
