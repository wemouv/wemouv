package com.diginamic.wemouv.dto;

/**
 * Objet de Transfert de Données (DTO) pour la requête de connexion.
 * <p>
 * Ce DTO est utilisé par le contrôleur d'authentification pour réceptionner
 * les identifiants envoyés par le Front-end lors d'une tentative de connexion (Login).
 * </p>
 */
public class LoginRequest {

    /** L'adresse e-mail de l'utilisateur, servant d'identifiant principal de connexion. */
    private String email;

    /** Le mot de passe en clair saisi par l'utilisateur sur le formulaire de connexion. */
    private String password;

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
}