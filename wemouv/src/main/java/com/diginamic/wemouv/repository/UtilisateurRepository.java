package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA dédié à l'entité Utilisateur.
 *
 * <p>Cette interface permet d'accéder aux données des utilisateurs
 * sans écrire manuellement les requêtes SQL ou l'implémentation DAO.</p>
 *
 * <p>En étendant {@code JpaRepository<Utilisateur, Long>}, Spring fournit
 * automatiquement les opérations standards de persistance :</p>
 * <ul>
 *     <li>{@code save(...)} : enregistrer ou mettre à jour un utilisateur</li>
 *     <li>{@code findById(...)} : rechercher un utilisateur par son identifiant</li>
 *     <li>{@code findAll()} : récupérer tous les utilisateurs</li>
 *     <li>{@code deleteById(...)} : supprimer un utilisateur par son identifiant</li>
 * </ul>
 *
 * <p>Des méthodes de recherche personnalisées peuvent également être ajoutées
 * en respectant la convention de nommage de Spring Data JPA.</p>
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur à partir de son adresse e-mail.
     *
     * <p>L'e-mail étant généralement unique, cette méthode retourne un
     * {@link Optional} afin de gérer proprement le cas où aucun utilisateur
     * n'est trouvé.</p>
     *
     * @param email adresse e-mail recherchée
     * @return un {@link Optional} contenant l'utilisateur s'il existe,
     *         sinon un {@link Optional} vide et evite les NullPointExeption
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Recherche tous les utilisateurs ayant un compte actif ou inactif.
     *
     * @param compteActif true pour les comptes actifs, false pour les comptes inactifs
     * @return la liste des utilisateurs correspondant à l'état du compte
     */
    List<Utilisateur> findByCompteActif(Boolean compteActif);

    /**
     * Recherche tous les utilisateurs ayant un nom donné.
     *
     * @param nom nom recherché
     * @return la liste des utilisateurs portant ce nom
     */
    List<Utilisateur> findByNom(String nom);

    /**
     * Recherche tous les utilisateurs ayant un prénom donné.
     *
     * @param prenom prénom recherché
     * @return la liste des utilisateurs portant ce prénom
     */
    List<Utilisateur> findByPrenom(String prenom);

    /**
     * Recherche les utilisateurs dont le nom contient la valeur saisie,
     * sans tenir compte des majuscules et minuscules.
     *
     * @param nom texte recherché dans le nom
     * @return la liste des utilisateurs correspondants
     */
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);
}