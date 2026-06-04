package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier dédié à la consultation des réservations de véhicules de service.
 * <p>
 * Ce service centralise la lecture des données de la table {@code reservation}.
 * Il est utilisé par les contrôleurs pour afficher l'historique global de l'entreprise
 * ou le planning de disponibilité d'un véhicule spécifique.
 * </p>
 */
@Service
public class ListeReservationVehicule {

    /** Dépôt d'accès aux données des réservations. */
    private final ReservationRepository reservationRepository;

    /**
     * Constructeur avec injection du dépôt des réservations.
     *
     * @param reservationRepository le dépôt utilisé pour lire les réservations en base de données
     */
    public ListeReservationVehicule(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Récupère la liste exhaustive de toutes les réservations de véhicules enregistrées.
     *
     * @return la liste complète de toutes les réservations de l'entreprise
     */
    public List<Reservation> lister() {
        return reservationRepository.findAll();
    }

    /**
     * Filtre et récupère le planning des réservations pour un véhicule de service spécifique.
     *
     * @param vehiculeId l'identifiant unique du véhicule de service concerné
     * @return la liste des réservations affectées à ce véhicule
     */
    public List<Reservation> listerParVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId);
    }
}