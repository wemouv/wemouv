package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.service.VehiculeDeServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
     * Récupère la liste des véhicules de service disponibles sur une période donnée.
     *
     * <p>Cette méthode expose un endpoint GET permettant de rechercher tous les
     * {@link VehiculeDeService} disponibles entre une date de début et une date de fin.
     * Les dates sont fournies en paramètres de requête.</p>
     *
     * @param dateDebut la date et l'heure de début de la période recherchée
     * @param dateFin   la date et l'heure de fin de la période recherchée
     *
     * @return une {@link ResponseEntity} contenant la liste des véhicules disponibles
     *         si la recherche réussit, ou un statut HTTP 404 si aucun résultat n'est trouvé
     *
     * @throws RuntimeException si une erreur survient lors de la récupération des données
     */
    @GetMapping("/available")
    public ResponseEntity<List<VehiculeDeService>> getAllVehiculesDeServiceAvailable(
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin) {

        try {
            List<VehiculeDeService> vehicules =
                    vehiculeDeServiceService.findAllAvailable(
                            dateDebut,
                            dateFin);

            return ResponseEntity.ok(vehicules);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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