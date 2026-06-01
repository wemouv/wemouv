package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.VehiculePerso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA dédié à l'entité VehiculePerso.
 *
 * <p>Cette interface permet de manipuler les véhicules personnels
 * sans écrire manuellement l'implémentation des opérations d'accès aux données.</p>
 *
 * <p>En étendant {@code JpaRepository<VehiculePerso, Long>},
 * Spring fournit automatiquement les opérations classiques
 * comme {@code save(...)}, {@code findById(...)}, {@code findAll()}
 * et {@code deleteById(...)}.</p>
 *
 * <p>Des méthodes de recherche spécifiques peuvent être ajoutées
 * en respectant les conventions de nommage de Spring Data JPA.</p>
 */
@Repository
public interface VehiculePersoRepository extends JpaRepository<VehiculePerso, Long> {

    /**
     * Recherche tous les véhicules personnels appartenant à un utilisateur donné.
     *
     * @param proprietaireId identifiant du propriétaire
     * @return la liste des véhicules personnels de cet utilisateur
     */
    List<VehiculePerso> findByProprietaireId(Long proprietaireId);
}