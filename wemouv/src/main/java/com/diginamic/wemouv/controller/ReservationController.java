package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des réservations (de véhicules de service).
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Liste toutes les réservations de l'entreprise (réservé aux admins en production).
     */
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Récupère une réservation par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère toutes les réservations d'un utilisateur donné.
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Reservation> getReservationsByUtilisateur(@PathVariable Long utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    /**
     * Récupère toutes les réservations liées à un véhicule.
     */
    @GetMapping("/vehicule/{vehiculeId}")
    public List<Reservation> getReservationsByVehicule(@PathVariable Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId);
    }

    /**
     * Crée une nouvelle réservation de véhicule.
     */
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    /**
     * Met à jour une réservation existante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation details) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setDateDebut(details.getDateDebut());
            reservation.setDateFin(details.getDateFin());
            reservation.setVehicule(details.getVehicule());
            reservation.setUtilisateur(details.getUtilisateur());
            reservation.setStatut(details.getStatut());
            
            Reservation updated = reservationRepository.save(reservation);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Annule/Supprime une réservation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        return reservationRepository.findById(id).map(reservation -> {
            reservationRepository.delete(reservation);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
    
}