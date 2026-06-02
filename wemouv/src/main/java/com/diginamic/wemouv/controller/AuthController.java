package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.AuthResponse;
import com.diginamic.wemouv.dto.LoginRequest;
import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import com.diginamic.wemouv.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurRepository utilisateurRepository;


    public AuthController(
            AuthenticationManager authManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UtilisateurRepository utilisateurRepository
    ) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails user =
                userDetailsService.loadUserByUsername(
                        request.getEmail()
                );

        String token =
                jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        if (utilisateurRepository
                .findByEmail(request.getEmail())
                .isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email déjà utilisé");
        }

        Utilisateur user = new Utilisateur();

        user.setCompteActif(request.getCompteActif());
        user.setAdresse(request.getAdresse());

        user.setEmail(request.getEmail());
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());

        user.setMotDePasse(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        try {

            user.setRole(
                    Role.valueOf(
                            request.getRole().toUpperCase()
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body("Role invalide");
        }

        utilisateurRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Utilisateur créé");
    }
}
