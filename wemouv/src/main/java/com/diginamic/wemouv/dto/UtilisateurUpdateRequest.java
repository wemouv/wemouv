package com.diginamic.wemouv.dto;

/**
 * DTO pour la modification partielle du profil d'un collaborateur.
 * <p>
 * Seuls les champs du profil sont exposés.
 * Le mot de passe et le rôle ne sont pas modifiables via cette route.
 * </p>
 */
public class UtilisateurUpdateRequest {

    /** Nouveau nom de famille (optionnel). */
    private String nom;

    /** Nouveau prénom (optionnel). */
    private String prenom;

    /** Nouvel email professionnel (optionnel). */
    private String email;

    /** Nouvelle adresse postale (optionnel). */
    private String adresse;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}