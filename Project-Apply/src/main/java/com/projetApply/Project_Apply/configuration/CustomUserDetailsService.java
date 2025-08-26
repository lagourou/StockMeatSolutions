package com.projetApply.Project_Apply.configuration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Recherche utilisateur avec email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("L'utilisateur avec l'email " + email + " n'existe pas"));
        log.info("Utilisateur trouvé: {}", email);
        return new UserDetailsImplements(user);
    }

}
