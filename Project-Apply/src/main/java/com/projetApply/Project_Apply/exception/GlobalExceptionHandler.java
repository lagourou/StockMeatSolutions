package com.projetApply.Project_Apply.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFound(ProductNotFoundException ex, Model model) {
        log.warn("Produit introuvable : {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "product/notfound";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        log.warn("Utilisateur introuvable : {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "user/notfound";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        log.error("Une erreur inattendue est survenue", ex);
        model.addAttribute("error", "Une erreur inattendue est survenue.");
        return "error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
        log.error("Type de paramètre invalide : {}", ex.getValue(), ex);
        model.addAttribute("error", "Type de paramètre invalide : " + ex.getValue());
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationError(MethodArgumentNotValidException ex, Model model) {
        log.error("Erreur de validation : {}", ex.getMessage(), ex);
        model.addAttribute("error", "Erreur de validation : " + ex.getMessage());
        return "error";
    }

}
