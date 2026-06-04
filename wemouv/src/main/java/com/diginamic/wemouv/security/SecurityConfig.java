package com.diginamic.wemouv.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration globale de Spring Security pour l'application WeMouv.
 * <p>
 * Cette classe définit les règles de sécurité du backend :
 * <ul>
 * <li>Désactivation des sessions côté serveur (API Stateless).</li>
 * <li>Mise en place des permissions sur les différentes routes (URL) selon les rôles.</li>
 * <li>Insertion du filtre JWT personnalisé pour intercepter et valider les requêtes.</li>
 * <li>Configuration de l'encodeur de mots de passe.</li>
 * </ul>
 * </p>
 */
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Injection du filtre JWT personnalisé.
     */
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configure la chaîne de filtres de sécurité (Security Filter Chain).
     * C'est ici que l'on définit "qui a le droit d'accéder à quoi".
     *
     * @param http l'objet de configuration de la sécurité HTTP
     * @return la chaîne de filtres construite
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                // 1. Désactivation du CSRF (Cross-Site Request Forgery).
                // Inutile pour une API REST Stateless utilisant des tokens JWT.
                .csrf(csrf -> csrf.disable())

                // 2. Gestion des sessions en mode STATELESS.
                // Spring Security ne créera pas de session en mémoire, chaque requête
                // devra être authentifiée par son token JWT.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Configuration des autorisations par URL et par Rôle
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques d'authentification (login, register)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/covoiturages/**").permitAll()


                        // Gestion des utilisateurs
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMIN")

                        // Gestion de la flotte de service (réservé aux administrateurs)
                        .requestMatchers(HttpMethod.DELETE, "/api/vehicules/service/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/vehicules/service/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/vehicules/service/**").hasRole("ADMIN")


                        // Toutes les autres requêtes nécessitent au minimum d'être connecté
                        .anyRequest().authenticated()

                )

                // 4. Configuration optionnelle pour supporter l'authentification basique
                .httpBasic(Customizer.withDefaults())

                // 5. Ajout de notre filtre JWT AVANT le filtre standard de Spring Security.
                // Cela permet de vérifier le token avant même que Spring n'essaie de chercher
                // une session ou un mot de passe.
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Définit l'algorithme de hachage des mots de passe.
     * BCrypt est le standard actuel de l'industrie : il intègre un "sel" (salt)
     * pour contrer les attaques par dictionnaire.
     *
     * @return une instance de BCryptPasswordEncoder
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expose le gestionnaire d'authentification (AuthenticationManager) de Spring.
     * Il sera utilisé par le contrôleur d'authentification (AuthController) pour
     * valider le couple email/mot de passe lors du login.
     *
     * @param config la configuration d'authentification de Spring
     * @return le gestionnaire d'authentification
     * @throws Exception en cas d'erreur de récupération
     */
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}