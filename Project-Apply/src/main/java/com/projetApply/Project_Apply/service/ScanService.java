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

/**
 * Service qui gère les opérations liées aux scans de produits par les
 * utilisateurs.
 * 
 * Cette classe permet de :
 * - enregistrer un scan (quand un utilisateur scanne un produit),
 * - récupérer la liste des scans faits par un utilisateur,
 * - récupérer les scans associés à un produit,
 * - transformer les données en format DTO pour l'affichage ou le transfert.
 * 
 * Elle utilise :
 * - ScanRepository pour enregistrer et récupérer les scans,
 * - UserRepository et ProductRepository pour vérifier les données liées,
 * - ScanMapper pour convertir les objets en DTO.
 */
@Service
@RequiredArgsConstructor
public class ScanService {

    private final ScanRepository scanRepository;
    private final ScanMapper scanMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Enregistre un nouveau scan pour un utilisateur et un produit donné.
     * 
     * @param userId  identifiant de l'utilisateur
     * @param barcode code-barres du produit scanné
     * @return le scan enregistré
     * @throws EntityNotFoundException  si l'utilisateur n'existe pas
     * @throws ProductNotFoundException si le produit n'existe pas
     */
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

    /**
     * Récupère tous les scans effectués par un utilisateur, sous forme de DTO.
     * 
     * @param userId identifiant de l'utilisateur
     * @return liste des scans en format DTO
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     */
    public List<ScanDTO> getScanDTOsByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        List<Scan> scans = scanRepository.findByUser(user);

        return scans.stream().map(scanMapper::toDTO).toList();
    }

    /**
     * Récupère tous les scans associés à un produit, sous forme de DTO.
     * 
     * @param barcode code-barres du produit
     * @return liste des scans en format DTO
     * @throws ProductNotFoundException si le produit n'existe pas
     */
    public List<ScanDTO> getScanDTOsByProduct(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("Produit introuvable"));

        List<Scan> scans = scanRepository.findByProduct(product);
        return scans.stream().map(scanMapper::toDTO).toList();
    }

    /**
     * Enregistre un scan et retourne directement le résultat en format DTO.
     * 
     * @param userId  identifiant de l'utilisateur
     * @param barcode code-barres du produit
     * @return le scan enregistré en format DTO
     */
    public ScanDTO saveScanDTO(int userId, String barcode) {
        Scan scan = saveScan(userId, barcode);
        return scanMapper.toDTO(scan);
    }

}
