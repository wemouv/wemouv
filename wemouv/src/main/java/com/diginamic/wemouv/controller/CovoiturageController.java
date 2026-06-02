package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.service.CovoiturageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des covoiturages et des participations passagers.
 */
@RestController
@RequestMapping("/covoiturages")
public class CovoiturageController {

    private final CovoiturageService covoiturageService;

    /**
     * Constructeur avec injection unique du Service.
     */
    public CovoiturageController(CovoiturageService covoiturageService) {
        this.covoiturageService = covoiturageService;
    }

    /**
     * Récupère la liste complète de tous les covoiturages enregistrés.
     */
    @GetMapping
    public List<Covoiturage> getAllCovoiturages() {
        return covoiturageService.findAll();
    }

    /**
     * Récupère un covoiturage spécifique par son identifiant unique.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Covoiturage> getCovoiturageById(@PathVariable Long id) {
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
    public ResponseEntity<Covoiturage> updateCovoiturage(@PathVariable Long id, @RequestBody Covoiturage covoiturageDetails) {
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
    public ResponseEntity<Void> deleteCovoiturage(@PathVariable Long id) {
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
            @PathVariable Long covoiturageId, 
            @PathVariable Long utilisateurId) {
        try {
            ParticipationCovoiturage participation = covoiturageService.participer(covoiturageId, utilisateurId);
            return ResponseEntity.status(HttpStatus.CREATED).body(participation);
        } catch (IllegalStateException e) {
            // Plus de places disponibles (Erreur 400)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Covoiturage introuvable (Erreur 404)
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permet à un utilisateur d'annuler sa participation à un covoiturage.
     */
    @DeleteMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<?> annulerParticipation(
            @PathVariable Long covoiturageId, 
            @PathVariable Long utilisateurId) {
        try {
            covoiturageService.annulerParticipation(covoiturageId, utilisateurId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Participation ou covoiturage introuvable
            return ResponseEntity.notFound().build();
        }
    }
}