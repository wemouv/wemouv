package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.dto.ReservationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeDeServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link ReservationService}.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UtilisateurService utilisateurService;
    @Mock private VehiculeDeServiceRepository vehiculeDeServiceRepository;

    @InjectMocks private ReservationService reservationService;

    private Reservation reservation;
    private VehiculeDeService vehicule;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        vehicule = new VehiculeDeService();
        vehicule.setId(1L);

        utilisateur = new Utilisateur();
        utilisateur.setEmail("test@test.com");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setVehicule(vehicule);
        reservation.setUtilisateur(utilisateur);
        reservation.setDateDebut(LocalDateTime.now().plusDays(1));
        reservation.setDateFin(LocalDateTime.now().plusDays(2));
    }

    /**
     * Vérifie que la création d'une réservation aboutit lorsque le véhicule
     * n'est associé à aucune autre réservation sur la période demandée.
     */
    @Test
    void create_DoitReussir_QuandVehiculeDisponible() {
        ReservationRequest request = new ReservationRequest();
        request.setVehiculeId(1L);
        request.setDateDebut(LocalDateTime.now().plusDays(5));
        request.setDateFin(LocalDateTime.now().plusDays(6));

        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);
        when(vehiculeDeServiceRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findByVehiculeId(1L)).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation result = reservationService.create(request, "test@test.com");

        assertNotNull(result);
        assertEquals(Statut.EN_ATTENTE, result.getStatut()); // ← corrigé
        verify(reservationRepository).save(any(Reservation.class));
    }

    /**
     * Vérifie qu'une exception est levée lorsqu'une tentative de réservation
     * chevauche une période déjà occupée en base de données.
     */
    @Test
    void create_DoitLancerException_QuandVehiculeDejaReserve() {
        ReservationRequest request = new ReservationRequest();
        request.setVehiculeId(1L);
        request.setDateDebut(reservation.getDateDebut());
        request.setDateFin(reservation.getDateFin());

        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);
        when(vehiculeDeServiceRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findByVehiculeId(1L)).thenReturn(Collections.singletonList(reservation));

        assertThrows(RuntimeException.class, () -> reservationService.create(request, "test@test.com"));
    }

    /**
     * Vérifie que lors d'une mise à jour, les informations sont fusionnées
     * tout en préservant les relations critiques comme l'utilisateur propriétaire.
     */
    @Test
    void update_DoitFusionnerDonnees_SansEffacerUtilisateur() {
        ReservationModificationRequest details = new ReservationModificationRequest();
        details.setDateDebut(LocalDateTime.now().plusDays(10));
        details.setDateFin(LocalDateTime.now().plusDays(11));
        details.setVehiculeId(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(vehiculeDeServiceRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation updated = reservationService.update(1L, details);

        assertEquals(details.getDateDebut(), updated.getDateDebut());
        assertNotNull(updated.getUtilisateur(), "L'utilisateur doit être préservé après mise à jour");
        verify(reservationRepository).save(reservation);
    }

    @Test
    void findAll_DoitRetournerListe() {
        when(reservationRepository.findAll()).thenReturn(java.util.List.of(reservation));
        java.util.List<Reservation> result = reservationService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findById_QuandExiste_DoitRetournerReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Reservation result = reservationService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_QuandExistePas_DoitLancerException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservationService.findById(1L));
    }

    @Test
    void annuler_DoitPasserStatutAAnnule() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation result = reservationService.annuler(1L);

        assertEquals(Statut.ANNULE, result.getStatut());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void confirmer_DoitPasserStatutAConfirme() {
        reservation.setStatut(Statut.EN_ATTENTE);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation result = reservationService.confirmer(1L);

        assertEquals(Statut.CONFIRME, result.getStatut());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void delete_QuandExiste_DoitSupprimer() {
        when(reservationRepository.existsById(1L)).thenReturn(true);
        reservationService.delete(1L);
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void delete_QuandExistePas_DoitLancerException() {
        when(reservationRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> reservationService.delete(1L));
    }

    @Test
    void findByUtilisateur_DoitRetournerListe() {
        when(reservationRepository.findByUtilisateurId(1L)).thenReturn(java.util.List.of(reservation));
        java.util.List<Reservation> result = reservationService.findByUtilisateur(1L);
        assertEquals(1, result.size());
    }

    @Test
    void findByVehicule_DoitRetournerListe() {
        when(reservationRepository.findByVehiculeId(1L)).thenReturn(java.util.List.of(reservation));
        java.util.List<Reservation> result = reservationService.findByVehicule(1L);
        assertEquals(1, result.size());
    }

    @Test
    void update_QuandCertainsChampsSontNull_DoitMettreAJourUniquementChampsNonNull() {
        ReservationModificationRequest details = new ReservationModificationRequest();
        // dateDebut, dateFin et vehiculeId restent null

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation updated = reservationService.update(1L, details);

        assertEquals(reservation.getDateDebut(), updated.getDateDebut());
        assertEquals(reservation.getDateFin(), updated.getDateFin());
        assertEquals(reservation.getVehicule(), updated.getVehicule());
        verify(reservationRepository).save(reservation);
    }
}