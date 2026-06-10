package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.AuthResponse;
import com.diginamic.wemouv.dto.ChangePasswordRequest;
import com.diginamic.wemouv.dto.LoginRequest;
import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import com.diginamic.wemouv.security.JwtService;
import com.diginamic.wemouv.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST gérant la sécurité, l'authentification et l'inscription.
 * <p>
 * Ce contrôleur est le point d'entrée public (non sécurisé par un token) permettant
 * aux utilisateurs de se connecter (pour obtenir un token JWT) ou de créer un compte.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Gestionnaire d'authentification de Spring Security. */
    private final AuthenticationManager authManager;

    /** Service permettant de charger les détails d'un utilisateur depuis la BDD. */
    private final UserDetailsService userDetailsService;

    /** Service utilitaire pour la génération et validation des tokens JWT. */
    private final JwtService jwtService;

    /** Outil de hachage pour sécuriser les mots de passe. */
    private final PasswordEncoder passwordEncoder;

    /** Dépôt pour vérifier et sauvegarder les utilisateurs lors de l'inscription. */
    private final UtilisateurRepository utilisateurRepository;

    private final UtilisateurService utilisateurService;

    /**
     * Constructeur injectant les services et composants de sécurité nécessaires.
     *
     * @param authManager gestionnaire d'authentification
     * @param userDetailsService service de lecture des utilisateurs
     * @param jwtService service de gestion des tokens
     * @param passwordEncoder encodeur de mots de passe (ex: BCrypt)
     * @param utilisateurRepository dépôt des utilisateurs
     */
    public AuthController(
            AuthenticationManager authManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UtilisateurRepository utilisateurRepository,
            UtilisateurService utilisateurService
    ) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
    }

    /**
     * Authentifie un utilisateur et génère son Token JWT.
     *
     * @param request DTO contenant l'email et le mot de passe en clair
     * @return un {@link ResponseEntity} contenant le Token JWT (HTTP 200),
     * ou un statut HTTP 401 (Unauthorized) si les identifiants sont faux
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {

            Utilisateur completUser = utilisateurService.findByEmail(request.getEmail());


            if (completUser == null || Boolean.FALSE.equals(completUser.getCompteActif())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN) // 403 Forbidden
                        .body("Votre compte n'est pas encore activé. Veuillez vérifier vos e-mails.");
            }


            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );


            UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect");
        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou mot de passe incorrect");
        }
    }

    /**
     * Inscrit un nouvel utilisateur dans le système de l'entreprise.
     *
     * @param request DTO contenant les informations du profil à créer
     * @return un {@link ResponseEntity} confirmant la création (HTTP 201),
     * ou un statut HTTP 400 (Bad Request) si l'email existe déjà ou si le rôle est invalide
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // Vérification de l'unicité de l'email
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email déjà utilisé");
        }

        Utilisateur user = new Utilisateur();

        user.setCompteActif(request.getCompteActif());
        user.setAdresse(request.getAdresse());
        user.setEmail(request.getEmail());
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());

        // Hachage du mot de passe avant la sauvegarde en base
        user.setMotDePasse(
                passwordEncoder.encode(request.getPassword())
        );

        // Assignation sécurisée du rôle (Administrateur, Employé...)
        try {
            user.setRole(
                    Role.valueOf(request.getRole().toUpperCase())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Rôle invalide");
        }

        utilisateurRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Utilisateur créé avec succès");
    }



    @PostMapping("/definir-mot-de-passe")
    public ResponseEntity<?> definirMotDePasse(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            String email = request.email();
            String token = request.token();
            String password = request.nouveauMotDePasse();

            Utilisateur user = utilisateurService.findByEmail(email);
            user.setMotDePasse(passwordEncoder.encode(password));
            user.setCompteActif(true);

            utilisateurService.update(user.getId(), user);

            return ResponseEntity.ok().body("Mot de passe configuré avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}