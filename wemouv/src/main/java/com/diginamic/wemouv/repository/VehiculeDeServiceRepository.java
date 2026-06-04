package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA dédié à l'entité VehiculeDeService.
 *
 * <p>Cette interface permet de manipuler les véhicules de service
 * de façon spécifique, en complément du repository générique VehiculeRepository.</p>
 *
 * <p>En étendant {@code JpaRepository<VehiculeDeService, Long>},
 * Spring fournit automatiquement les opérations standards de persistance
 * comme {@code save(...)}, {@code findById(...)}, {@code findAll()}
 * et {@code deleteById(...)}.</p>
 */
@Repository
public interface VehiculeDeServiceRepository extends JpaRepository<VehiculeDeService, Long> {

    /**
     * Recherche tous les véhicules de service ayant une localisation donnée.
     *
     * @param localisation localisation recherchée
     * @return la liste des véhicules de service correspondants
     */
    List<VehiculeDeService> findByLocalisation(String localisation);

    /**
     * Recherche tous les véhicules de service ayant un statut donné.
     *
     * @param statut statut recherché
     * @return la liste des véhicules de service correspondants
     */
    List<VehiculeDeService> findByStatut(Statut statut);

    /**
     * TÂCHE 15 : Recherche les véhicules par immatriculation (recherche partielle et insensible à la casse).
     */
    List<VehiculeDeService> findByImmatriculationContainingIgnoreCase(String immatriculation);

    /**
     * TÂCHE 15 : Recherche les véhicules par marque (recherche partielle et insensible à la casse).
     */
    List<VehiculeDeService> findByMarque(Marque marque);

}
