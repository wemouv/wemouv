package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.service.CovoiturageService;
import com.diginamic.wemouv.service.RechercheCovoiturage;
import com.diginamic.wemouv.service.ReserverCovoiturage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des covoiturages et des participations passagers.
 */
@RestController
@RequestMapping("/covoiturages")
public class CovoiturageController {

    private final CovoiturageService covoiturageService;
    private final RechercheCovoiturage rechercheCovoiturage;
    private final ReserverCovoiturage reserverCovoiturage;

    /**
     * Constructeur avec injection des services.
     */
    public CovoiturageController(CovoiturageService covoiturageService,
                                 RechercheCovoiturage rechercheCovoiturage,
                                 ReserverCovoiturage reserverCovoiturage) {
        this.covoiturageService = covoiturageService;
        this.rechercheCovoiturage = rechercheCovoiturage;
        this.reserverCovoiturage = reserverCovoiturage;
    }

    /**
     * Récupère la liste complète de tous les covoiturages enregistrés.
     */
    @GetMapping
    public List<Covoiturage> getAllCovoiturages() {
        return covoiturageService.findAll();
    }

    /**
     * Recherche des covoiturages selon des critères optionnels.
     */
    @GetMapping("/recherche")
    public List<Covoiturage> rechercherCovoiturages(
            @RequestParam(value = "depart", required = false) String depart,
            @RequestParam(value = "arrivee", required = false) String arrivee,
            @RequestParam(value = "date", required = false) LocalDateTime date,
            @RequestParam(value = "statut", required = false) Statut statut) {
        return rechercheCovoiturage.rechercher(depart, arrivee, date, statut);
    }

    /**
     * Récupère un covoiturage spécifique par son identifiant unique.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Covoiturage> getCovoiturageById(@PathVariable("id") Long id) {
        try {
            Covoiturage covoiturage = covoiturageService.findById(id);
            return ResponseEntity.ok(covoiturage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crée et enregistre un nouveau covoiturage.
     */
    @PostMapping
    public ResponseEntity<Covoiturage> createCovoiturage(@RequestBody Covoiturage covoiturage) {
        Covoiturage savedCovoiturage = covoiturageService.create(covoiturage);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCovoiturage);
    }

    /**
     * Met à jour les informations d'un covoiturage existant.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Covoiturage> updateCovoiturage(@PathVariable("id") Long id, @RequestBody Covoiturage covoiturageDetails) {
        try {
            Covoiturage updatedCovoiturage = covoiturageService.update(id, covoiturageDetails);
            return ResponseEntity.ok(updatedCovoiturage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime définitivement un covoiturage du système.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCovoiturage(@PathVariable("id") Long id) {
        try {
            covoiturageService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permet à un utilisateur de réserver une place (participer) dans un covoiturage.
     */
    @PostMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<?> participer(
            @PathVariable("covoiturageId") Long covoiturageId,
            @PathVariable("utilisateurId") Long utilisateurId) {
        try {
            ParticipationCovoiturage participation = reserverCovoiturage.reserver(covoiturageId, utilisateurId);
            return ResponseEntity.status(HttpStatus.CREATED).body(participation);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permet à un utilisateur d'annuler sa participation à un covoiturage.
     */
    @DeleteMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<?> annulerParticipation(
            @PathVariable("covoiturageId") Long covoiturageId,
            @PathVariable("utilisateurId") Long utilisateurId) {
        try {
            covoiturageService.annulerParticipation(covoiturageId, utilisateurId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
