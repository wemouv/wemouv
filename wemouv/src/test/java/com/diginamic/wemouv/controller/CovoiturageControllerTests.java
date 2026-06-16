package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.CovoiturageRequest;
import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.service.AnnuleParticiaptionCovoiturage;
import com.diginamic.wemouv.service.CovoiturageService;
import com.diginamic.wemouv.service.RechercheCovoiturage;
import com.diginamic.wemouv.service.ReserverCovoiturage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link CovoiturageController}.
 * <p>
 * Valide les codes HTTP et la délégation aux services sans démarrer Spring ni MySQL.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class CovoiturageControllerTests {

    @Mock private CovoiturageService covoiturageService;
    @Mock private RechercheCovoiturage rechercheCovoiturage;
    @Mock private ReserverCovoiturage reserverCovoiturage;
    @Mock private AnnuleParticiaptionCovoiturage annuleParticiaptionCovoiturage;

    @InjectMocks private CovoiturageController controller;

    @Test
    void getAllCovoiturages_DoitRetourner200AvecListe() {
        Covoiturage covoiturage = new Covoiturage();
        covoiturage.setId(1L);
        when(covoiturageService.findAll()).thenReturn(List.of(covoiturage));

        ResponseEntity<List<Covoiturage>> response = controller.getAllCovoiturages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(covoiturageService).findAll();
    }

    @Test
    void rechercherCovoiturages_DoitDeleguerAuServiceRecherche() {
        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 8, 0);
        when(rechercheCovoiturage.rechercher("Paris", "Lyon", date, Statut.EN_ATTENTE))
                .thenReturn(List.of(new Covoiturage()));

        List<Covoiturage> result = controller.rechercherCovoiturages(
                "Paris", "Lyon", date, Statut.EN_ATTENTE);

        assertEquals(1, result.size());
        verify(rechercheCovoiturage).rechercher("Paris", "Lyon", date, Statut.EN_ATTENTE);
    }

    @Test
    void getCovoiturageById_QuandExiste_DoitRetourner200() {
        Covoiturage covoiturage = new Covoiturage();
        covoiturage.setId(5L);
        when(covoiturageService.findById(5L)).thenReturn(covoiturage);

        ResponseEntity<?> response = controller.getCovoiturageById(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(covoiturage, response.getBody());
    }

    @Test
    void getCovoiturageById_QuandIntrouvable_DoitRetourner404() {
        when(covoiturageService.findById(99L))
                .thenThrow(new RuntimeException("Covoiturage introuvable"));

        ResponseEntity<?> response = controller.getCovoiturageById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Covoiturage introuvable", response.getBody());
    }

    @Test
    void createCovoiturage_QuandValide_DoitRetourner201() {
        CovoiturageRequest request = new CovoiturageRequest();
        Covoiturage created = new Covoiturage();
        created.setId(10L);
        when(covoiturageService.create(request)).thenReturn(created);

        ResponseEntity<?> response = controller.createCovoiturage(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(created, response.getBody());
    }

    @Test
    void createCovoiturage_QuandErreur_DoitRetourner500() {
        CovoiturageRequest request = new CovoiturageRequest();

        when(covoiturageService.create(request)).thenThrow(new RuntimeException("Erreur création"));

        ResponseEntity<?> response = controller.createCovoiturage(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        assertEquals("Une erreur interne est survenue lors de la création.", response.getBody());
    }

    @Test
    void updateCovoiturage_QuandExiste_DoitRetourner200() {
        CovoiturageRequest request = new CovoiturageRequest();
        Covoiturage updated = new Covoiturage();
        updated.setId(3L);
        when(covoiturageService.update(3L, request)).thenReturn(updated);

        ResponseEntity<?> response = controller.updateCovoiturage(3L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
    }

    @Test
    void updateCovoiturage_QuandIntrouvable_DoitRetourner404() {
        CovoiturageRequest request = new CovoiturageRequest();
        when(covoiturageService.update(3L, request))
                .thenThrow(new RuntimeException("Introuvable"));

        ResponseEntity<?> response = controller.updateCovoiturage(3L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Introuvable", response.getBody());
    }

    @Test
    void deleteCovoiturage_QuandExiste_DoitRetourner204() {
        ResponseEntity<?> response = controller.deleteCovoiturage(7L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(covoiturageService).delete(7L);
    }

    @Test
    void deleteCovoiturage_QuandIntrouvable_DoitRetourner404() {
        doThrow(new RuntimeException("Suppression impossible"))
                .when(covoiturageService).delete(7L);

        ResponseEntity<?> response = controller.deleteCovoiturage(7L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Suppression impossible", response.getBody());
    }

    @Test
    void participer_QuandValide_DoitRetourner201() {
        ParticipationCovoiturage participation = new ParticipationCovoiturage();
        when(reserverCovoiturage.reserver(1L, 10L)).thenReturn(participation);

        ResponseEntity<?> response = controller.participer(1L, 10L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(participation, response.getBody());
    }

    @Test
    void participer_QuandRegleMetierViolée_DoitRetourner400() {
        when(reserverCovoiturage.reserver(1L, 5L))
                .thenThrow(new IllegalStateException("L'organisateur ne peut pas réserver une place sur son propre covoiturage"));

        ResponseEntity<?> response = controller.participer(1L, 5L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("organisateur"));
    }

    @Test
    void participer_QuandRessourceIntrouvable_DoitRetourner404() {
        when(reserverCovoiturage.reserver(1L, 10L))
                .thenThrow(new RuntimeException("Covoiturage introuvable"));

        ResponseEntity<?> response = controller.participer(1L, 10L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Covoiturage introuvable", response.getBody());
    }

    @Test
    void annulerParticipation_QuandValide_DoitRetourner204() {
        ResponseEntity<?> response = controller.annulerParticipation(2L, 8L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(annuleParticiaptionCovoiturage).annuler(2L, 8L);
    }

    @Test
    void annulerParticipation_QuandIntrouvable_DoitRetourner404() {
        doThrow(new RuntimeException("Participation introuvable"))
                .when(annuleParticiaptionCovoiturage).annuler(2L, 8L);

        ResponseEntity<?> response = controller.annulerParticipation(2L, 8L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Participation introuvable", response.getBody());
    }

    @Test
    void getMesReservations_QuandExiste_DoitRetourner200() {
        Map<String, List<Covoiturage>> map = Map.of(
                "enCours", List.of(new Covoiturage()),
                "historique", List.of()
        );
        when(covoiturageService.getReservationsPassager(4L)).thenReturn(map);

        ResponseEntity<?> response = controller.getMesReservations(4L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(map, response.getBody());
    }

    @Test
    void getMesReservations_QuandIntrouvable_DoitRetourner404() {
        when(covoiturageService.getReservationsPassager(4L))
                .thenThrow(new RuntimeException("Utilisateur introuvable"));

        ResponseEntity<?> response = controller.getMesReservations(4L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Utilisateur introuvable", response.getBody());
    }

    @Test
    void getMesAnnonces_QuandExiste_DoitRetourner200() {
        Map<String, List<Covoiturage>> map = Map.of("enCours", List.of());
        when(covoiturageService.getAnnoncesConducteur(6L)).thenReturn(map);

        ResponseEntity<?> response = controller.getMesAnnonces(6L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(map, response.getBody());
    }

    @Test
    void getMesAnnonces_QuandIntrouvable_DoitRetourner404() {
        when(covoiturageService.getAnnoncesConducteur(6L))
                .thenThrow(new RuntimeException("Conducteur introuvable"));

        ResponseEntity<?> response = controller.getMesAnnonces(6L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Conducteur introuvable", response.getBody());
    }
}
