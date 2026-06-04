package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service dédié à la modification d'une réservation de véhicule.
 * <p>
 * Permet de mettre à jour la date de prise, la date de restitution
 * et/ou le véhicule choisi, sans modifier les autres informations
 * (utilisateur, statut).
 * </p>
 */
@Service
public class ModifierReservationVehicule {

    private final ReservationRepository reservationRepository;
    private final VehiculeRepository vehiculeRepository;

    public ModifierReservationVehicule(ReservationRepository reservationRepository,
                                       VehiculeRepository vehiculeRepository) {
        this.reservationRepository = reservationRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    /**
     * Modifie une réservation existante (champs optionnels).
     * <p>
     * Seuls les champs non {@code null} du DTO sont appliqués :
     * {@code dateDebut}, {@code dateFin}, {@code vehiculeId}.
     * </p>
     *
     * @param id      identifiant de la réservation à modifier
     * @param request données de modification (champs optionnels)
     * @return la réservation mise à jour
     * @throws RuntimeException      si la réservation ou le véhicule est introuvable
     * @throws IllegalStateException si aucun champ à modifier ou si les dates sont incohérentes
     */
    @Transactional
    public Reservation modifier(Long id, ReservationModificationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        boolean modifie = false;

        if (request.getDateDebut() != null) {
            reservation.setDateDebut(request.getDateDebut());
            modifie = true;
        }
        if (request.getDateFin() != null) {
            reservation.setDateFin(request.getDateFin());
            modifie = true;
        }
        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                    .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
            reservation.setVehicule(vehicule);
            modifie = true;
        }

        if (!modifie) {
            throw new IllegalStateException("Aucun champ à modifier (dateDebut, dateFin ou vehiculeId requis)");
        }

        LocalDateTime debut = reservation.getDateDebut();
        LocalDateTime fin = reservation.getDateFin();
        if (debut != null && fin != null && !debut.isBefore(fin)) {
            throw new IllegalStateException("La date de prise doit être antérieure à la date de restitution");
        }

        return reservationRepository.save(reservation);
    }
}
