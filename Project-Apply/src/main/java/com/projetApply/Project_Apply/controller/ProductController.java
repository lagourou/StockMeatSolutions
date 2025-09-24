package com.projetApply.Project_Apply.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.exception.UserNotFoundException;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.UserRepository;
import com.projetApply.Project_Apply.service.ProductService;
import com.projetApply.Project_Apply.service.ScanService;

import lombok.RequiredArgsConstructor;

/**
 * Contrôleur pour gérer les produits dans l’application.
 * 
 * Il permet de :
 * - ajouter ou mettre à jour un produit via "/products/add",
 * - afficher la liste des produits et le stock total via "/products",
 * - supprimer un produit via "/products/remove",
 * - rafraîchir les statuts des produits via "/products/refresh-status".
 * 
 * Utilise :
 * - ProductService pour gérer les opérations sur les produits,
 * - ScanService pour récupérer les scans liés à l’utilisateur,
 * - UserRepository pour identifier l’utilisateur connecté.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ScanService scanService;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public String addOrUpdateProduct(
            @RequestParam String codeBarre,
            @RequestParam(required = false) String nom,
            @RequestParam int quantite,
            @RequestParam int poids,
            @RequestParam BigDecimal prix,
            Model model) {

        ProductDTO product = productService.addProduct(codeBarre, nom, quantite, poids, prix);
        return "redirect:/products/" + product.getId();

    }

    @GetMapping
    public String listProducts(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("Utilsateur introuvable"));
        int userId = user.getId();

        productService.updateAllStatuses();
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("stockTotal", productService.getTotalStock());

        List<ScanDTO> scans = scanService.getScanDTOsByUser(userId);
        model.addAttribute("scanTotal", scans.size());

        return "products";
    }

    @PostMapping("/remove")
    public String removeProduct(@RequestParam String codeBarre, Model model) {
        productService.removeProduct(codeBarre);
        return "redirect:/products/";
    }

    @PostMapping("refresh-status")
    public String updateStatus() {
        productService.updateAllStatuses();
        return "redirect:/products";
    }
}
