package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.repository.VehiculeDeServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des véhicules de service de l'entreprise.
 */
@RestController
@RequestMapping("/api/vehicules/service")
public class VehiculeDeServiceController {

    private final VehiculeDeServiceRepository vehiculeDeServiceRepository;

    public VehiculeDeServiceController(VehiculeDeServiceRepository vehiculeDeServiceRepository) {
        this.vehiculeDeServiceRepository = vehiculeDeServiceRepository;
    }

    /**
     * Récupère l'ensemble de la flotte de véhicules de service de l'entreprise.
     */
    @GetMapping
    public List<VehiculeDeService> getAllVehiculesDeService() {
        return vehiculeDeServiceRepository.findAll();
    }

    /**
     * Récupère un véhicule de service spécifique par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDeService> getVehiculeDeServiceById(@PathVariable Long id) {
        return vehiculeDeServiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Enregistre un nouveau véhicule de service dans le parc automobile.
     */
    @PostMapping
    public ResponseEntity<VehiculeDeService> createVehiculeDeService(@RequestBody VehiculeDeService vehiculeDeService) {
        VehiculeDeService savedVehicule = vehiculeDeServiceRepository.save(vehiculeDeService);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Met à jour les informations d'un véhicule de service (statut, localisation, etc.).
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDeService> updateVehiculeDeService(@PathVariable Long id, @RequestBody VehiculeDeService details) {
        return vehiculeDeServiceRepository.findById(id).map(vehicule -> {
            vehicule.setImmatriculation(details.getImmatriculation());
            vehicule.setMarque(details.getMarque());
            vehicule.setMotorisation(details.getMotorisation());
            vehicule.setNbPlace(details.getNbPlace());
            vehicule.setPhotoUrl(details.getPhotoUrl());
            vehicule.setCo2Km(details.getCo2Km());
            vehicule.setCategorie(details.getCategorie());
            vehicule.setLocalisation(details.getLocalisation());
            vehicule.setStatut(details.getStatut());
            
            VehiculeDeService updated = vehiculeDeServiceRepository.save(vehicule);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un véhicule de service du parc.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculeDeService(@PathVariable Long id) {
        return vehiculeDeServiceRepository.findById(id).map(vehicule -> {
            vehiculeDeServiceRepository.delete(vehicule);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}