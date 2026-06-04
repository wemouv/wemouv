package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA dédié à la consultation et la gestion des participations.
 * <p>
 * Contrairement au repository gérant uniquement la clé composite, celui-ci est
 * axé sur la recherche métier : il permet notamment de retrouver l'historique
 * des trajets d'un utilisateur en tant que passager (passés ou à venir) en
 * naviguant à travers les relations des entités.
 * </p>
 */
@Repository
public interface ParticipationCovoiturageRepository extends JpaRepository<ParticipationCovoiturage, ParticipationCovoiturageId> {

    /**
     * Récupère la liste des trajets futurs auxquels un utilisateur est inscrit en tant que passager.
     * <p>
     * Spring Data effectue une jointure implicite : il cherche les participations
     * de l'utilisateur, regarde le covoiturage lié, et filtre si sa date de départ
     * est postérieure à la date fournie.
     * </p>
     *
     * @param utilisateurId l'identifiant de l'utilisateur (passager)
     * @param date la date de référence (généralement la date et l'heure actuelles)
     * @return la liste des participations pour les trajets à venir
     */
    List<ParticipationCovoiturage> findByUtilisateurIdAndCovoiturageDateDepartAfter(Long utilisateurId, LocalDateTime date);

    /**
     * Récupère l'historique des trajets passés auxquels un utilisateur a participé.
     * <p>
     * Le fonctionnement est identique à la recherche des trajets futurs,
     * mais filtre sur les dates antérieures à la date de référence.
     * </p>
     *
     * @param utilisateurId l'identifiant de l'utilisateur (passager)
     * @param date la date de référence (généralement la date et l'heure actuelles)
     * @return la liste des participations pour les trajets déjà effectués
     */
    List<ParticipationCovoiturage> findByUtilisateurIdAndCovoiturageDateDepartBefore(Long utilisateurId, LocalDateTime date);

    /**
     * Supprime toutes les participations associées à un covoiturage spécifique.
     * <p>
     * Cette méthode est particulièrement utile pour nettoyer la base de données
     * en cascade lorsqu'un covoiturage est supprimé ou définitivement annulé.
     * </p>
     *
     * @param covoiturageId l'identifiant du covoiturage dont il faut supprimer les passagers inscrits
     */
    void deleteByCovoiturageId(Long covoiturageId);
}