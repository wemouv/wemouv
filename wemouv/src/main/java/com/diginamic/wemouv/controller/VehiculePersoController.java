package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.repository.VehiculePersoRepository;
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

    private final VehiculePersoRepository vehiculePersoRepository;

    public VehiculePersoController(VehiculePersoRepository vehiculePersoRepository) {
        this.vehiculePersoRepository = vehiculePersoRepository;
    }

    /**
     * Récupère la liste de tous les véhicules personnels enregistrés.
     */
    @GetMapping
    public List<VehiculePerso> getAllVehiculesPerso() {
        return vehiculePersoRepository.findAll();
    }

    /**
     * Récupère les véhicules personnels associés à un propriétaire (collaborateur) spécifique.
     */
    @GetMapping("/proprietaire/{proprietaireId}")
    public List<VehiculePerso> getVehiculesByProprietaire(@PathVariable Long proprietaireId) {
        return vehiculePersoRepository.findByProprietaireId(proprietaireId);
    }

    /**
     * Enregistre un nouveau véhicule personnel.
     */
    @PostMapping
    public ResponseEntity<VehiculePerso> createVehiculePerso(@RequestBody VehiculePerso vehiculePerso) {
        VehiculePerso savedVehicule = vehiculePersoRepository.save(vehiculePerso);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Supprime un véhicule personnel du système.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculePerso(@PathVariable Long id) {
        return vehiculePersoRepository.findById(id).map(vehicule -> {
            vehiculePersoRepository.delete(vehicule);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}