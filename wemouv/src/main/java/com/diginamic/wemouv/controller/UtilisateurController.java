package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.dto.UtilisateurUpdateRequest;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.findAll();
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            Utilisateur utilisateur = utilisateurService.findByEmail(email);
            return ResponseEntity.ok(utilisateur);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUtilisateurById(@PathVariable("id") Long id) {
        try {
            Utilisateur utilisateur = utilisateurService.findById(id);
            return ResponseEntity.ok(utilisateur);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Crée un nouveau collaborateur et envoie un email d'activation.
     *
     * @param details le DTO contenant les informations du nouveau compte
     * @return un {@link ResponseEntity} contenant l'utilisateur créé (HTTP 201)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUtilisateur(@Valid  @RequestBody RegisterRequest details) {
        try {
            Utilisateur cree = utilisateurService.create(details);
            return ResponseEntity.status(HttpStatus.CREATED).body(cree);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Met à jour les informations du profil d'un collaborateur.
     *
     * @param id l'identifiant de l'utilisateur à modifier
     * @param details le DTO contenant les nouvelles données de profil
     * @return un {@link ResponseEntity} contenant l'utilisateur mis à jour (HTTP 200)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUtilisateur(
            @PathVariable("id") Long id,
            @RequestBody UtilisateurUpdateRequest details) {
        try {
            Utilisateur updated = utilisateurService.update(id, details);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Désactive le compte d'un collaborateur (Soft Delete).
     *
     * @param id l'identifiant de l'utilisateur à désactiver
     * @return HTTP 204 si réussi, HTTP 404 si introuvable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable("id") Long id) {
        try {
            utilisateurService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Réactive un collaborateur désactivé.
     *
     * @param id identifiant du collaborateur à réactiver
     * @return HTTP 204 si réussi
     */
    @DeleteMapping("reactivate/{id}")
    public ResponseEntity<Void> reactivateUtilisateur(@PathVariable Long id) {
        utilisateurService.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recherche des utilisateurs par terme (nom, prénom ou email).
     *
     * @param terme le terme de recherche
     * @return la liste des utilisateurs correspondants
     */
    @GetMapping("/filtrer/search")
    public ResponseEntity<List<Utilisateur>> searchUtilisateurs(@RequestParam("q") String terme) {
        List<Utilisateur> resultats = utilisateurService.search(terme);
        return ResponseEntity.ok(resultats);
    }
}