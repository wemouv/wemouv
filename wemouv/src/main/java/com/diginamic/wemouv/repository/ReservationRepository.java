package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA dédié à l'accès aux données de l'entité {@link Reservation}.
 * <p>
 * Cette interface gère la persistance et la recherche des emprunts de véhicules
 * de service. En héritant de {@link JpaRepository}, elle fournit automatiquement
 * les opérations CRUD de base (save, findById, delete, etc.) sans implémentation manuelle.
 * </p>
 * <p>
 * Elle inclut également des méthodes de recherche personnalisées générées
 * dynamiquement par Spring ("Derived Query Methods") pour filtrer les réservations
 * par utilisateur, véhicule, statut ou période.
 * </p>
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Récupère l'ensemble des réservations effectuées par un utilisateur spécifique.
     *
     * @param utilisateurId l'identifiant du collaborateur (emprunteur)
     * @return la liste de ses réservations
     */
    List<Reservation> findByUtilisateurId(Long utilisateurId);

    /**
     * Récupère l'ensemble des réservations associées à un véhicule spécifique.
     * <p>Cette méthode est essentielle pour vérifier la disponibilité d'un véhicule
     * avant de valider une nouvelle réservation.</p>
     *
     * @param vehiculeId l'identifiant du véhicule de service ciblé
     * @return la liste des réservations planifiées pour ce véhicule
     */
    List<Reservation> findByVehiculeId(Long vehiculeId);

    /**
     * Récupère toutes les réservations ayant un statut particulier.
     *
     * @param statut l'état de la réservation (ex: CONFIRME, ANNULE)
     * @return la liste des réservations correspondantes
     */
    List<Reservation> findByStatut(Statut statut);

    /**
     * Récupère les réservations dont le départ est prévu exactement à la date et l'heure fournies.
     *
     * @param date la date et l'heure exactes de référence
     * @return la liste des réservations correspondantes
     */
    List<Reservation> findByDateDebut(LocalDateTime date);

    /**
     * Récupère les réservations dont la date de début est comprise dans un intervalle de temps défini.
     * <p>
     * Utilise le mot-clé {@code Between} de Spring Data JPA pour générer
     * automatiquement une condition SQL {@code WHERE date_debut BETWEEN ?1 AND ?2}.
     * </p>
     *
     * @param debut la date et l'heure minimales de l'intervalle
     * @param fin la date et l'heure maximales de l'intervalle
     * @return la liste des réservations débutant dans cette période
     */
    List<Reservation> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);
}