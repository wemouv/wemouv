package com.diginamic.wemouv.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // Filtre JWT qui intercepte chaque requête pour valider le token
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactive CSRF (inutile pour une API REST + JWT)
                .csrf(csrf -> csrf.disable())

                // Pas de session côté serveur, chaque requête est authentifiée via le token JWT
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Définition des règles d'accès par route
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()        // login/register accessibles sans token
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // réservé aux admins
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll() // ressources statiques libres
                        .requestMatchers("/login").permitAll() // pages Thymeleaf libres
                        .anyRequest().authenticated()                        // tout le reste nécessite d'être connecté
                )

                // Page de login Thymeleaf
                .formLogin(form -> form
                        .loginPage("/login")                // notre page HTML personnalisée
                        .defaultSuccessUrl("/", true)       // redirige vers l'accueil après connexion
                        .permitAll()
                )

                // Déconnexion
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")         // redirige vers login après déconnexion
                        .permitAll()
                )

                // Ajoute le filtre JWT avant le filtre d'auth classique de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Encodeur de mot de passe BCrypt (hashage sécurisé)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Gestionnaire d'authentification utilisé dans AuthController pour le login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}