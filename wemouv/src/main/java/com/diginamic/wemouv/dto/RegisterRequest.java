package com.diginamic.wemouv.dto;

/**
 * Objet de Transfert de Données (DTO) pour la requête d'inscription.
 * <p>
 * Ce DTO est utilisé par le contrôleur d'authentification pour réceptionner
 * l'ensemble des données envoyées par le Front-end lors de la création
 * d'un nouveau profil collaborateur.
 * </p>
 */
public class RegisterRequest {

    /** Le nom de famille du collaborateur. */
    private String nom;

    /** Le prénom du collaborateur. */
    private String prenom;

    /** L'adresse e-mail professionnelle (qui servira d'identifiant de connexion). */
    private String email;

    /** Le mot de passe en clair saisi lors de l'inscription (sera haché par le Backend). */
    private String password;

    /** Le rôle du collaborateur (ex: "ADMIN", "USER", "CHAUFFEUR") transmis sous forme de texte. */
    private String role;

    /** L'adresse postale personnelle ou professionnelle du collaborateur. */
    private String adresse;

    /** Indique si le compte est immédiatement utilisable après sa création. */
    private Boolean compteActif;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Boolean getCompteActif() {
        return compteActif;
    }

    public void setCompteActif(Boolean compteActif) {
        this.compteActif = compteActif;
    }
}