package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA racine dédié à l'accès aux données de l'entité {@link Vehicule}.
 * <p>
 * Ce repository permet de requêter l'ensemble des véhicules de l'application de
 * manière polymorphe (qu'il s'agisse de véhicules personnels ou de service).
 * </p>
 * <p>
 * En étendant {@code JpaRepository<Vehicule, Long>}, Spring fournit automatiquement
 * les opérations standards de persistance (CRUD) sans implémentation manuelle.
 * </p>
 */
@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    /**
     * Recherche un véhicule (personnel ou de service) à partir de sa plaque d'immatriculation.
     * <p>
     * L'immatriculation étant strictement unique en base de données, cette méthode
     * retourne un {@link Optional} afin de gérer proprement l'absence de résultat
     * et d'éviter les NullPointerException.
     * </p>
     *
     * @param immatriculation la plaque d'immatriculation officielle recherchée
     * @return un {@link Optional} contenant le véhicule s'il existe, sinon un Optional vide
     */
    Optional<Vehicule> findByImmatriculation(String immatriculation);

    /**
     * Recherche tous les véhicules appartenant à un constructeur automobile spécifique.
     *
     * @param marque la marque recherchée via l'énumération {@link Marque}
     * @return la liste globale des véhicules correspondants
     */
    List<Vehicule> findByMarque(Marque marque);

    /**
     * Recherche tous les véhicules équipés d'un type de motorisation spécifique.
     *
     * @param motorisation le type d'énergie recherché via l'énumération {@link Motorisation}
     * @return la liste globale des véhicules correspondants
     */
    List<Vehicule> findByMotorisation(Motorisation motorisation);

    /**
     * Recherche tous les véhicules appartenant à une catégorie (gabarit) spécifique.
     *
     * @param categorie la catégorie recherchée via l'énumération {@link Categorie}
     * @return la liste globale des véhicules correspondants
     */
    List<Vehicule> findByCategorie(Categorie categorie);

    /**
     * Recherche tous les véhicules dont la capacité d'accueil est supérieure
     * ou égale au nombre de places spécifié.
     * <p>
     * Utilise le mot-clé {@code GreaterThanEqual} pour générer une condition SQL
     * {@code WHERE nb_place >= ?1}. Idéal pour vérifier si un véhicule peut
     * accueillir un groupe de covoiturage.
     * </p>
     *
     * @param nbPlace le nombre minimum de places requises (conducteur inclus)
     * @return la liste des véhicules disposant de la capacité suffisante
     */
    List<Vehicule> findByNbPlaceGreaterThanEqual(int nbPlace);
}