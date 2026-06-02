package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des profils collaborateurs.
 */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // Injection du Service uniquement
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère la liste de tous les collaborateurs.
     */
    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.findAll();
    }

    /**
     * Récupère les détails d'un collaborateur par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(
            @PathVariable("id") Long id
    ) {
        try {
            Utilisateur utilisateur = utilisateurService.findById(id);
            return ResponseEntity.ok(utilisateur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Met à jour le profil d'un collaborateur (adresse, nom, prenom, etc.).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(
            @PathVariable("id") Long id,
            @RequestBody Utilisateur details
    ) {
        try {
            Utilisateur updated =
                    utilisateurService.update(id, details);

            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Désactive un collaborateur.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(
            @PathVariable("id") Long id
    ) {
        try {
            utilisateurService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}