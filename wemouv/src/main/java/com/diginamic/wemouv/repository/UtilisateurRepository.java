package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA dédié à l'accès aux données de l'entité {@link Utilisateur}.
 * <p>
 * Cette interface permet d'interagir avec la table des collaborateurs
 * sans écrire manuellement les requêtes SQL ou l'implémentation d'un DAO.
 * </p>
 * <p>
 * En étendant {@code JpaRepository<Utilisateur, Long>}, Spring fournit
 * automatiquement les opérations standards de persistance :
 * <ul>
 * <li>{@code save(...)} : enregistrer ou mettre à jour un utilisateur</li>
 * <li>{@code findById(...)} : rechercher un utilisateur par son identifiant</li>
 * <li>{@code findAll()} : récupérer tous les utilisateurs</li>
 * <li>{@code deleteById(...)} : supprimer un utilisateur par son identifiant</li>
 * </ul>
 * </p>
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur à partir de son adresse e-mail.
     * <p>
     * L'e-mail étant strictement unique en base de données (identifiant de connexion),
     * cette méthode retourne un {@link Optional} afin de gérer proprement le cas où
     * aucun utilisateur ne correspond, évitant ainsi les {@link NullPointerException}.
     * </p>
     *
     * @param email l'adresse e-mail professionnelle recherchée
     * @return un {@link Optional} contenant l'utilisateur s'il existe, sinon un Optional vide
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Recherche tous les utilisateurs selon le statut d'activation de leur compte.
     * <p>Idéal pour filtrer les comptes désactivés (soft delete) lors de l'affichage.</p>
     *
     * @param compteActif {@code true} pour les comptes actifs, {@code false} pour les inactifs
     * @return la liste des utilisateurs correspondant à ce critère
     */
    List<Utilisateur> findByCompteActif(Boolean compteActif);

    /**
     * Recherche tous les utilisateurs portant un nom de famille précis.
     *
     * @param nom le nom de famille recherché
     * @return la liste des utilisateurs correspondants
     */
    List<Utilisateur> findByNom(String nom);

    /**
     * Recherche tous les utilisateurs portant un prénom précis.
     *
     * @param prenom le prénom recherché
     * @return la liste des utilisateurs correspondants
     */
    List<Utilisateur> findByPrenom(String prenom);

    /**
     * Recherche les utilisateurs dont le nom de famille contient le fragment de texte saisi.
     * <p>Recherche partielle (type LIKE %texte%) ignorant les majuscules/minuscules.</p>
     *
     * @param nom le fragment de texte recherché dans le nom
     * @return la liste des utilisateurs correspondants
     */
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);
}