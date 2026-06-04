package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.service.VehiculeDeServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link VehiculeDeServiceController}.
 * <p>
 * Valide les codes HTTP et la délégation au service sans démarrer Spring ni MySQL.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class VehiculeDeServiceControllerTests {

    @Mock private VehiculeDeServiceService vehiculeDeServiceService;

    @InjectMocks private VehiculeDeServiceController controller;

    @Test
    void getAllVehiculesDeService_DoitRetourner200AvecListe() {
        VehiculeDeService vehicule = new VehiculeDeService();
        vehicule.setId(1L);
        when(vehiculeDeServiceService.getVehiculesFlotte("AB-123", "Renault"))
                .thenReturn(List.of(vehicule));

        ResponseEntity<List<VehiculeDeService>> response =
                controller.getAllVehiculesDeService("AB-123", "Renault");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(vehiculeDeServiceService).getVehiculesFlotte("AB-123", "Renault");
    }

    @Test
    void getAllVehiculesDeService_SansFiltre_DoitDeleguerAvecNull() {
        when(vehiculeDeServiceService.getVehiculesFlotte(null, null))
                .thenReturn(List.of());

        ResponseEntity<List<VehiculeDeService>> response =
                controller.getAllVehiculesDeService(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllVehiculesDeServiceAvailable_QuandDisponibles_DoitRetourner200() {
        LocalDateTime debut = LocalDateTime.of(2026, 6, 10, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 10, 18, 0);
        when(vehiculeDeServiceService.findAllAvailable(debut, fin))
                .thenReturn(List.of(new VehiculeDeService()));

        ResponseEntity<?> response = controller.getAllVehiculesDeServiceAvailable(debut, fin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void getAllVehiculesDeServiceAvailable_QuandErreur_DoitRetourner404() {
        LocalDateTime debut = LocalDateTime.of(2026, 6, 10, 9, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 10, 8, 0);
        when(vehiculeDeServiceService.findAllAvailable(debut, fin))
                .thenThrow(new RuntimeException("Période invalide"));

        ResponseEntity<?> response = controller.getAllVehiculesDeServiceAvailable(debut, fin);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Période invalide", response.getBody());
    }

    @Test
    void getVehiculeDeServiceById_QuandExiste_DoitRetourner200() {
        VehiculeDeService vehicule = new VehiculeDeService();
        vehicule.setId(5L);
        when(vehiculeDeServiceService.findById(5L)).thenReturn(vehicule);

        ResponseEntity<?> response = controller.getVehiculeDeServiceById(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(vehicule, response.getBody());
    }

    @Test
    void getVehiculeDeServiceById_QuandIntrouvable_DoitRetourner404() {
        when(vehiculeDeServiceService.findById(99L))
                .thenThrow(new RuntimeException("Véhicule introuvable"));

        ResponseEntity<?> response = controller.getVehiculeDeServiceById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Véhicule introuvable", response.getBody());
    }

    @Test
    void createVehiculeDeService_QuandValide_DoitRetourner201() {
        VehiculeDeService input = new VehiculeDeService();
        VehiculeDeService created = new VehiculeDeService();
        created.setId(10L);
        when(vehiculeDeServiceService.create(input)).thenReturn(created);

        ResponseEntity<VehiculeDeService> response = controller.createVehiculeDeService(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(created, response.getBody());
    }

    @Test
    void updateVehiculeDeService_QuandExiste_DoitRetourner200() {
        VehiculeDeService details = new VehiculeDeService();
        VehiculeDeService updated = new VehiculeDeService();
        updated.setId(3L);
        when(vehiculeDeServiceService.update(3L, details)).thenReturn(updated);

        ResponseEntity<?> response = controller.updateVehiculeDeService(3L, details);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
    }

    @Test
    void updateVehiculeDeService_QuandIntrouvable_DoitRetourner404() {
        VehiculeDeService details = new VehiculeDeService();
        when(vehiculeDeServiceService.update(3L, details))
                .thenThrow(new RuntimeException("Introuvable"));

        ResponseEntity<?> response = controller.updateVehiculeDeService(3L, details);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Introuvable", response.getBody());
    }

    @Test
    void deleteVehiculeDeService_QuandExiste_DoitRetourner204() {
        ResponseEntity<?> response = controller.deleteVehiculeDeService(7L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehiculeDeServiceService).delete(7L);
    }

    @Test
    void deleteVehiculeDeService_QuandIntrouvable_DoitRetourner404() {
        doThrow(new RuntimeException("Suppression impossible"))
                .when(vehiculeDeServiceService).delete(7L);

        ResponseEntity<?> response = controller.deleteVehiculeDeService(7L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Suppression impossible", response.getBody());
    }
}
