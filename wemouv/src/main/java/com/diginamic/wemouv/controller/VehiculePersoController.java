package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculeDeService;
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
 */
@RestController
@RequestMapping("/api/vehicules/perso")
public class VehiculePersoController {

    private final VehiculePersoService vehiculePersoService;
    private final UtilisateurService utilisateurService;

    // Injection du Service uniquement
    public VehiculePersoController(VehiculePersoService vehiculePersoService, UtilisateurService utilisateurService) {
        this.vehiculePersoService = vehiculePersoService;
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère la liste de tous les véhicules personnels enregistrés.
     */
    @GetMapping
    public List<VehiculePerso> getAllVehiculesPerso() {
        return vehiculePersoService.findAll();
    }

    /**
     * Récupère la liste des véhicules de service disponibles sur une période donnée.
     *
     * <p>Cette méthode expose un endpoint GET permettant de rechercher tous les
     * {@link VehiculePerso} disponibles entre une date de début et une date de fin.
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
    public ResponseEntity<List<VehiculePerso>> getAllVehiculesPersoAvailable(
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin) {

        try {
            List<VehiculePerso> vehicules =
                    vehiculePersoService.findAllAvailable(
                            dateDebut,
                            dateFin);

            return ResponseEntity.ok(vehicules);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<VehiculePerso> createVehiculePerso(@RequestBody VehiculePerso vehiculePerso, Authentication authentication) {

        String email = authentication.getName();

        Utilisateur utilisateur =
                utilisateurService.findByEmail(email);

        vehiculePerso.setProprietaire(utilisateur);

        VehiculePerso savedVehicule = vehiculePersoService.create(vehiculePerso);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicule);
    }

    /**
     * Met à jour un véhicule personnel.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehiculePerso> updateVehiculePerso(
            @RequestBody VehiculePerso vehiculePerso,
            @PathVariable Long id) {

        VehiculePerso savedVehicule =
                vehiculePersoService.update(id, vehiculePerso);

        return ResponseEntity.ok(savedVehicule);
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