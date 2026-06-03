package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service dédié à l'affichage des réservations de véhicules.
 * <p>
 * Ce service centralise la consultation des lignes de la table {@code reservation}
 * (période, statut, utilisateur, véhicule).
 * http GET localhost:8080/api/reservations : liste toutes les réservations
 * http GET localhost:8080/api/reservations/vehicule/{vehiculeId} : liste les réservations d'un véhicule    
 * </p>
 */
@Service
public class ListeReservationVehicule {

    private final ReservationRepository reservationRepository;

    /**
     * Constructeur avec injection du repository des réservations.
     *
     * @param reservationRepository repository utilisé pour accéder aux réservations
     */
    public ListeReservationVehicule(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Retourne la liste complète de toutes les réservations de véhicules.
     *
     * @return toutes les réservations enregistrées en base
     */
    public List<Reservation> lister() {
        return reservationRepository.findAll();
    }

    /**
     * Retourne les réservations associées à un véhicule donné.
     *
     * @param vehiculeId identifiant du véhicule
     * @return les réservations de ce véhicule
     */
    public List<Reservation> listerParVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId);
    }
}
