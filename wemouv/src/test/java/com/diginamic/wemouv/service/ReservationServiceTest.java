package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.ReservationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeRepository;
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
 * <p>
 * Cette classe valide les règles métier cruciales liées à la réservation de véhicules :
 * vérification des chevauchements de dates, gestion de la disponibilité des véhicules
 * et intégrité des entités lors des mises à jour.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UtilisateurService utilisateurService;
    @Mock private VehiculeRepository vehiculeRepository;

    @InjectMocks private ReservationService reservationService;

    private Reservation reservation;
    private Vehicule vehicule;
    private Utilisateur utilisateur;

    /**
     * Initialisation du contexte de test avec un véhicule, un utilisateur
     * et une réservation existante pour simuler les conflits de planning.
     */
    @BeforeEach
    void setUp() {
        vehicule = new Vehicule();
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
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findByVehiculeId(1L)).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation result = reservationService.create(request, "test@test.com");

        assertNotNull(result);
        assertEquals(Statut.CONFIRME, result.getStatut());
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
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findByVehiculeId(1L)).thenReturn(Collections.singletonList(reservation));

        assertThrows(RuntimeException.class, () -> reservationService.create(request, "test@test.com"));
    }

    /**
     * Vérifie que lors d'une mise à jour, les informations sont fusionnées
     * tout en préservant les relations critiques comme l'utilisateur propriétaire.
     */
    @Test
    void update_DoitFusionnerDonnees_SansEffacerUtilisateur() {
        Reservation details = new Reservation();
        details.setDateDebut(LocalDateTime.now().plusDays(10));
        details.setDateFin(LocalDateTime.now().plusDays(11));
        details.setStatut(Statut.ANNULE);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation updated = reservationService.update(1L, details);

        assertEquals(details.getDateDebut(), updated.getDateDebut());
        assertEquals(Statut.ANNULE, updated.getStatut());
        assertNotNull(updated.getUtilisateur(), "L'utilisateur doit être préservé après mise à jour");
        verify(reservationRepository).save(reservation);
    }
}