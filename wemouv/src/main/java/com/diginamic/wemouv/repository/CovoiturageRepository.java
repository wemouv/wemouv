package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
// Les méthodes de recherche renverront souvent une liste de covoiturages.

/**
 * Repository Spring Data JPA dédié à l'entité Covoiturage.
 *
 * <p>Cette interface permet d'accéder aux données de la table "covoiturage"
 * sans écrire manuellement une classe d'implémentation SQL ou JPA.</p>
 *
 * <p>En étendant JpaRepository<Covoiturage, Long>, Spring génère automatiquement
 * les opérations classiques de persistance :</p>
 * <ul>
 *   <li>save(...) : enregistrer ou mettre à jour un covoiturage</li>
 *   <li>findById(...) : rechercher un covoiturage par son identifiant</li>
 *   <li>findAll() : récupérer tous les covoiturages</li>
 *   <li>deleteById(...) : supprimer un covoiturage par son identifiant</li>
 * </ul>
 *
 * <p>On peut également déclarer des méthodes supplémentaires en suivant
 * la convention de nommage Spring Data JPA. Spring analysera leur nom
 * pour générer automatiquement la requête correspondante.</p>
 */
@Repository // On déclare ce composant comme repository Spring.

public interface CovoiturageRepository extends JpaRepository<Covoiturage, Long> {
    //Affiche tous les covoiturages
    List<Covoiturage> findAll();

    /**
     * Recherche tous les covoiturages créés par un organisateur donné.
     */
    List<Covoiturage> findByOrganisateurId(Long organisateurId);


    /**
     * Recherche tous les covoiturages associés à un véhicule donné.
     */
    List<Covoiturage> findByVehiculeId(Long vehiculeId);
    // Ici Spring comprend qu'il faut filtrer sur la relation "vehicule"
    // puis sur son identifiant.

    /**
     * Recherche tous les covoiturages selon leur statut.
     */
    List<Covoiturage> findByStatut(Statut statut);
    // Spring va filtrer sur la colonne "statut" de l'entité.

    /**
     * Recherche les covoiturages a une date de départ .
     * à la date fournie en paramètre.
     *
     * @param dateDepart date de référence
     * @return la liste des covoiturages prévus après cette date
     */
    List<Covoiturage> findByDateDepart(LocalDateTime dateDepart);

    /**
     * Recherche les covoiturages a une date donnée ayant un statut donné.
     * @param dateDepart date de référence
     * @param statut statut recherché
     * @return la liste filtrée par organisateur et par statut
     */
    List<Covoiturage> findByStatutAndDateDepart(Statut statut, LocalDateTime dateDepart);


    /**
     * Recherche les covoiturages d'un organisateur ayant un statut donné.
     *
     * @param organisateurId identifiant de l'organisateur
     * @param statut statut recherché
     * @return la liste filtrée par organisateur et par statut
     */
    List<Covoiturage> findByOrganisateurIdAndStatut(Long organisateurId, Statut statut);
    // Le mot-clé "And" permet de combiner plusieurs critères.
    // Spring génère une requête avec les 2 conditions.

    /**
     * Recherche les covoiturages dont l'adresse de départ contient
     * le texte fourni, sans tenir compte des majuscules/minuscules.
     *
     * @param adresseDepart texte recherché dans l'adresse de départ
     * @return la liste des covoiturages correspondants
     */
    List<Covoiturage> findByAdresseDepartContainingIgnoreCase(String adresseDepart);
    // "Containing" correspond à une recherche partielle, proche d'un LIKE %texte%.
    // "IgnoreCase" demande d'ignorer la casse.

    /**
     * Recherche les covoiturages dont l'adresse d'arrivée contient
     * le texte fourni, sans tenir compte des majuscules/minuscules.
     *
     * @param adresseArrive texte recherché dans l'adresse d'arrivée
     * @return la liste des covoiturages correspondants
     */
    List<Covoiturage> findByAdresseArriveContainingIgnoreCase(String adresseArrive);
    // Même principe que pour l'adresse de départ.

}



