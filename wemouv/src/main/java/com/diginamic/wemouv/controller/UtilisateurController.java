package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des profils collaborateurs.
 */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère la liste de tous les collaborateurs.
     */
    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Récupère les détails d'un collaborateur par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Met à jour le profil d'un collaborateur (adresse, nom, prenom, etc.).
     * Note : En production, on ne met pas à jour le mot de passe ou le rôle ici.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur details) {
        return utilisateurRepository.findById(id).map(utilisateur -> {
            utilisateur.setNom(details.getNom());
            utilisateur.setPrenom(details.getPrenom());
            utilisateur.setEmail(details.getEmail());
            utilisateur.setAdresse(details.getAdresse());
            
            // Si l'utilisateur a changé de rôle ou de statut actif (généralement géré par admin)
            if (details.getRole() != null) {
                utilisateur.setRole(details.getRole());
            }
            if (details.getCompteActif() != null) {
                utilisateur.setCompteActif(details.getCompteActif());
            }
            
            Utilisateur updated = utilisateurRepository.save(utilisateur);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime ou désactive un collaborateur.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        return utilisateurRepository.findById(id).map(utilisateur -> {
            // Option 1 : Soft delete 
            utilisateur.setCompteActif(false);
            utilisateurRepository.save(utilisateur);
            
            // Option 2 : Hard delete
            // utilisateurRepository.delete(utilisateur);
            
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}