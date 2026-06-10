package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.dto.ReservationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.service.ListeReservationVehicule;
import com.diginamic.wemouv.service.ReservationService;
import com.diginamic.wemouv.service.SupprimerReservationVehicule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link ReservationController}.
 * <p>
 * Valide les codes HTTP et la délégation aux services sans démarrer Spring ni MySQL.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ReservationControllerTests {

    @Mock private ReservationService reservationService;
    @Mock private ListeReservationVehicule listeReservationVehicule;
    @Mock private SupprimerReservationVehicule supprimerReservationVehicule;
    @Mock private Authentication authentication;

    @InjectMocks private ReservationController controller;

    @Test
    void getAllReservations_DoitRetournerListe() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(listeReservationVehicule.lister()).thenReturn(List.of(reservation));

        List<Reservation> result = controller.getAllReservations();

        assertEquals(1, result.size());
        verify(listeReservationVehicule).lister();
    }

    @Test
    void getReservationById_QuandExiste_DoitRetourner200() {
        Reservation reservation = new Reservation();
        reservation.setId(5L);
        when(reservationService.findById(5L)).thenReturn(reservation);

        ResponseEntity<?> response = controller.getReservationById(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(reservation, response.getBody());
    }

    @Test
    void getReservationById_QuandIntrouvable_DoitRetourner404() {
        when(reservationService.findById(99L))
                .thenThrow(new RuntimeException("Réservation introuvable"));

        ResponseEntity<?> response = controller.getReservationById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Réservation introuvable", response.getBody());
    }

    @Test
    void getReservationsByUtilisateur_DoitDeleguerAuService() {
        when(reservationService.findByUtilisateur(3L)).thenReturn(List.of(new Reservation()));

        List<Reservation> result = controller.getReservationsByUtilisateur(3L);

        assertEquals(1, result.size());
        verify(reservationService).findByUtilisateur(3L);
    }

    @Test
    void getReservationsByVehicule_DoitDeleguerAuServiceListe() {
        when(listeReservationVehicule.listerParVehicule(7L)).thenReturn(List.of(new Reservation()));

        List<Reservation> result = controller.getReservationsByVehicule(7L);

        assertEquals(1, result.size());
        verify(listeReservationVehicule).listerParVehicule(7L);
    }

    @Test
    void createReservation_QuandValide_DoitRetourner201() {
        ReservationRequest request = new ReservationRequest();
        Reservation created = new Reservation();
        created.setId(10L);

        when(authentication.getName()).thenReturn("user@test.com");
        when(reservationService.create(request, "user@test.com")).thenReturn(created);

        ResponseEntity<Reservation> response = controller.createReservation(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(created, response.getBody());
        verify(reservationService).create(request, "user@test.com");
    }

    @Test
    void updateReservation_QuandExiste_DoitRetourner200() {
        ReservationModificationRequest details = new ReservationModificationRequest();
        Reservation updated = new Reservation();
        updated.setId(2L);
        when(reservationService.update(2L, details)).thenReturn(updated);

        ResponseEntity<?> response = controller.updateReservation(2L, details);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
    }

    @Test
    void updateReservation_QuandRegleMetierViolée_DoitRetourner400() {
        ReservationModificationRequest details = new ReservationModificationRequest();
        when(reservationService.update(2L, details))
                .thenThrow(new IllegalStateException("Chevauchement de dates"));

        ResponseEntity<?> response = controller.updateReservation(2L, details);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Chevauchement de dates", response.getBody());
    }

    @Test
    void updateReservation_QuandIntrouvable_DoitRetourner404() {
        ReservationModificationRequest details = new ReservationModificationRequest();
        when(reservationService.update(2L, details))
                .thenThrow(new RuntimeException("Introuvable"));

        ResponseEntity<?> response = controller.updateReservation(2L, details);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Introuvable", response.getBody());
    }

    @Test
    void deleteReservation_QuandExiste_DoitRetourner204() {
        ResponseEntity<?> response = controller.deleteReservation(4L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(supprimerReservationVehicule).supprimer(4L);
    }

    @Test
    void deleteReservation_QuandIntrouvable_DoitRetourner404() {
        doThrow(new RuntimeException("Réservation introuvable"))
                .when(supprimerReservationVehicule).supprimer(4L);

        ResponseEntity<?> response = controller.deleteReservation(4L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Réservation introuvable", response.getBody());
    }
}
