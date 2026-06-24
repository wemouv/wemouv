package com.diginamic.wemouv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Objet de Transfert de Données (DTO) pour la requête de connexion.
 * <p>
 * Ce DTO est utilisé par le contrôleur d'authentification pour réceptionner
 * les identifiants envoyés par le Front-end lors d'une tentative de connexion (Login).
 * </p>
 */
public class LoginRequest {

    /**
     * L'adresse e-mail de l'utilisateur, servant d'identifiant principal de connexion.
     */
    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "L'adresse e-mail n'est pas valide.")
    private String email;

    /**
     * Le mot de passe en clair saisi par l'utilisateur sur le formulaire de connexion.
     */
    @NotBlank(message = "Le mot de passe est obligatoire.")
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