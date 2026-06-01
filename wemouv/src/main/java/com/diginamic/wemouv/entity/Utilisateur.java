package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Role;
import jakarta.persistence.*;

/**
 * Représente un utilisateur de la plateforme WeMouv.
 * <p>
 * Cette entité contient les informations personnelles et d'authentification
 * nécessaires pour identifier un utilisateur, gérer ses droits et déterminer
 * s'il peut accéder aux fonctionnalités de l'application.
 * </p>
 *
 * <p>
 * Un utilisateur possède :
 * <ul>
 *     <li>des informations d'identité (nom, prénom)</li>
 *     <li>des informations de connexion (email, mot de passe)</li>
 *     <li>un rôle déterminant ses permissions ({@link Role})</li>
 *     <li>un statut indiquant si son compte est actif</li>
 * </ul>
 * </p>
 *
 * <p>
 * L'email est unique et sert d'identifiant de connexion.
 * </p>
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    /** Identifiant unique de l'utilisateur. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de famille de l'utilisateur. */
    @Column(nullable = false, length = 100)
    private String nom;

    /** Prénom de l'utilisateur. */
    @Column(nullable = false, length = 100)
    private String prenom;

    /** Adresse email unique servant d'identifiant de connexion. */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Mot de passe chiffré de l'utilisateur. */
    @Column(nullable = false, length = 255)
    private String motDePasse;

    /** Adresse postale de l'utilisateur (optionnelle). */
    private String adresse;

    /** Rôle attribué à l'utilisateur (ex : ADMIN, UTILISATEUR). */
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Indique si le compte est actif ou désactivé. */
    private Boolean compteActif;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return l'identifiant de l'utilisateur */
    public Long getId() { return id; }

    /** @param id identifiant de l'utilisateur */
    public void setId(Long id) { this.id = id; }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getCompteActif() {
        return compteActif;
    }

    public void setCompteActif(Boolean compteActif) {
        this.compteActif = compteActif;
    }
}
