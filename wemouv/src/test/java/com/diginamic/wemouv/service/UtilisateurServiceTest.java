package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link UtilisateurService}.
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @Test
    void findAll_DoitRetournerListe() {
        Utilisateur u1 = new Utilisateur();
        Utilisateur u2 = new Utilisateur();
        when(utilisateurRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Utilisateur> result = utilisateurService.findAll();

        assertEquals(2, result.size());
        verify(utilisateurRepository).findAll();
    }

    @Test
    void findById_QuandExiste_DoitRetournerUtilisateur() {
        Long id = 1L;
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.findById(id)).thenReturn(Optional.of(u));

        Utilisateur result = utilisateurService.findById(id);

        assertNotNull(result);
        assertEquals(u, result);
    }

    @Test
    void findById_QuandExistePas_DoitLancerException() {
        Long id = 1L;
        when(utilisateurRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> utilisateurService.findById(id));
    }

    @Test
    void findByEmail_QuandExiste_DoitRetournerUtilisateur() {
        String email = "test@test.com";
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(u));

        Utilisateur result = utilisateurService.findByEmail(email);

        assertNotNull(result);
        assertEquals(u, result);
    }

    @Test
    void findByEmail_QuandExistePas_DoitLancerException() {
        String email = "test@test.com";
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> utilisateurService.findByEmail(email));
    }

    @Test
    void create_DoitSauvegarderUtilisateur() {
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.save(u)).thenReturn(u);

        Utilisateur created = utilisateurService.create(u);

        assertNotNull(created);
        verify(utilisateurRepository).save(u);
    }

    @Test
    void update_QuandExiste_DoitMettreAJourEtSauvegarder() {
        Long id = 1L;
        Utilisateur details = new Utilisateur();
        details.setNom("NouveauNom");

        when(utilisateurRepository.existsById(id)).thenReturn(true);
        when(utilisateurRepository.save(details)).thenReturn(details);

        Utilisateur updated = utilisateurService.update(id, details);

        assertEquals(id, updated.getId());
        assertEquals("NouveauNom", updated.getNom());
        verify(utilisateurRepository).save(details);
    }

    @Test
    void update_QuandExistePas_DoitLancerException() {
        Long id = 1L;
        Utilisateur details = new Utilisateur();
        when(utilisateurRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> utilisateurService.update(id, details));
    }

    @Test
    void delete_QuandExiste_DoitSupprimer() {
        Long id = 1L;
        when(utilisateurRepository.existsById(id)).thenReturn(true);

        utilisateurService.delete(id);

        verify(utilisateurRepository).deleteById(id);
    }

    @Test
    void delete_QuandExistePas_DoitLancerException() {
        Long id = 1L;
        when(utilisateurRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> utilisateurService.delete(id));
    }



    /**
     * Vérifie que la méthode {@code softDelete} change correctement
     * l'état du compte à {@code false} et déclenche la sauvegarde.
     */
    @Test
    void softDelete_DoitPasserCompteActifAFalse() {
        Long id = 1L;
        Utilisateur u = new Utilisateur();
        u.setCompteActif(true);
        when(utilisateurRepository.findById(id)).thenReturn(Optional.of(u));

        utilisateurService.softDelete(id);

        assertFalse(u.getCompteActif(), "Le compte devrait être désactivé");
        verify(utilisateurRepository).save(u);
    }

    /**
     * Vérifie que la méthode {@code reactivate} restaure l'état
     * du compte à {@code true} et déclenche la sauvegarde.
     */
    @Test
    void reactivate_DoitPasserCompteActifATrue() {
        Long id = 1L;
        Utilisateur u = new Utilisateur();
        u.setCompteActif(false);
        when(utilisateurRepository.findById(id)).thenReturn(Optional.of(u));

        utilisateurService.reactivate(id);

        assertTrue(u.getCompteActif(), "Le compte devrait être réactivé");
        verify(utilisateurRepository).save(u);
    }
}