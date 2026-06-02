package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des réservations (de véhicules de service).
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    // Injection du Service uniquement
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Liste toutes les réservations de l'entreprise.
     */
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    /**
     * Récupère une réservation par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
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
    public List<Reservation> getReservationsByUtilisateur(@PathVariable Long utilisateurId) {
        return reservationService.findByUtilisateur(utilisateurId);
    }

    /**
     * Récupère toutes les réservations liées à un véhicule.
     */
    @GetMapping("/vehicule/{vehiculeId}")
    public List<Reservation> getReservationsByVehicule(@PathVariable Long vehiculeId) {
        return reservationService.findByVehicule(vehiculeId);
    }

    /**
     * Crée une nouvelle réservation de véhicule.
     */
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.create(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    /**
     * Met à jour une réservation existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation details) {
        try {
            Reservation updated = reservationService.update(id, details);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Annule/Supprime une réservation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}