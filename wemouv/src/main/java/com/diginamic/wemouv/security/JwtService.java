package com.diginamic.wemouv.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service gérant le cycle de vie des jetons d'authentification JWT (JSON Web Token).
 * <p>
 * Ce composant est responsable de :
 * <ul>
 * <li>La génération d'un jeton sécurisé lors de la connexion d'un utilisateur.</li>
 * <li>L'extraction des informations (comme l'e-mail) contenues dans un jeton envoyé par le Front-end.</li>
 * <li>La validation cryptographique du jeton.</li>
 * </ul>
 * </p>
 */
@Service
public class JwtService {

    /** * Clé secrète utilisée pour signer numériquement les jetons.
     * (Note : En production, cette valeur devrait idéalement être stockée dans application.properties).
     */
    private final String SECRET =
            "123456789123456789123456789123456789123456789123456789";

    /** Clé cryptographique générée à partir du secret en utilisant l'algorithme HMAC-SHA. */
    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Génère un nouveau jeton JWT pour un utilisateur qui vient de s'authentifier.
     * <p>Le jeton contient le nom d'utilisateur (e-mail) en tant que "Sujet",
     * la date de création, et une durée de validité fixée à 24 heures.</p>
     *
     * @param user les détails de l'utilisateur (issu de Spring Security)
     * @return le jeton JWT sous forme de chaîne de caractères
     */
    public String generateToken(UserDetails user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                // Définition de l'expiration : Date actuelle + (1000ms * 60s * 60m * 24h) = 24 heures
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60 * 24
                        )
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur (e-mail) enfoui à l'intérieur du jeton.
     * <p>Cette méthode valide implicitement la signature du jeton : si le jeton a été
     * falsifié, une exception sera levée lors du "parse".</p>
     *
     * @param token le jeton JWT transmis par le client
     * @return l'adresse e-mail (sujet) contenue dans le jeton
     */
    public String extractUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Vérifie si un jeton est valide en s'assurant qu'il appartient bien
     * à l'utilisateur ciblé.
     * <p>La validation de la date d'expiration et de la signature est déjà
     * gérée en amont par la méthode extractUsername().</p>
     *
     * @param token le jeton JWT à valider
     * @param user les détails de l'utilisateur chargé depuis la base de données
     * @return {@code true} si le jeton est valide et correspond à l'utilisateur, {@code false} sinon
     */
    public boolean isValid(
            String token,
            UserDetails user
    ) {

        String email = extractUsername(token);

        return email.equals(user.getUsername());
    }
}