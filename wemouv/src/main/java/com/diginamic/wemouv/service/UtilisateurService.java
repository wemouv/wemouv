package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.dto.UtilisateurUpdateRequest;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service métier gérant les profils des collaborateurs (utilisateurs).
 * <p>
 * Cette classe assure la consultation, la création, la modification,
 * la désactivation (soft delete) et la réactivation des comptes utilisateurs
 * au sein de l'application.
 * </p>
 */
@Service
public class UtilisateurService {

    /** Dépôt d'accès aux données des utilisateurs. */
    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur avec injection des dépendances requises.
     *
     * @param utilisateurRepository le dépôt pour interagir avec la table utilisateur
     * @param emailService le service d'envoi d'emails
     * @param passwordEncoder l'encodeur de mot de passe
     */
    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              EmailService emailService,
                              PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupère la liste de tous les collaborateurs inscrits.
     *
     * @return la liste globale des utilisateurs
     */
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    /**
     * Recherche un collaborateur par son identifiant unique.
     *
     * @param id l'identifiant recherché
     * @return l'utilisateur correspondant
     * @throws RuntimeException si l'utilisateur n'existe pas en base
     */
    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    /**
     * Recherche un collaborateur via son adresse e-mail.
     *
     * @param email l'e-mail recherché
     * @return l'utilisateur correspondant
     * @throws RuntimeException si aucun utilisateur ne possède cet e-mail
     */
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    /**
     * Enregistre un nouvel utilisateur en base de données.
     * <p>
     * Génère un token temporaire, envoie un email d'activation
     * et crée le compte avec un statut inactif.
     * </p>
     *
     * @param request le DTO contenant les informations du nouveau compte
     * @return l'utilisateur sauvegardé avec son ID généré
     */

    public Utilisateur create(RegisterRequest request) {
        String tokenTemporaire = UUID.randomUUID().toString();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setAdresse(request.getAdresse());
        if (request.getRole() != null) {
            utilisateur.setRole(Role.valueOf(request.getRole().toUpperCase()));
        }
        utilisateur.setMotDePasse(passwordEncoder.encode(tokenTemporaire));
        utilisateur.setCompteActif(false);

        Utilisateur nouvelUtilisateur = utilisateurRepository.save(utilisateur);

        String lienConfiguration = "http://localhost:4200/initialiser-mot-de-passe?token="
                + tokenTemporaire + "&email=" + nouvelUtilisateur.getEmail();

        String sujet = "WeMouv : Activation de votre compte collaborateur";
        String corps = "Bonjour " + nouvelUtilisateur.getPrenom() + ",\n\n"
                + "Un compte a été créé pour vous sur l'application WeMouv.\n"
                + "Veuillez cliquer sur le lien ci-dessous pour configurer votre mot de passe et activer votre compte :\n\n"
                + lienConfiguration + "\n\n"
                + "À bientôt,\nL'équipe WeMouv.";

        emailService.sendMail(nouvelUtilisateur.getEmail(), sujet, corps);

        return nouvelUtilisateur;
    }

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param id l'identifiant du collaborateur à modifier
     * @param details les nouvelles données du profil
     * @return l'utilisateur mis à jour
     * @throws RuntimeException si l'utilisateur est introuvable
     */
    @Transactional
    public Utilisateur update(Long id, UtilisateurUpdateRequest details) {
        Utilisateur existing = findById(id);
        if (details.getNom() != null) existing.setNom(details.getNom());
        if (details.getPrenom() != null) existing.setPrenom(details.getPrenom());
        if (details.getEmail() != null) existing.setEmail(details.getEmail());
        if (details.getAdresse() != null) existing.setAdresse(details.getAdresse());
        return utilisateurRepository.save(existing);
    }

    /**
     * Supprime définitivement un utilisateur de la base de données (Hard Delete).
     *
     * @param id l'identifiant de l'utilisateur à détruire
     * @throws RuntimeException si l'utilisateur est introuvable
     */
    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable pour suppression");
        }
        utilisateurRepository.deleteById(id);
    }

    /**
     * Désactive le compte d'un utilisateur sans le supprimer de la base (Soft Delete).
     * <p>Passe la propriété {@code compteActif} à {@code false}.</p>
     *
     * @param id l'identifiant du collaborateur à désactiver
     * @throws RuntimeException si l'utilisateur est introuvable
     */
    public void softDelete(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour désactivation"));
        u.setCompteActif(false);
        utilisateurRepository.save(u);
    }

    /**
     * Réactive le compte d'un utilisateur précédemment désactivé.
     * <p>Passe la propriété {@code compteActif} à {@code true}.</p>
     *
     * @param id l'identifiant du collaborateur à réactiver
     * @throws RuntimeException si l'utilisateur est introuvable
     */
    public void reactivate(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour réactivation"));
        u.setCompteActif(true);
        utilisateurRepository.save(u);
    }

    /**
     * Recherche des utilisateurs par terme (nom, prénom ou email).
     *
     * @param terme le terme de recherche
     * @return la liste des utilisateurs correspondants
     */
    public List<Utilisateur> search(String terme) {
        if (terme == null || terme.isBlank()) {
            return findAll();
        }
        String termeMinuscule = terme.toLowerCase();
        return findAll().stream()
                .filter(u -> {
                    boolean matchEmail = u.getEmail() != null && u.getEmail().toLowerCase().contains(termeMinuscule);
                    boolean matchNom = u.getNom() != null && u.getNom().toLowerCase().contains(termeMinuscule);
                    boolean matchPrenom = u.getPrenom() != null && u.getPrenom().toLowerCase().contains(termeMinuscule);
                    return matchEmail || matchNom || matchPrenom;
                })
                .toList();
    }
}