package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import com.diginamic.wemouv.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          UtilisateurRepository utilisateurRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get("email"), body.get("motDePasse"))
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(body.get("email")).orElseThrow();
        String token = jwtUtils.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("admin/register")
    public ResponseEntity<?> register(@RequestBody Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(Map.of("message", "Compte créé avec succès"));
    }
}
