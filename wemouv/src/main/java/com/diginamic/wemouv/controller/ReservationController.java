package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.dto.ReservationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.service.ListeReservationVehicule;
import com.diginamic.wemouv.service.ReservationService;
import com.diginamic.wemouv.service.SupprimerReservationVehicule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des réservations de véhicules de service.
 * <p>
 * Ce contrôleur expose les API permettant de gérer le cycle de vie des réservations
 * (création, modification, annulation) ainsi que la consultation des historiques
 * par utilisateur ou par véhicule.
 * </p>
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    /** Le service métier contenant la logique globale des réservations. */
    private final ReservationService reservationService;

    /** Le service dédié à la consultation et au filtrage des réservations de véhicules. */
    private final ListeReservationVehicule listeReservationVehicule;
    private final SupprimerReservationVehicule supprimerReservationVehicule;

    /**
     * Constructeur avec injection des services dédiés.
     *
     * @param reservationService le service gérant la logique métier des réservations
     * @param listeReservationVehicule le service gérant la lecture et le listage des réservations
     */
    public ReservationController(ReservationService reservationService,
                                 ListeReservationVehicule listeReservationVehicule,
                                 SupprimerReservationVehicule supprimerReservationVehicule) {
        this.reservationService = reservationService;
        this.listeReservationVehicule = listeReservationVehicule;
        this.supprimerReservationVehicule = supprimerReservationVehicule;
    }

    /**
     * Récupère la liste complète de toutes les réservations de l'entreprise.
     *
     * @return la liste de l'intégralité des réservations (HTTP 200)
     */
    @GetMapping
    public List<Reservation> getAllReservations() {
        return listeReservationVehicule.lister();
    }

    /**
     * Récupère une réservation spécifique par son identifiant unique.
     *
     * @param id l'identifiant unique de la réservation recherchée
     * @return un {@link ResponseEntity} contenant la réservation si elle est trouvée (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur approprié
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable("id") Long id) {
        try {
            Reservation reservation = reservationService.findById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Récupère l'historique de toutes les réservations effectuées par un utilisateur donné.
     *
     * @param utilisateurId l'identifiant de l'utilisateur (collaborateur) concerné
     * @return la liste de ses réservations (HTTP 200)
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Reservation> getReservationsByUtilisateur(@PathVariable("utilisateurId") Long utilisateurId) {
        return reservationService.findByUtilisateur(utilisateurId);
    }

    /**
     * Récupère le planning de toutes les réservations liées à un véhicule de service précis.
     *
     * @param vehiculeId l'identifiant du véhicule concerné
     * @return la liste des réservations affectées à ce véhicule (HTTP 200)
     */
    @GetMapping("/vehicule/{vehiculeId}")
    public List<Reservation> getReservationsByVehicule(@PathVariable("vehiculeId") Long vehiculeId) {
        return listeReservationVehicule.listerParVehicule(vehiculeId);
    }

    /**
     * Crée et enregistre une nouvelle réservation de véhicule.
     * <p>Utilise le contexte de sécurité Spring Security pour lier la réservation à l'utilisateur connecté.</p>
     *
     * @param request l'objet de transfert de données (DTO) contenant les dates et l'ID du véhicule
     * @param authentication l'objet d'authentification injecté automatiquement par Spring Security
     * @return un {@link ResponseEntity} contenant la réservation créée avec le statut HTTP 201 (Created)
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
     * Met à jour les informations d'une réservation existante (par exemple, modification des dates).
     *
     * @param id l'identifiant de la réservation à modifier
     * @param details l'entité contenant les nouvelles données à appliquer
     * @return un {@link ResponseEntity} contenant la réservation mise à jour (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur si introuvable
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable("id") Long id, @RequestBody Reservation details) {
        try {
            Reservation updated = reservationService.update(id, details);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Annule et supprime définitivement une réservation du système.
     *
     * @param id l'identifiant de la réservation à supprimer
     * @return HTTP 204 (No Content) si la suppression est réussie,
     * ou HTTP 404 (Not Found) avec le message d'erreur si introuvable
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