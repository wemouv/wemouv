package com.diginamic.wemouv.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre de sécurité interceptant chaque requête HTTP pour valider le jeton JWT.
 * <p>
 * Ce composant hérite de {@link OncePerRequestFilter} pour garantir qu'il ne
 * s'exécutera qu'une seule fois par requête entrante. Son rôle est de :
 * <ol>
 * <li>Vérifier la présence de l'en-tête "Authorization" contenant "Bearer ".</li>
 * <li>Extraire le jeton (token) et récupérer l'adresse e-mail (username) associée.</li>
 * <li>Vérifier la validité du jeton.</li>
 * <li>Informer le contexte de sécurité de Spring (SecurityContext) que l'utilisateur est authentifié.</li>
 * </ol>
 * </p>
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructeur avec injection des dépendances requises.
     *
     * @param jwtService service gérant la génération et la validation des jetons JWT
     * @param userDetailsService service permettant de charger les détails de l'utilisateur depuis la BDD
     */
    public JwtFilter(
            JwtService jwtService,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Méthode principale du filtre exécutée à chaque requête HTTP.
     *
     * @param request la requête HTTP entrante
     * @param response la réponse HTTP sortante
     * @param chain la chaîne des filtres de sécurité suivants
     * @throws ServletException en cas d'erreur liée au servlet
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        System.out.println("URI = " + request.getRequestURI());

        // 1. Récupération de l'en-tête d'autorisation
        String authHeader = request.getHeader("Authorization");

        System.out.println("HEADER = " + authHeader);

        // 2. Si pas d'en-tête ou pas de préfixe "Bearer ", on laisse passer au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Pas de bearer");
            chain.doFilter(request, response);
            return;
        }

        // 3. Extraction du jeton (on coupe les 7 premiers caractères : "Bearer ")
        String token = authHeader.substring(7);

        System.out.println("TOKEN = " + token);

        // 4. Extraction de l'identifiant (e-mail) depuis le jeton
        String email = jwtService.extractUsername(token);

        System.out.println("EMAIL = " + email);

        // 5. Si l'e-mail existe et que l'utilisateur n'est pas déjà authentifié dans le contexte actuel
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Récupération de l'utilisateur en base de données
            UserDetails user =
                    userDetailsService.loadUserByUsername(email);

            System.out.println("USER = " + user.getUsername());

            // 6. Si le jeton est valide (non expiré et correspond bien à l'utilisateur)
            if (jwtService.isValid(token, user)) {

                System.out.println("TOKEN VALIDE");

                // 7. Création du jeton d'authentification interne pour Spring Security
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                // Ajout des détails liés à la requête (adresse IP, session, etc.)
                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // 8. Validation finale : on place l'utilisateur dans le contexte de sécurité
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);

                System.out.println("AUTH OK");
            } else {
                System.out.println("TOKEN INVALIDE");
            }
        }

        // 9. On passe la main au filtre suivant de la chaîne
        chain.doFilter(request, response);
    }
}