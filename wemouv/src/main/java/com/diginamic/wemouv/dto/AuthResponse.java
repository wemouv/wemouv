package com.diginamic.wemouv.dto;

/**
 * Objet de Transfert de Données (DTO) représentant la réponse d'authentification.
 * <p>
 * Ce DTO est renvoyé au Front-end (client) une fois la connexion réussie.
 * Il contient le jeton de sécurité (Token JWT) permettant au client de s'authentifier
 * automatiquement lors de ses futures requêtes vers l'API.
 * </p>
 */
public class AuthResponse {

    /** Le jeton de sécurité d'authentification (généralement au format JWT). */
    private String token;

    /**
     * Constructeur initialisant la réponse avec le jeton généré.
     *
     * @param token le jeton de sécurité à transmettre au client
     */
    public AuthResponse(String token) {
        this.token = token;
    }

    /**
     * Récupère le jeton d'authentification.
     *
     * @return le jeton sous forme de chaîne de caractères
     */
    public String getToken() {
        return token;
    }
}