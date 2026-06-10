package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.diginamic.wemouv.dto.UtilisateurUpdateRequest;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des profils collaborateurs (utilisateurs).
 * <p>
 * Ce contrôleur expose les API permettant de lister, consulter, modifier
 * et désactiver (soft delete) les comptes des utilisateurs de l'application.
 * </p>
 */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    /** Le service métier contenant la logique de gestion des utilisateurs. */
    private final UtilisateurService utilisateurService;

    /**
     * Constructeur avec injection du service dédié.
     *
     * @param utilisateurService le service métier gérant les utilisateurs
     */
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère la liste complète de tous les collaborateurs de l'entreprise.
     *
     * @return la liste de l'intégralité des utilisateurs (HTTP 200)
     */
    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.findAll();
    }



    /**
     * Retourne les informations de l'utilisateur actuellement authentifié.
     * <p>
     * L'identité de l'utilisateur est récupérée à partir du contexte de sécurité
     * Spring Security, alimenté par le JWT fourni dans la requête.
     * </p>
     *
     * @param authentication informations d'authentification de l'utilisateur connecté
     * @return l'utilisateur correspondant à l'email contenu dans le JWT
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        try {

            String email = authentication.getName();

            Utilisateur utilisateur =
                    utilisateurService.findByEmail(email);

            return ResponseEntity.ok(utilisateur);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        }
    }

    /**
     * Récupère les détails d'un collaborateur spécifique par son identifiant unique.
     *
     * @param id l'identifiant unique de l'utilisateur recherché
     * @return un {@link ResponseEntity} contenant l'utilisateur trouvé (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur approprié
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUtilisateurById(
            @PathVariable("id") Long id
    ) {
        try {
            Utilisateur utilisateur = utilisateurService.findById(id);
            return ResponseEntity.ok(utilisateur);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    /**
     * Met à jour les informations du profil d'un collaborateur (adresse, nom, prénom, etc.).
     *
     * @param id l'identifiant de l'utilisateur à modifier
     * @param details l'entité contenant les nouvelles données de profil à appliquer
     * @return un {@link ResponseEntity} contenant l'utilisateur mis à jour (HTTP 200),
     * ou un statut HTTP 404 (Not Found) avec le message d'erreur si introuvable
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUtilisateur(
            @PathVariable("id") Long id,
            @RequestBody UtilisateurUpdateRequest details) { // ✅
        try {
            Utilisateur updated = utilisateurService.update(id, details);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Désactive le compte d'un collaborateur (Suppression logique / Soft Delete).
     * <p>L'utilisateur n'est pas détruit en base, mais son accès est révoqué.</p>
     *
     * @param id l'identifiant de l'utilisateur à désactiver
     * @return HTTP 204 (No Content) si la désactivation est réussie,
     * ou HTTP 404 (Not Found) avec le message d'erreur si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtilisateur(
            @PathVariable("id") Long id
    ) {
        try {
            utilisateurService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Réactive un collaborateur à partir de son identifiant.
     *
     * <p>Renvoie un statut HTTP 204 si la réactivation réussit.
     * Les erreurs (utilisateur introuvable, déjà actif, etc.)
     * sont gérées par le GlobalExceptionHandler.</p>
     *
     * @param id identifiant du collaborateur à réactiver
     * @return 204 No Content si la réactivation est effectuée
     */
    @DeleteMapping("reactivate/{id}")
    public ResponseEntity<Void> reactivateUtilisateur(@PathVariable Long id) {
        utilisateurService.reactivate(id);
        return ResponseEntity.noContent().build();
    }





}