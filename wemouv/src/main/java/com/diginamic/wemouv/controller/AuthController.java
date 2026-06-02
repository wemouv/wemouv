package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UtilisateurRepository utilisateurRepository,
                          PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Page d'inscription
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "auth/register"; // templates/auth/register.html
    }

    // Traitement du formulaire d'inscription
    @PostMapping("/register")
    public String register(@ModelAttribute Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateurRepository.save(utilisateur);
        return "redirect:/login?registered";
    }
}
