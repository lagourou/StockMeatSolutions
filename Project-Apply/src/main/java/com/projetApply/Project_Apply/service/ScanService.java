package com.projetApply.Project_Apply.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projetApply.Project_Apply.dto.ScanDTO;
import com.projetApply.Project_Apply.exception.ProductNotFoundException;
import com.projetApply.Project_Apply.mapper.ScanMapper;
import com.projetApply.Project_Apply.model.Product;
import com.projetApply.Project_Apply.model.Scan;
import com.projetApply.Project_Apply.model.User;
import com.projetApply.Project_Apply.repository.ProductRepository;
import com.projetApply.Project_Apply.repository.ScanRepository;
import com.projetApply.Project_Apply.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final ScanMapper scanMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Scan saveScan(int userId, String barcode) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("Produit introuvable"));

        Scan scan = new Scan();
        scan.setUser(user);
        scan.setProduct(product);
        scan.setDateScan(new Timestamp(System.currentTimeMillis()));

        return scanRepository.save(scan);
    }

    public List<ScanDTO> getScanDTOsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        List<Scan> scans = scanRepository.findByUser(user);

        return scans.stream().map(scanMapper::toDTO).toList();
    }

    public List<ScanDTO> getScanDTOsByProduct(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("Produit introuvable"));

        List<Scan> scans = scanRepository.findByProduct(product);
        return scans.stream().map(scanMapper::toDTO).toList();
    }

    public ScanDTO saveScanDTO(int userId, String barcode) {
        Scan scan = saveScan(userId, barcode);
        return scanMapper.toDTO(scan);
    }

}
