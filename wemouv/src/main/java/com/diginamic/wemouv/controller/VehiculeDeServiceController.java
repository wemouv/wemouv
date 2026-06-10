package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.service.VehiculeDeServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des véhicules de service de l'entreprise.
 * <p>
 * Ce contrôleur expose les points d'accès (endpoints) permettant aux utilisateurs
 * et aux administrateurs de lister, filtrer, vérifier la disponibilité, créer,
 * modifier ou supprimer des véhicules de la flotte professionnelle.
 * </p>
 */
@RestController
@RequestMapping("/api/vehicules/service")
public class VehiculeDeServiceController {

    /** Le service métier contenant la logique liée aux véhicules de service. */
    private final VehiculeDeServiceService vehiculeDeServiceService;

    /**
     * Constructeur avec injection unique du Service dédié.
     *
     * @param vehiculeDeServiceService le service gérant la logique métier des véhicules
     */
    public VehiculeDeServiceController(VehiculeDeServiceService vehiculeDeServiceService) {
        this.vehiculeDeServiceService = vehiculeDeServiceService;
    }

    /**
     * TÂCHE 15 : Récupère l'ensemble de la flotte de véhicules de service de l'entreprise avec filtrage multicritère.
     * <p>
     * Cet endpoint permet à l'administrateur de visualiser l'ensemble des détails du parc automobile.
     * Il intègre un filtrage dynamique : si un paramètre est fourni (immatriculation ou marque),
     * la liste est filtrée en conséquence. Sinon, l'intégralité du catalogue est retournée.
     * </p>
     *
     * @param immatriculation fragment de plaque d'immatriculation à rechercher (optionnel)
     * @param marque          fragment de marque de véhicule à rechercher (optionnel)
     * @return un {@link ResponseEntity} contenant la liste des véhicules correspondants (HTTP 200)
     */
    @GetMapping
    public ResponseEntity<List<VehiculeDeService>> getAllVehiculesDeService(
            @RequestParam(required = false) String immatriculation,
            @RequestParam(required = false) String marque) {

        List<VehiculeDeService> vehicules = vehiculeDeServiceService.getVehiculesFlotte(immatriculation, marque);
        return ResponseEntity.ok(vehicules);
    }

    /**
     * Récupère la liste des véhicules de service disponibles sur une période donnée.
     * <p>
     * Un véhicule est considéré comme disponible s'il est actif et qu'aucune autre réservation
     * professionnelle ne chevauche la plage horaire demandée.
     * </p>
     *
     * @param dateDebut la date et l'heure de début de la période recherchée
     * @param dateFin   la date et l'heure de fin de la période recherchée
     * @return un {@link ResponseEntity} contenant la liste des véhicules disponibles (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur en cas de problème
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAllVehiculesDeServiceAvailable(
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin) {
        try {
            List<VehiculeDeService> vehicules = vehiculeDeServiceService.findAllAvailable(dateDebut, dateFin);
            return ResponseEntity.ok(vehicules);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Récupère un véhicule de service spécifique par son identifiant unique.
     *
     * @param id l'identifiant unique du véhicule recherché
     * @return un {@link ResponseEntity} contenant le véhicule s'il est trouvé (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur s'il n'existe pas
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVehiculeDeServiceById(@PathVariable Long id) {
        try {
            VehiculeDeService vehicule = vehiculeDeServiceService.findById(id);
            return ResponseEntity.ok(vehicule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Enregistre un nouveau véhicule de service dans le parc automobile.
     *
     * @param vehiculeDeService l'entité contenant les spécifications de la nouvelle voiture
     * @return un {@link ResponseEntity} contenant le véhicule créé avec le statut HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<VehiculeDeService> createVehiculeDeService(@RequestBody VehiculeDeService vehiculeDeService) {
        VehiculeDeService savedVehicule = vehiculeDeServiceService.create(vehiculeDeService);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Met à jour l'ensemble des informations et caractéristiques d'un véhicule de service.
     *
     * @param id      l'identifiant du véhicule à modifier
     * @param details les nouvelles caractéristiques techniques ou de statut à appliquer
     * @return un {@link ResponseEntity} contenant l'entité mise à jour en base (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur si l'ID n'existe pas
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehiculeDeService(@PathVariable Long id, @RequestBody VehiculeDeService details) {
        try {
            VehiculeDeService updated = vehiculeDeServiceService.update(id, details);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Supprime définitivement un véhicule de service du parc automobile de l'entreprise.
     *
     * @param id l'identifiant du véhicule à supprimer
     * @return un {@link ResponseEntity} vide avec le statut HTTP 204 (No Content) en cas de réussite,
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur si le véhicule n'a pas pu être trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehiculeDeService(@PathVariable Long id) {
        try {
            vehiculeDeServiceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PatchMapping("/{id}/statut")
    public ResponseEntity<Void> changerStatut(
            @PathVariable Long id,
            @RequestParam Disponibilite statut) {

        vehiculeDeServiceService.changerStatut(id, statut);
        return ResponseEntity.ok().build();
    }
}