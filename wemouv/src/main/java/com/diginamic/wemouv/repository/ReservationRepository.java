package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Repository Spring Data JPA dédié à l'entité Reservation.
 *
 * <p>Cette interface permet de gérer les réservations de véhicules
 * sans écrire manuellement l'implémentation des opérations de base.</p>
 *
 * <p>En héritant de JpaRepository, on récupère automatiquement les méthodes
 * classiques de persistance :</p>
 * <ul>
 *     <li>save(...) pour enregistrer une réservation</li>
 *     <li>findById(...) pour rechercher une réservation par son identifiant</li>
 *     <li>findAll() pour récupérer toutes les réservations</li>
 *     <li>deleteById(...) pour supprimer une réservation</li>
 * </ul>
 *
 * <p>On peut aussi ajouter des méthodes de recherche personnalisées en suivant
 * la convention de nommage de Spring Data JPA.</p>
**/


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Retourne toutes les réservations d'un utilisateur.
     *
     * @param utilisateurId identifiant de l'utilisateur
     * @return liste des réservations de cet utilisateur
     */
    List<Reservation> findByUtilisateurId(Long utilisateurId);

    /**
     * Retourne toutes les réservations d'un véhicule.
     *
     * @param vehiculeId identifiant du véhicule
     * @return liste des réservations pour ce véhicule
     */
    List<Reservation> findByVehiculeId(Long vehiculeId);

    /**
     * Retourne toutes les réservations selon un statut donné.
     *
     * @param statut statut de réservation
     * @return liste des réservations correspondantes
     */
    List<Reservation> findByStatut(Statut statut);

    /**
     * Retourne les réservations a une date donnée.
     *
     * @param date date de référence
     * @return liste des réservations les reservation a une date donnée
     */
    List<Reservation> findByDateDebut(LocalDateTime date);

    /**
     * Retourne les réservations comprises entre deux dates de début.
     *
     * @param debut date minimale
     * @param fin date maximale
     * @return liste des réservations dans l'intervalle
     */
    List<Reservation> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);
}
