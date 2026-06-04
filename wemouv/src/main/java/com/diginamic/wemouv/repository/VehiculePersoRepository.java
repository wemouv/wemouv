package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.VehiculePerso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA dédié à l'accès aux données de l'entité {@link VehiculePerso}.
 * <p>
 * Cette interface permet de manipuler spécifiquement les véhicules personnels
 * appartenant aux collaborateurs de l'entreprise.
 * </p>
 * <p>
 * En étendant {@code JpaRepository<VehiculePerso, Long>}, Spring fournit automatiquement
 * les opérations standards de persistance (CRUD) en base de données sans nécessiter
 * l'implémentation d'un DAO manuel.
 * </p>
 */
@Repository
public interface VehiculePersoRepository extends JpaRepository<VehiculePerso, Long> {

    /**
     * Recherche l'ensemble des véhicules personnels déclarés par un collaborateur spécifique.
     * <p>
     * Cette méthode est particulièrement utile pour afficher la liste déroulante
     * des véhicules d'un utilisateur lorsqu'il souhaite proposer un nouveau covoiturage.
     * </p>
     *
     * @param proprietaireId l'identifiant du collaborateur (propriétaire du véhicule)
     * @return la liste de ses véhicules personnels enregistrés
     */
    List<VehiculePerso> findByProprietaireId(Long proprietaireId);
}