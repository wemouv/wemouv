package com.diginamic.wemouv.dto;



public class RegisterRequest {

    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private String adresse;
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

    public void setCompteActif(Boolean compte_actif) {
        this.compteActif = compte_actif;
    }
}
