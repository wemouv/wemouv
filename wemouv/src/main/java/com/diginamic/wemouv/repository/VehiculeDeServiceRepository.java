package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Marque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA dédié à l'accès aux données de l'entité {@link VehiculeDeService}.
 * <p>
 * Cette interface permet de manipuler spécifiquement les véhicules de la flotte
 * d'entreprise, en complément du repository générique des véhicules.
 * </p>
 * <p>
 * En étendant {@code JpaRepository<VehiculeDeService, Long>}, Spring fournit
 * automatiquement les opérations standards de persistance (CRUD) en base de données.
 * </p>
 */
@Repository
public interface VehiculeDeServiceRepository extends JpaRepository<VehiculeDeService, Long> {

    /**
     * Recherche tous les véhicules de service stationnés à une localisation donnée.
     *
     * @param localisation la localisation ou le parking d'attache recherché
     * @return la liste des véhicules de service correspondants
     */
    List<VehiculeDeService> findByLocalisation(String localisation);

    /**
     * Recherche tous les véhicules de service selon leur disponibilité technique ou logistique.
     *
     * @param statut l'état du véhicule (ex: DISPONIBLE, EN_REPARATION) via l'énumération {@link Disponibilite}
     * @return la liste des véhicules de service correspondants
     */
    List<VehiculeDeService> findByStatut(Disponibilite statut);

    /**
     * Recherche les véhicules de service dont la plaque d'immatriculation contient le fragment de texte saisi.
     * <p>Recherche partielle (type LIKE %texte%) ignorant les majuscules/minuscules.
     * Idéal pour une barre de recherche administrateur.</p>
     *
     * @param immatriculation le fragment de plaque d'immatriculation recherché
     * @return la liste des véhicules correspondants
     */
    List<VehiculeDeService> findByImmatriculationContainingIgnoreCase(String immatriculation);

    /**
     * Recherche les véhicules de service appartenant à un constructeur spécifique.
     *
     * @param marque la marque du constructeur via l'énumération {@link Marque}
     * @return la liste des véhicules de service correspondants
     */
    List<VehiculeDeService> findByMarque(Marque marque);

}