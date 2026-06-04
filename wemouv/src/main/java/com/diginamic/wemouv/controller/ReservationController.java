package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.service.ListeReservationVehicule;
import com.diginamic.wemouv.service.ModifierReservationVehicule;
import com.diginamic.wemouv.service.ReservationService;
import com.diginamic.wemouv.service.SupprimerReservationVehicule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des réservations (de véhicules de service).
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ListeReservationVehicule listeReservationVehicule;
    private final SupprimerReservationVehicule supprimerReservationVehicule;
    private final ModifierReservationVehicule modifierReservationVehicule;

    public ReservationController(ReservationService reservationService,
                                 ListeReservationVehicule listeReservationVehicule,
                                 SupprimerReservationVehicule supprimerReservationVehicule,
                                 ModifierReservationVehicule modifierReservationVehicule) {
        this.reservationService = reservationService;
        this.listeReservationVehicule = listeReservationVehicule;
        this.supprimerReservationVehicule = supprimerReservationVehicule;
        this.modifierReservationVehicule = modifierReservationVehicule;
    }

    /**
     * Liste toutes les réservations de l'entreprise.
     */
    @GetMapping
    public List<Reservation> getAllReservations() {
        return listeReservationVehicule.lister();
    }

    /**
     * Récupère une réservation par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") Long id) {
        try {
            Reservation reservation = reservationService.findById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupère toutes les réservations d'un utilisateur donné.
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Reservation> getReservationsByUtilisateur(@PathVariable("utilisateurId") Long utilisateurId) {
        return reservationService.findByUtilisateur(utilisateurId);
    }

    /**
     * Récupère toutes les réservations liées à un véhicule.
     */
    @GetMapping("/vehicule/{vehiculeId}")
    public List<Reservation> getReservationsByVehicule(@PathVariable("vehiculeId") Long vehiculeId) {
        return listeReservationVehicule.listerParVehicule(vehiculeId);
    }

    /**
     * Crée une nouvelle réservation de véhicule.
     */
    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody ReservationRequest request,
            Authentication authentication) {

        Reservation savedReservation =
                reservationService.create(
                        request,
                        authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedReservation);
    }

    /**
     * Met à jour une réservation existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable("id") Long id,
                                               @RequestBody ReservationModificationRequest request) {
        try {
            Reservation updated = modifierReservationVehicule.modifier(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Annule/Supprime une réservation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable("id") Long id) {
        try {
            supprimerReservationVehicule.supprimer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
