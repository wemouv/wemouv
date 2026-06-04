package com.diginamic.wemouv.security;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service faisant le lien entre la base de données de l'application et Spring Security.
 * <p>
 * Ce composant implémente l'interface standard {@link UserDetailsService} de Spring.
 * Son but unique est de chercher un utilisateur en base de données lors de la connexion,
 * et de le transformer en un objet {@link UserDetails} compréhensible par le moteur
 * de sécurité (avec ses identifiants et ses rôles).
 * </p>
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Injection du repository pour accéder à la table des utilisateurs.
     */
    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Charge les détails d'un utilisateur à partir de son identifiant de connexion.
     * Dans cette application, l'identifiant (username) correspond à l'adresse e-mail.
     *
     * @param email l'adresse e-mail saisie lors de la tentative de connexion
     * @return un objet {@link UserDetails} contenant l'email, le mot de passe haché et les autorités (rôles)
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à cet e-mail
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Recherche de l'utilisateur dans notre base de données
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email));

        // 2. Conversion de notre entité Utilisateur en un objet User propre à Spring Security.
        // On ajoute obligatoirement le préfixe "ROLE_" devant le rôle de l'utilisateur,
        // car c'est la convention attendue par les méthodes .hasRole() du SecurityConfig.
        return new User(
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()))
        );
    }
}