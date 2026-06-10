package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.AuthResponse;
import com.diginamic.wemouv.dto.LoginRequest;
import com.diginamic.wemouv.dto.RegisterRequest;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import com.diginamic.wemouv.security.JwtService;
import com.diginamic.wemouv.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link AuthController}.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock private AuthenticationManager authManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UtilisateurRepository utilisateurRepository;

    // 💡 LA CLÉ EST ICI : Il manquait le mock du service !
    @Mock private UtilisateurService utilisateurService;

    @InjectMocks private AuthController authController;

    /**
     * Vérifie qu'un login valide renvoie 200 avec un token JWT.
     */
    @Test
    void login_QuandIdentifiantsValides_DoitRetournerToken() {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("secret");

        // 1. Simulation du UtilisateurService (et non plus du Repository)
        Utilisateur mockUser = new Utilisateur();
        mockUser.setEmail("user@test.com");
        mockUser.setCompteActif(true);
        lenient().when(utilisateurService.findByEmail("user@test.com")).thenReturn(mockUser);

        // 2. Simulation de l'Authentification
        Authentication auth = mock(Authentication.class);
        lenient().when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        // 3. Simulation des détails Spring Security
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("user@test.com")
                .password("encoded")
                .roles("USER")
                .build();
        lenient().when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

        // 4. Génération du Token
        lenient().when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-test");

        // ACT
        ResponseEntity<?> response = authController.login(request);

        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le test devrait passer au vert !");
        assertInstanceOf(AuthResponse.class, response.getBody());
        assertEquals("jwt-token-test", ((AuthResponse) response.getBody()).getToken());
    }

    /**
     * Vérifie qu'un échec d'authentification renvoie 401 avec un message explicite.
     */
    @Test
    void login_QuandIdentifiantsInvalides_DoitRetourner401() {
        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("mauvais");

        // L'utilisateur doit exister pour passer le premier contrôle du contrôleur
        Utilisateur mockUser = new Utilisateur();
        mockUser.setEmail("user@test.com");
        mockUser.setCompteActif(true);
        lenient().when(utilisateurService.findByEmail("user@test.com")).thenReturn(mockUser);

        lenient().doThrow(new BadCredentialsException("Bad credentials"))
                .when(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // ACT
        ResponseEntity<?> response = authController.login(request);

        // ASSERT
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Email ou mot de passe incorrect", response.getBody());
    }

    /**
     * Vérifie qu'une inscription valide renvoie 201 et persiste l'utilisateur.
     */
    @Test
    void register_QuandDonneesValides_DoitCreerUtilisateur() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Alice");
        request.setEmail("alice@test.com");
        request.setPassword("password123");
        request.setRole("USER");
        request.setAdresse("Paris");
        request.setCompteActif(true);

        lenient().when(utilisateurRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        lenient().when(passwordEncoder.encode("password123")).thenReturn("hash-securise");

        // ACT
        ResponseEntity<?> response = authController.register(request);

        // ASSERT
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Utilisateur créé avec succès", response.getBody());

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(captor.capture());
        Utilisateur saved = captor.getValue();
        assertEquals("alice@test.com", saved.getEmail());
        assertEquals(Role.USER, saved.getRole());
        assertEquals("hash-securise", saved.getMotDePasse());
    }

    /**
     * Vérifie qu'un email déjà utilisé renvoie 400 sans sauvegarde.
     */
    @Test
    void register_QuandEmailDejaUtilise_DoitRetourner400() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existant@test.com");

        lenient().when(utilisateurRepository.findByEmail("existant@test.com"))
                .thenReturn(Optional.of(new Utilisateur()));

        // ACT
        ResponseEntity<?> response = authController.register(request);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email déjà utilisé", response.getBody());
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    /**
     * Vérifie qu'un rôle inconnu renvoie 400 sans sauvegarde.
     */
    @Test
    void register_QuandRoleInvalide_DoitRetourner400() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nouveau@test.com");
        request.setPassword("pwd");
        request.setRole("SUPER_ADMIN_INEXISTANT");

        lenient().when(utilisateurRepository.findByEmail("nouveau@test.com")).thenReturn(Optional.empty());
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("hash");

        // ACT
        ResponseEntity<?> response = authController.register(request);

        // ASSERT
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rôle invalide", response.getBody());
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }
}