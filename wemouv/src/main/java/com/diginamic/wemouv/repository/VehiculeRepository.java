
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
 * Repository Spring Data JPA dédié à l'entité Vehicule.
 *
 * <p>Cette interface permet d'accéder aux données des véhicules
 * sans écrire manuellement les requêtes SQL ou une implémentation DAO.</p>
 *
 * <p>En étendant {@code JpaRepository<Vehicule, Long>}, Spring fournit
 * automatiquement les opérations standards de persistance :</p>
 * <ul>
 *     <li>{@code save(...)} : enregistrer ou mettre à jour un véhicule</li>
 *     <li>{@code findById(...)} : rechercher un véhicule par son identifiant</li>
 *     <li>{@code findAll()} : récupérer tous les véhicules</li>
 *     <li>{@code deleteById(...)} : supprimer un véhicule par son identifiant</li>
 * </ul>
 *
 * <p>Des méthodes de recherche personnalisées peuvent être ajoutées
 * en respectant la convention de nommage de Spring Data JPA.</p>
 */
@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    /**
     * Recherche un véhicule à partir de son immatriculation.
     *
     * <p>L'immatriculation étant généralement unique, cette méthode retourne
     * un {@link Optional} afin de gérer le cas où aucun véhicule n'est trouvé.</p>
     *
     * @param immatriculation immatriculation recherchée
     * @return un {@link Optional} contenant le véhicule s'il existe,
     *         sinon un {@link Optional} vide
     */
    Optional<Vehicule> findByImmatriculation(String immatriculation);

    /**
     * Recherche tous les véhicules d'une marque donnée.
     *
     * @param marque marque recherchée
     * @return la liste des véhicules correspondant à la marque
     */
    List<Vehicule> findByMarque(Marque marque);

    /**
     * Recherche tous les véhicules d'une motorisation donnée.
     *
     * @param motorisation type de motorisation recherché
     * @return la liste des véhicules correspondant à cette motorisation
     */
    List<Vehicule> findByMotorisation(Motorisation motorisation);

    /**
     * Recherche tous les véhicules d'une catégorie donnée.
     *
     * @param categorie catégorie recherchée
     * @return la liste des véhicules correspondant à cette catégorie
     */
    List<Vehicule> findByCategorie(Categorie categorie);

    /**
     * Recherche tous les véhicules ayant un nombre de places supérieur
     * ou égal à la valeur indiquée.
     *
     * @param nbPlace nombre minimal de places
     * @return la liste des véhicules correspondant au critère
     */
    List<Vehicule> findByNbPlaceGreaterThanEqual(int nbPlace);
}