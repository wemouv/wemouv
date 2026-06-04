package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.service.UtilisateurService;
import com.diginamic.wemouv.service.VehiculePersoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des véhicules personnels des collaborateurs.
 * <p>
 * Ce contrôleur expose les points d'accès (endpoints) permettant d'ajouter, de consulter,
 * de modifier ou de supprimer les véhicules personnels utilisés dans le cadre du covoiturage.
 * </p>
 */
@RestController
@RequestMapping("/api/vehicules/perso")
public class VehiculePersoController {

    /** Le service métier gérant la logique des véhicules personnels. */
    private final VehiculePersoService vehiculePersoService;

    /** Le service métier gérant les profils utilisateurs. */
    private final UtilisateurService utilisateurService;

    /**
     * Constructeur avec injection des services dédiés.
     *
     * @param vehiculePersoService le service gérant les véhicules personnels
     * @param utilisateurService le service gérant les utilisateurs
     */
    public VehiculePersoController(VehiculePersoService vehiculePersoService, UtilisateurService utilisateurService) {
        this.vehiculePersoService = vehiculePersoService;
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère la liste complète de tous les véhicules personnels enregistrés.
     *
     * @return la liste de l'intégralité des véhicules personnels (HTTP 200)
     */
    @GetMapping
    public List<VehiculePerso> getAllVehiculesPerso() {
        return vehiculePersoService.findAll();
    }

    /**
     * Récupère la liste des véhicules personnels disponibles sur une période donnée.
     * <p>Cette méthode expose un endpoint GET permettant de rechercher tous les
     * {@link VehiculePerso} disponibles entre une date de début et une date de fin.
     * Les dates sont fournies en paramètres de requête.</p>
     *
     * @param dateDebut la date et l'heure de début de la période recherchée
     * @param dateFin   la date et l'heure de fin de la période recherchée
     * @return une {@link ResponseEntity} contenant la liste des véhicules disponibles (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur si la récupération échoue
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAllVehiculesPersoAvailable(
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin) {
        try {
            List<VehiculePerso> vehicules = vehiculePersoService.findAllAvailable(dateDebut, dateFin);
            return ResponseEntity.ok(vehicules);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Récupère les véhicules personnels associés à un propriétaire (collaborateur) spécifique.
     *
     * @param proprietaireId l'identifiant unique du propriétaire
     * @return la liste des véhicules appartenant à ce collaborateur (HTTP 200)
     */
    @GetMapping("/proprietaire/{proprietaireId}")
    public List<VehiculePerso> getVehiculesByProprietaire(@PathVariable("proprietaireId") Long proprietaireId) {
        return vehiculePersoService.findByProprietaire(proprietaireId);
    }

    /**
     * Enregistre un nouveau véhicule personnel et l'associe à l'utilisateur connecté.
     * <p>Le propriétaire du véhicule est automatiquement déduit via le Token de sécurité.</p>
     *
     * @param vehiculePerso les détails du véhicule à créer
     * @param authentication l'objet contenant l'email de l'utilisateur connecté
     * @return un {@link ResponseEntity} contenant le véhicule créé avec le statut HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<VehiculePerso> createVehiculePerso(
            @RequestBody VehiculePerso vehiculePerso,
            Authentication authentication) {

        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurService.findByEmail(email);

        vehiculePerso.setProprietaire(utilisateur);
        VehiculePerso savedVehicule = vehiculePersoService.create(vehiculePerso);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Met à jour les informations d'un véhicule personnel existant.
     *
     * @param vehiculePerso les nouvelles données à appliquer
     * @param id l'identifiant du véhicule à modifier
     * @return un {@link ResponseEntity} contenant le véhicule mis à jour (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur s'il est introuvable
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehiculePerso(
            @RequestBody VehiculePerso vehiculePerso,
            @PathVariable Long id) {
        try {
            VehiculePerso savedVehicule = vehiculePersoService.update(id, vehiculePerso);
            return ResponseEntity.ok(savedVehicule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Supprime définitivement un véhicule personnel du système.
     *
     * @param id l'identifiant du véhicule à supprimer
     * @return un {@link ResponseEntity} vide avec le statut HTTP 204 (No Content) en cas de réussite,
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur s'il est introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehiculePerso(@PathVariable("id") Long id) {
        try {
            vehiculePersoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}