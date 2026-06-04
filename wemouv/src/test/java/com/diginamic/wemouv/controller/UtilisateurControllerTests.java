package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.service.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link UtilisateurController}.
 * <p>
 * Valide les codes HTTP et la délégation au service sans démarrer Spring ni MySQL.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurControllerTests {

    @Mock private UtilisateurService utilisateurService;

    @InjectMocks private UtilisateurController controller;

    @Test
    void getAllUtilisateurs_DoitRetournerListe() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        when(utilisateurService.findAll()).thenReturn(List.of(utilisateur));

        List<Utilisateur> result = controller.getAllUtilisateurs();

        assertEquals(1, result.size());
        verify(utilisateurService).findAll();
    }

    @Test
    void getUtilisateurById_QuandExiste_DoitRetourner200() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(5L);
        utilisateur.setEmail("user@test.com");
        when(utilisateurService.findById(5L)).thenReturn(utilisateur);

        ResponseEntity<?> response = controller.getUtilisateurById(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(utilisateur, response.getBody());
    }

    @Test
    void getUtilisateurById_QuandIntrouvable_DoitRetourner404() {
        when(utilisateurService.findById(99L))
                .thenThrow(new RuntimeException("Utilisateur introuvable"));

        ResponseEntity<?> response = controller.getUtilisateurById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utilisateur introuvable", response.getBody());
    }

    @Test
    void updateUtilisateur_QuandExiste_DoitRetourner200() {
        Utilisateur details = new Utilisateur();
        details.setNom("Martin");
        Utilisateur updated = new Utilisateur();
        updated.setId(2L);
        updated.setNom("Martin");
        when(utilisateurService.update(2L, details)).thenReturn(updated);

        ResponseEntity<?> response = controller.updateUtilisateur(2L, details);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(utilisateurService).update(2L, details);
    }

    @Test
    void updateUtilisateur_QuandIntrouvable_DoitRetourner404() {
        Utilisateur details = new Utilisateur();
        when(utilisateurService.update(2L, details))
                .thenThrow(new RuntimeException("Introuvable"));

        ResponseEntity<?> response = controller.updateUtilisateur(2L, details);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Introuvable", response.getBody());
    }

    @Test
    void deleteUtilisateur_QuandExiste_DoitRetourner204() {
        ResponseEntity<?> response = controller.deleteUtilisateur(3L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(utilisateurService).softDelete(3L);
    }

    @Test
    void deleteUtilisateur_QuandIntrouvable_DoitRetourner404() {
        doThrow(new RuntimeException("Utilisateur introuvable"))
                .when(utilisateurService).softDelete(3L);

        ResponseEntity<?> response = controller.deleteUtilisateur(3L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utilisateur introuvable", response.getBody());
    }

    @Test
    void reactivateUtilisateur_QuandValide_DoitRetourner204() {
        ResponseEntity<Void> response = controller.reactivateUtilisateur(4L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(utilisateurService).reactivate(4L);
    }
}
