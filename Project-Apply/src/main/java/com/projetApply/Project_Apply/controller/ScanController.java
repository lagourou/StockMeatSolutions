package com.projetApply.Project_Apply.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.projetApply.Project_Apply.configuration.UserDetailsImplements;
import com.projetApply.Project_Apply.dto.ProductDTO;
import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.service.ScanService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@SessionAttributes("scannedProducts")
@RequestMapping("/scans")
public class ScanController {

    private final ScanService scanService;

    @GetMapping("/user/{userId}")
    public String getScansByUser(@PathVariable int userId, Model model) {
        List<ScanDTO> scans = scanService.getScanDTOsByUser(userId);
        model.addAttribute("scans", scans);
        return "scans/user";
    }

    @GetMapping("/product/{barcode}")
    public String getScansByProduct(@PathVariable String barcode, Model model) {
        List<ScanDTO> scans = scanService.getScanDTOsByProduct(barcode);
        model.addAttribute("scans", scans);
        return "scans/product";
    }

    @GetMapping
    public String getShowScanPage(Model model) {
        return "scans";
    }

    @PostMapping("/save")
    public String saveScan(@RequestParam String barcode,
            @ModelAttribute("scannedProducts") List<ProductDTO> scannedProducts, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplements userDetails = (UserDetailsImplements) authentication.getPrincipal();
        int userId = userDetails.getId();

        ScanDTO scan = scanService.saveScanDTO(userId, barcode);
        ProductDTO product = scan.getProduct();

        if (product != null) {
            scannedProducts.add(product);
            model.addAttribute("successMessage", "✔️ Produit scanné avec succès !");
        }

        BigDecimal totalAmount = scannedProducts.stream()
                .map(ProductDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("product", product);
        model.addAttribute("scannedProducts", scannedProducts);
        model.addAttribute("totalAmount", totalAmount);

        return "scans";

    }

    @ModelAttribute("scannedProducts")
    public List<ProductDTO> scannedProducts() {
        return new ArrayList<>();
    }

    @PostMapping("/reset")
    public String resetScannedProducts(SessionStatus status) {
        status.setComplete();
        return "redirect:/scans";
    }

}
