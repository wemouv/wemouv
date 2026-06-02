package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.service.VehiculeDeServiceService;
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

    private final VehiculeDeServiceService vehiculeDeServiceService;

    // Injection du Service uniquement
    public VehiculeDeServiceController(VehiculeDeServiceService vehiculeDeServiceService) {
        this.vehiculeDeServiceService = vehiculeDeServiceService;
    }

    /**
     * Récupère l'ensemble de la flotte de véhicules de service de l'entreprise.
     */
    @GetMapping
    public List<VehiculeDeService> getAllVehiculesDeService() {
        return vehiculeDeServiceService.findAll();
    }

    /**
     * Récupère un véhicule de service spécifique par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDeService> getVehiculeDeServiceById(@PathVariable Long id) {
        try {
            VehiculeDeService vehicule = vehiculeDeServiceService.findById(id);
            return ResponseEntity.ok(vehicule);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Enregistre un nouveau véhicule de service dans le parc automobile.
     */
    @PostMapping
    public ResponseEntity<VehiculeDeService> createVehiculeDeService(@RequestBody VehiculeDeService vehiculeDeService) {
        VehiculeDeService savedVehicule = vehiculeDeServiceService.create(vehiculeDeService);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Met à jour les informations d'un véhicule de service.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDeService> updateVehiculeDeService(@PathVariable Long id, @RequestBody VehiculeDeService details) {
        try {
            VehiculeDeService updated = vehiculeDeServiceService.update(id, details);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime un véhicule de service du parc.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculeDeService(@PathVariable Long id) {
        try {
            vehiculeDeServiceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}