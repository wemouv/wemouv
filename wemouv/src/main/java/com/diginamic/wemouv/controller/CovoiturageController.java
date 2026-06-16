package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.CovoiturageRequest;
import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.service.AnnuleParticiaptionCovoiturage;
import com.diginamic.wemouv.service.CovoiturageService;
import com.diginamic.wemouv.service.RechercheCovoiturage;
import com.diginamic.wemouv.service.ReserverCovoiturage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des covoiturages et des participations passagers.
 * <p>Ce contrôleur expose les API permettant de gérer le cycle de vie des trajets
 * (CRUD), ainsi que les inscriptions et consultations de réservations ou d'annonces.</p>
 */
@RestController
@RequestMapping("/api/covoiturages")
public class CovoiturageController {

    // --- AJOUT DU LOGGER ---
    private static final Logger logger = LoggerFactory.getLogger(CovoiturageController.class);

    /** Le service métier contenant la logique globale des covoiturages. */
    private final CovoiturageService covoiturageService;

    /** Le service dédié à la recherche multicritère de trajets. */
    private final RechercheCovoiturage rechercheCovoiturage;

    /** Le service dédié à la réservation de places. */
    private final ReserverCovoiturage reserverCovoiturage;

    /** Le service dédié à l'annulation de participations. */
    private final AnnuleParticiaptionCovoiturage annuleParticiaptionCovoiturage;

    /**
     * Constructeur avec injection des services dédiés.
     */
    public CovoiturageController(CovoiturageService covoiturageService,
                                 RechercheCovoiturage rechercheCovoiturage,
                                 ReserverCovoiturage reserverCovoiturage,
                                 AnnuleParticiaptionCovoiturage annuleParticiaptionCovoiturage) {
        this.covoiturageService = covoiturageService;
        this.rechercheCovoiturage = rechercheCovoiturage;
        this.reserverCovoiturage = reserverCovoiturage;
        this.annuleParticiaptionCovoiturage = annuleParticiaptionCovoiturage;
    }

    @GetMapping
    public ResponseEntity<List<Covoiturage>> getAllCovoiturages() {
        return ResponseEntity.ok(covoiturageService.findAll());
    }

    @GetMapping("/recherche")
    public List<Covoiturage> rechercherCovoiturages(
            @RequestParam(value = "depart", required = false) String depart,
            @RequestParam(value = "arrivee", required = false) String arrivee,
            @RequestParam(value = "date", required = false) LocalDateTime date,
            @RequestParam(value = "statut", required = false) Statut statut) {
        return rechercheCovoiturage.rechercher(depart, arrivee, date, statut);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCovoiturageById(@PathVariable("id") Long id) {
        try {
            Covoiturage covoiturage = covoiturageService.findById(id);
            return ResponseEntity.ok(covoiturage);
        } catch (RuntimeException e) {
            // Pour un 404, un simple log info ou rien suffit.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCovoiturage(
            @RequestBody CovoiturageRequest request
    ) {
        try {
            Covoiturage savedCovoiturage = covoiturageService.create(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedCovoiturage);
        } catch (Exception e) {

            logger.error("Erreur lors de la création du covoiturage", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue lors de la création.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCovoiturage(
            @PathVariable("id") Long id,
            @RequestBody CovoiturageRequest request
    ) {
        try {
            Covoiturage updatedCovoiturage = covoiturageService.update(id, request);
            return ResponseEntity.ok(updatedCovoiturage);
        } catch (RuntimeException e) {
            // Exception métier (ex: Non trouvé)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du covoiturage avec l'ID {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue lors de la mise à jour.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCovoiturage(
            @PathVariable("id") Long id
    ) {
        try {
            covoiturageService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du covoiturage avec l'ID {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue lors de la suppression.");
        }
    }

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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<?> annulerParticipation(
            @PathVariable("covoiturageId") Long covoiturageId,
            @PathVariable("utilisateurId") Long utilisateurId) {
        try {
            annuleParticiaptionCovoiturage.annuler(covoiturageId, utilisateurId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/mes-reservations/{utilisateurId}")
    public ResponseEntity<?> getMesReservations(@PathVariable Long utilisateurId) {
        try {
            Map<String, List<Covoiturage>> reservations = covoiturageService.getReservationsPassager(utilisateurId);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/mes-annonces/{conducteurId}")
    public ResponseEntity<?> getMesAnnonces(@PathVariable Long conducteurId) {
        try {
            Map<String, List<Covoiturage>> annonces = covoiturageService.getAnnoncesConducteur(conducteurId);
            return ResponseEntity.ok(annonces);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}