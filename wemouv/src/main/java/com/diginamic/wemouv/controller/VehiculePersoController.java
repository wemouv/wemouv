package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.service.VehiculePersoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des véhicules personnels des collaborateurs.
 */
@RestController
@RequestMapping("/api/vehicules/perso")
public class VehiculePersoController {

    private final VehiculePersoService vehiculePersoService;

    // Injection du Service uniquement
    public VehiculePersoController(VehiculePersoService vehiculePersoService) {
        this.vehiculePersoService = vehiculePersoService;
    }

    /**
     * Récupère la liste de tous les véhicules personnels enregistrés.
     */
    @GetMapping
    public List<VehiculePerso> getAllVehiculesPerso() {
        return vehiculePersoService.findAll();
    }

    /**
     * Récupère les véhicules personnels associés à un propriétaire (collaborateur) spécifique.
     */
    @GetMapping("/proprietaire/{proprietaireId}")
    public List<VehiculePerso> getVehiculesByProprietaire(@PathVariable("proprietaireId") Long proprietaireId) {
        return vehiculePersoService.findByProprietaire(proprietaireId);
    }

    /**
     * Enregistre un nouveau véhicule personnel.
     */
    @PostMapping
    public ResponseEntity<VehiculePerso> createVehiculePerso(@RequestBody VehiculePerso vehiculePerso) {
        VehiculePerso savedVehicule = vehiculePersoService.create(vehiculePerso);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Supprime un véhicule personnel du système.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculePerso(@PathVariable("id") Long id) {
        try {
            vehiculePersoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}