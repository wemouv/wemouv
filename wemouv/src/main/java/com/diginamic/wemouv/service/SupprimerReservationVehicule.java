package com.diginamic.wemouv.service;

import com.diginamic.wemouv.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à la suppression d'une réservation de véhicule.
 * <p>
 * Ce service supprime une ligne de la table {@code reservation} identifiée
 * par son {@code id} (clé primaire auto-générée).
 * </p>
 *
 * <p>
 * Ce service complète {@link ListeReservationVehicule} dans le parcours utilisateur :
 * la liste permet de consulter les réservations, la suppression permet d'en retirer une.
 * </p>
 */
@Service
public class SupprimerReservationVehicule {

    private final ReservationRepository reservationRepository;

    /**
     * Constructeur avec injection du repository des réservations.
     *
     * @param reservationRepository repository utilisé pour accéder aux réservations
     */
    public SupprimerReservationVehicule(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Supprime une réservation de véhicule par son identifiant.
     * <p>
     * La méthode vérifie d'abord que la réservation existe, puis la supprime
     * de la table {@code reservation}. L'opération est exécutée dans une transaction.
     * </p>
     *
     * @param id identifiant de la réservation à supprimer
     * @throws RuntimeException si la réservation est introuvable
     */
    @Transactional
    public void supprimer(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Réservation introuvable");
        }
        reservationRepository.deleteById(id);
    }
}
