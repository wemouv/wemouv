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
 * Implémentation concrète du service de gestion des utilisateurs.
 * <p>
 * Cette classe centralise la logique métier relative aux collaborateurs (utilisateurs) :
 * création de compte avec notification, mise à jour des informations, gestion des statuts
 * d'activation (soft delete) et algorithmes de recherche.
 * </p>
 */
@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur principal permettant l'injection des dépendances par Spring.
     *
     * @param utilisateurRepository le composant d'accès aux données des utilisateurs
     * @param emailService          le service dédié à l'envoi des courriels
     * @param passwordEncoder       l'utilitaire de hachage des mots de passe (Spring Security)
     */
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  EmailService emailService,
                                  PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupère l'exhaustivité des collaborateurs présents en base de données.
     *
     * @return une liste contenant tous les entités {@link Utilisateur}
     */
    @Override
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    /**
     * Recherche un utilisateur spécifique grâce à son identifiant technique (clé primaire).
     *
     * @param id l'identifiant unique de l'utilisateur recherché
     * @return l'entité {@link Utilisateur} correspondante
     * @throws RuntimeException si aucun utilisateur ne correspond à cet identifiant
     */
    @Override
    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    /**
     * Recherche un utilisateur via son adresse e-mail (identifiant unique de connexion).
     *
     * @param email l'adresse e-mail exacte de l'utilisateur
     * @return l'entité {@link Utilisateur} correspondante
     * @throws RuntimeException si l'adresse e-mail n'est attribuée à aucun compte
     */
    @Override
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    /**
     * Crée un nouveau profil utilisateur, génère un jeton d'activation sécurisé
     * et notifie l'utilisateur par e-mail pour l'initialisation de son mot de passe.
     * <p>
     * Par défaut, le compte nouvellement créé possède le statut inactif.
     * </p>
     *
     * @param request l'objet de transfert (DTO) contenant les données saisies lors de l'inscription
     * @return l'entité {@link Utilisateur} persistée en base, incluant son nouvel identifiant généré
     */
    @Override
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
     * Met à jour partiellement ou totalement les informations de profil d'un utilisateur existant.
     * Seuls les champs non nuls fournis dans la requête seront modifiés.
     *
     * @param id      l'identifiant technique de l'utilisateur à modifier
     * @param details le DTO contenant les nouvelles valeurs à appliquer
     * @return l'entité {@link Utilisateur} après sa mise à jour
     * @throws RuntimeException si l'utilisateur à mettre à jour est introuvable
     */
    @Override
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
     * Supprime physiquement (Hard Delete) un utilisateur de la base de données.
     * <p>Attention : Cette action est irréversible et peut impacter l'intégrité référentielle.</p>
     *
     * @param id l'identifiant technique de l'utilisateur à supprimer
     * @throws RuntimeException si l'utilisateur ciblé n'existe pas
     */
    @Override
    public void delete(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable pour suppression");
        }
        utilisateurRepository.deleteById(id);
    }

    /**
     * Désactive un compte utilisateur (Soft Delete) sans altérer les données historiques.
     * <p>L'accès à l'application lui sera refusé (passage de compteActif à false).</p>
     *
     * @param id l'identifiant technique de l'utilisateur à suspendre
     * @throws RuntimeException si l'utilisateur ciblé est introuvable
     */
    @Override
    public void softDelete(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour désactivation"));
        u.setCompteActif(false);
        utilisateurRepository.save(u);
    }

    /**
     * Restaure les droits d'accès d'un compte préalablement désactivé.
     * <p>Permet à l'utilisateur de se connecter à nouveau (passage de compteActif à true).</p>
     *
     * @param id l'identifiant technique de l'utilisateur à réactiver
     * @throws RuntimeException si l'utilisateur ciblé est introuvable
     */
    @Override
    public void reactivate(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour réactivation"));
        u.setCompteActif(true);
        utilisateurRepository.save(u);
    }

    /**
     * Filtre les utilisateurs en fonction d'un terme de recherche textuel.
     * Le terme est comparé (insensible à la casse) à l'e-mail, au nom et au prénom.
     *
     * @param terme la chaîne de caractères recherchée (si vide ou nulle, retourne tous les utilisateurs)
     * @return une liste des entités {@link Utilisateur} correspondant aux critères
     */
    @Override
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

    /**
     * Méthode utilitaire permettant de persister directement une entité modifiée.
     * Principalement utilisée pour les processus internes ne nécessitant pas de validation via DTO.
     *
     * @param utilisateur l'entité {@link Utilisateur} à sauvegarder
     * @return l'entité après sauvegarde en base de données
     */
    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }
}