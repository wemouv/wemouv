package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.dto.UtilisateurUpdateRequest;
import com.diginamic.wemouv.entity.Utilisateur;

import java.util.List;

/**
 * Contrat d'interface définissant les opérations métier disponibles pour la gestion des utilisateurs.
 * <p>
 * Ce service agit comme une façade pour les contrôleurs, garantissant un couplage faible.
 * Il regroupe l'ensemble des actions possibles sur les collaborateurs : récupération,
 * création, mise à jour, recherches avancées et gestion du cycle de vie (activation/désactivation).
 * </p>
 */
public interface UtilisateurService {

    /**
     * Récupère la liste exhaustive de tous les utilisateurs enregistrés dans le système.
     *
     * @return une liste contenant toutes les entités {@link Utilisateur}
     */
    List<Utilisateur> findAll();

    /**
     * Recherche un utilisateur par son identifiant technique unique.
     *
     * @param id l'identifiant de l'utilisateur recherché
     * @return l'entité {@link Utilisateur} correspondante
     * @throws RuntimeException (selon l'implémentation) si l'identifiant n'existe pas
     */
    Utilisateur findById(Long id);

    /**
     * Recherche un utilisateur via son adresse e-mail de connexion.
     *
     * @param email l'adresse e-mail exacte à rechercher
     * @return l'entité {@link Utilisateur} correspondante
     * @throws RuntimeException (selon l'implémentation) si aucun compte n'est lié à cet e-mail
     */
    Utilisateur findByEmail(String email);

    /**
     * Inscrit un nouvel utilisateur dans le système à partir des données fournies.
     * La logique métier sous-jacente gère généralement la génération de mot de passe,
     * l'attribution du rôle et l'envoi d'e-mails d'activation.
     *
     * @param request le DTO contenant les informations nécessaires à la création (nom, email, rôle, etc.)
     * @return l'entité {@link Utilisateur} telle qu'elle a été sauvegardée en base (avec son nouvel ID)
     */
    Utilisateur create(RegisterRequest request);

    /**
     * Met à jour les informations d'un profil utilisateur existant.
     *
     * @param id      l'identifiant de l'utilisateur à modifier
     * @param details le DTO contenant les nouvelles informations (seuls les champs renseignés sont mis à jour)
     * @return l'entité {@link Utilisateur} après l'application des modifications
     */
    Utilisateur update(Long id, UtilisateurUpdateRequest details);

    /**
     * Supprime définitivement (Hard Delete) un utilisateur du système.
     *
     * @param id l'identifiant de l'utilisateur à supprimer
     */
    void delete(Long id);

    /**
     * Désactive un compte utilisateur (Soft Delete) sans le supprimer de la base de données.
     * Cette action révoque ses droits d'accès à la plateforme.
     *
     * @param id l'identifiant de l'utilisateur à désactiver
     */
    void softDelete(Long id);

    /**
     * Réactive un compte utilisateur préalablement désactivé, lui redonnant accès à la plateforme.
     *
     * @param id l'identifiant de l'utilisateur à réactiver
     */
    void reactivate(Long id);

    /**
     * Effectue une recherche globale sur les utilisateurs à partir d'un mot-clé.
     * Le filtre s'applique généralement sur le nom, le prénom ou l'adresse e-mail.
     *
     * @param terme le mot-clé ou la chaîne de caractères à rechercher
     * @return une liste filtrée d'entités {@link Utilisateur} correspondant au critère
     */
    List<Utilisateur> search(String terme);

    /**
     * Sauvegarde ou met à jour directement une entité utilisateur.
     * Opération de bas niveau souvent réservée aux traitements internes de l'application.
     *
     * @param utilisateur l'entité {@link Utilisateur} à persister
     * @return l'entité sauvegardée
     */
    Utilisateur save(Utilisateur utilisateur);
}