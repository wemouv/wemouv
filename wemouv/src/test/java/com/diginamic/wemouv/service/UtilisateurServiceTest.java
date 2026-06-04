package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link UtilisateurService}.
 * <p>
 * Ces tests vérifient la logique métier liée à la gestion des utilisateurs,
 * notamment les changements d'état (activation/désactivation) et la recherche.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

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