package com.diginamic.wemouv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Pour réceptionner les données envoyées par Angular
 * lors de la configuration initiale du mot de passe.
 */
public record ChangePasswordRequest(

        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "Le format de l'adresse email est invalide")
        String email,

        @NotBlank(message = "Le jeton de sécurité est manquant")
        String token,

        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String nouveauMotDePasse
) {}