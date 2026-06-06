package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entité JPA représentant un utilisateur (collaborateur) de la plateforme WeMouv.
 * <p>
 * Cette entité contient les informations personnelles et d'authentification
 * nécessaires pour identifier un utilisateur, gérer ses droits et déterminer
 * s'il peut accéder aux fonctionnalités de l'application.
 * </p>
 * <p>
 * Un utilisateur possède :
 * <ul>
 * <li>des informations d'identité (nom, prénom, adresse)</li>
 * <li>des informations de connexion sécurisées (email, mot de passe)</li>
 * <li>un rôle déterminant ses permissions via l'énumération {@link Role}</li>
 * <li>un statut indiquant si son compte est actif (gestion du soft delete)</li>
 * </ul>
 * </p>
 * <p>
 * L'adresse e-mail est strictement unique en base de données et sert d'identifiant de connexion.
 * </p>
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    /** Identifiant unique de l'utilisateur (généré automatiquement). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de famille du collaborateur. */
    @Column(nullable = false, length = 100)
    private String nom;

    /** Prénom du collaborateur. */
    @Column(nullable = false, length = 100)
    private String prenom;

    /** Adresse e-mail professionnelle (doit être unique). */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** * Mot de passe haché de l'utilisateur.
     * L'annotation @JsonIgnore garantit qu'il ne sera jamais sérialisé ni renvoyé au client.
     */
    @JsonIgnore
    @Column(nullable = false, updatable = false, length = 255)
    private String motDePasse;

    /** Adresse postale du collaborateur (optionnelle). */
    private String adresse;

    /** Rôle système attribué au collaborateur (ex: ADMIN, UTILISATEUR, CHAUFFEUR). */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    /** Indique si le compte est actif (true) ou suspendu/supprimé logiquement (false). */
    private Boolean compteActif;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return l'identifiant de l'utilisateur */
    public Long getId() { return id; }

    /** @param id identifiant de l'utilisateur */
    public void setId(Long id) { this.id = id; }

    /** @return le nom de famille de l'utilisateur */
    public String getNom() { return nom; }

    /** @param nom le nom de famille de l'utilisateur */
    public void setNom(String nom) { this.nom = nom; }

    /** @return le prénom de l'utilisateur */
    public String getPrenom() { return prenom; }

    /** @param prenom le prénom de l'utilisateur */
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /** @return l'adresse e-mail de l'utilisateur */
    public String getEmail() { return email; }

    /** @param email l'adresse e-mail de l'utilisateur */
    public void setEmail(String email) { this.email = email; }

    /** @return le mot de passe haché */
    public String getMotDePasse() { return motDePasse; }

    /** @param motDePasse le mot de passe haché */
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    /** @return l'adresse postale de l'utilisateur */
    public String getAdresse() { return adresse; }

    /** @param adresse l'adresse postale de l'utilisateur */
    public void setAdresse(String adresse) { this.adresse = adresse; }

    /** @return le rôle système de l'utilisateur */
    public Role getRole() { return role; }

    /** @param role le rôle système de l'utilisateur */
    public void setRole(Role role) { this.role = role; }

    /** @return true si le compte est actif, false sinon */
    public Boolean getCompteActif() { return compteActif; }

    /** @param compteActif le statut d'activation du compte */
    public void setCompteActif(Boolean compteActif) { this.compteActif = compteActif; }
}