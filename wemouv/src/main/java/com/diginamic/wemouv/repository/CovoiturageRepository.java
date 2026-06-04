package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA dédié à l'accès aux données de l'entité {@link Covoiturage}.
 * <p>
 * En étendant {@code JpaRepository<Covoiturage, Long>}, Spring génère automatiquement
 * les opérations classiques de persistance (CRUD) en base de données.
 * </p>
 * <p>
 * Ce composant exploite également la puissance des "Derived Query Methods" :
 * Spring analyse le nom des méthodes déclarées ici pour générer et exécuter
 * automatiquement les requêtes SQL correspondantes sous le capot.
 * </p>
 */
@Repository
public interface CovoiturageRepository extends JpaRepository<Covoiturage, Long> {

    /**
     * Récupère l'intégralité des covoiturages enregistrés en base.
     *
     * @return la liste globale des covoiturages
     */
    List<Covoiturage> findAll();

    /**
     * Recherche tous les covoiturages créés par un organisateur spécifique.
     *
     * @param organisateurId l'identifiant de l'organisateur
     * @return la liste des covoiturages associés à cet utilisateur
     */
    List<Covoiturage> findByOrganisateurId(Long organisateurId);

    /**
     * Recherche tous les covoiturages affectés à un véhicule spécifique.
     * <p>Filtre sur la relation "vehicule" puis sur son identifiant.</p>
     *
     * @param vehiculeId l'identifiant du véhicule
     * @return la liste des covoiturages utilisant ce véhicule
     */
    List<Covoiturage> findByVehiculeId(Long vehiculeId);

    /**
     * Recherche tous les covoiturages ayant un statut particulier.
     *
     * @param statut l'état du covoiturage (ex: CONFIRME, ANNULE)
     * @return la liste des covoiturages correspondants
     */
    List<Covoiturage> findByStatut(Statut statut);

    /**
     * Recherche les covoiturages prévus à une date de départ exacte.
     *
     * @param dateDepart la date et l'heure exactes de départ
     * @return la liste des covoiturages partant à cette date
     */
    List<Covoiturage> findByDateDepart(LocalDateTime dateDepart);

    /**
     * Recherche les covoiturages combinant un statut spécifique ET une date de départ exacte.
     *
     * @param statut l'état du covoiturage
     * @param dateDepart la date et l'heure de départ
     * @return la liste des covoiturages correspondants aux deux critères
     */
    List<Covoiturage> findByStatutAndDateDepart(Statut statut, LocalDateTime dateDepart);

    /**
     * Recherche les covoiturages créés par un organisateur ET ayant un statut spécifique.
     *
     * @param organisateurId l'identifiant de l'organisateur
     * @param statut l'état du covoiturage recherché
     * @return la liste des covoiturages filtrée
     */
    List<Covoiturage> findByOrganisateurIdAndStatut(Long organisateurId, Statut statut);

    /**
     * Recherche les covoiturages dont l'adresse de départ contient le texte fourni.
     * <p>Recherche partielle de type {@code LIKE %texte%} ignorant les majuscules/minuscules.</p>
     *
     * @param adresseDepart le fragment de texte recherché dans l'adresse
     * @return la liste des covoiturages correspondants
     */
    List<Covoiturage> findByAdresseDepartContainingIgnoreCase(String adresseDepart);

    /**
     * Recherche les covoiturages dont l'adresse d'arrivée contient le texte fourni.
     * <p>Recherche partielle de type {@code LIKE %texte%} ignorant les majuscules/minuscules.</p>
     *
     * @param adresseArrive le fragment de texte recherché dans l'adresse
     * @return la liste des covoiturages correspondants
     */
    List<Covoiturage> findByAdresseArriveContainingIgnoreCase(String adresseArrive);

    /**
     * Recherche les covoiturages futurs d'un organisateur (annonces en cours ou à venir),
     * triés par date de départ chronologique (de la plus proche à la plus lointaine).
     *
     * @param organisateurId l'identifiant de l'organisateur
     * @param date la date de référence (généralement {@code LocalDateTime.now()})
     * @return la liste triée des covoiturages à venir
     */
    List<Covoiturage> findByOrganisateurIdAndDateDepartAfterOrderByDateDepartAsc(Long organisateurId, LocalDateTime date);

    /**
     * Recherche l'historique des covoiturages passés d'un organisateur,
     * triés par date de départ anti-chronologique (du plus récent au plus ancien).
     *
     * @param organisateurId l'identifiant de l'organisateur
     * @param date la date de référence (généralement {@code LocalDateTime.now()})
     * @return la liste triée de l'historique des covoiturages
     */
    List<Covoiturage> findByOrganisateurIdAndDateDepartBeforeOrderByDateDepartDesc(Long organisateurId, LocalDateTime date);

    /**
     * Recherche tous les covoiturages futurs associés à un véhicule spécifique.
     * <p>Cette méthode est particulièrement utile pour annuler les trajets en cascade
     * lorsqu'un véhicule tombe en panne.</p>
     *
     * @param vehicule l'entité du véhicule concerné
     * @param date la date de référence (généralement {@code LocalDateTime.now()})
     * @return la liste des covoiturages futurs impactant ce véhicule
     */
    List<Covoiturage> findByVehiculeAndDateDepartAfter(Vehicule vehicule, LocalDateTime date);

}