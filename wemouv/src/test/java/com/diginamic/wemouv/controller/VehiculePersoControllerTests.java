package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.service.UtilisateurService;
import com.diginamic.wemouv.service.VehiculePersoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link VehiculePersoController}.
 * <p>
 * Valide les codes HTTP et la délégation aux services sans démarrer Spring ni MySQL.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class VehiculePersoControllerTests {

    @Mock private VehiculePersoService vehiculePersoService;
    @Mock private UtilisateurService utilisateurService;
    @Mock private Authentication authentication;

    @InjectMocks private VehiculePersoController controller;

    @Test
    void getAllVehiculesPerso_DoitRetournerListe() {
        VehiculePerso vehicule = new VehiculePerso();
        when(vehiculePersoService.findAll()).thenReturn(List.of(vehicule));

        List<VehiculePerso> result = controller.getAllVehiculesPerso();

        assertEquals(1, result.size());
        verify(vehiculePersoService).findAll();
    }

    @Test
    void getAllVehiculesPersoAvailable_QuandDisponibles_DoitRetourner200() {
        LocalDateTime debut = LocalDateTime.of(2026, 6, 10, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 10, 18, 0);
        when(vehiculePersoService.findAllAvailable(debut, fin))
                .thenReturn(List.of(new VehiculePerso()));

        ResponseEntity<?> response = controller.getAllVehiculesPersoAvailable(debut, fin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void getAllVehiculesPersoAvailable_QuandErreur_DoitRetourner404() {
        LocalDateTime debut = LocalDateTime.of(2026, 6, 10, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 10, 18, 0);
        when(vehiculePersoService.findAllAvailable(debut, fin))
                .thenThrow(new RuntimeException("Période invalide"));

        ResponseEntity<?> response = controller.getAllVehiculesPersoAvailable(debut, fin);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Période invalide", response.getBody());
    }

    @Test
    void getVehiculesByProprietaire_DoitDeleguerAuService() {
        when(vehiculePersoService.findByProprietaire(3L)).thenReturn(List.of(new VehiculePerso()));

        List<VehiculePerso> result = controller.getVehiculesByProprietaire(3L);

        assertEquals(1, result.size());
        verify(vehiculePersoService).findByProprietaire(3L);
    }

    @Test
    void createVehiculePerso_QuandValide_DoitRetourner201EtAssocierProprietaire() {
        VehiculePerso input = new VehiculePerso();
        Utilisateur proprietaire = new Utilisateur();
        proprietaire.setId(7L);
        proprietaire.setEmail("user@test.com");
        VehiculePerso saved = new VehiculePerso();
        saved.setId(10L);

        when(authentication.getName()).thenReturn("user@test.com");
        when(utilisateurService.findByEmail("user@test.com")).thenReturn(proprietaire);
        when(vehiculePersoService.create(input)).thenReturn(saved);

        ResponseEntity<VehiculePerso> response =
                controller.createVehiculePerso(input, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
        assertSame(proprietaire, input.getProprietaire());
        verify(utilisateurService).findByEmail("user@test.com");
        verify(vehiculePersoService).create(input);
    }

    @Test
    void updateVehiculePerso_QuandExiste_DoitRetourner200() {
        VehiculePerso details = new VehiculePerso();
        VehiculePerso updated = new VehiculePerso();
        updated.setId(2L);
        when(vehiculePersoService.update(2L, details)).thenReturn(updated);

        ResponseEntity<?> response = controller.updateVehiculePerso(details, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
    }

    @Test
    void updateVehiculePerso_QuandIntrouvable_DoitRetourner404() {
        VehiculePerso details = new VehiculePerso();
        when(vehiculePersoService.update(2L, details))
                .thenThrow(new RuntimeException("Véhicule introuvable"));

        ResponseEntity<?> response = controller.updateVehiculePerso(details, 2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Véhicule introuvable", response.getBody());
    }

    @Test
    void deleteVehiculePerso_QuandExiste_DoitRetourner204() {
        ResponseEntity<?> response = controller.deleteVehiculePerso(4L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehiculePersoService).delete(4L);
    }

    @Test
    void deleteVehiculePerso_QuandIntrouvable_DoitRetourner404() {
        doThrow(new RuntimeException("Suppression impossible"))
                .when(vehiculePersoService).delete(4L);

        ResponseEntity<?> response = controller.deleteVehiculePerso(4L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Suppression impossible", response.getBody());
    }
}
