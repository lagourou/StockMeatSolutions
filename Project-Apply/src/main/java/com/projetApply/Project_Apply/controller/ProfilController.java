package com.projetApply.Project_Apply.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.dto.UserDTO;
import com.projetApply.Project_Apply.mapper.UserMapper;
import com.projetApply.Project_Apply.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfilController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/profil")
    public String profil(@AuthenticationPrincipal UserDetailsImplements userDetails, Model model) throws Exception {

        if (userDetails != null) {
            UserDTO userForm = userMapper.toDTO(userService.getUserById(userDetails.getId()));
            model.addAttribute("user", userForm);
        } else {
            log.warn("Utilisateur non authentifié, redirection vers la page de connexion.");
            return "redirect:/login";
        }
        return "profil";
    }

    @PostMapping("/modify-profil")
    public String modifyUser(@Valid @ModelAttribute("user") UserDTO updatedUser, BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImplements userDetails, RedirectAttributes redirectAttributes)
            throws Exception {

        if (bindingResult.hasErrors()) {
            log.warn("Erreur dans la modification du profil: {}", bindingResult.getAllErrors());
            return "profil";
        }
        log.info("Modification du profil de l'utilisateur ID: {}", userDetails.getId());
        userService.updateUser(updatedUser, userDetails.getId());
        redirectAttributes.addFlashAttribute("success", "Profil bien modifié");

        return "redirect:/profil";
    }

}
