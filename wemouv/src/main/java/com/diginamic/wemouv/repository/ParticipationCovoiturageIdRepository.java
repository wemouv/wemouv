package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA dédié à la gestion des inscriptions aux covoiturages.
 * <p>
 * Cette interface gère l'entité {@link ParticipationCovoiturage} qui représente
 * la table de jointure entre les utilisateurs et les trajets.
 * Elle utilise la clé primaire composite {@link ParticipationCovoiturageId}.
 * </p>
 */
@Repository
public interface ParticipationCovoiturageIdRepository
        extends JpaRepository<ParticipationCovoiturage, ParticipationCovoiturageId> {

    /**
     * Vérifie si un collaborateur est déjà inscrit comme passager à un trajet spécifique.
     * <p>
     * Spring Data JPA traduit automatiquement cette méthode en une requête SQL
     * optimisée de type {@code SELECT EXISTS (...)}.
     * </p>
     *
     * @param utilisateurId l'identifiant du collaborateur (passager)
     * @param covoiturageId l'identifiant du trajet de covoiturage
     * @return {@code true} si la participation existe déjà, {@code false} sinon
     */
    boolean existsByUtilisateurIdAndCovoiturageId(Long utilisateurId, Long covoiturageId);
}