package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link ListeReservationVehicule}.
 * <p>
 * Cette classe valide que le service de consultation des réservations
 * délègue correctement les appels de lecture au {@link ReservationRepository}.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ListeReservationVehiculeTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ListeReservationVehicule listeReservationService;

    /**
     * Vérifie que la méthode {@code lister} récupère bien l'ensemble
     * des réservations disponibles dans la base de données.
     */
    @Test
    void lister_DoitRetournerToutesLesReservations() {
        // ARRANGE
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation(), new Reservation()));

        // ACT
        List<Reservation> result = listeReservationService.lister();

        // ASSERT
        assertEquals(2, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    /**
     * Vérifie que la méthode {@code listerParVehicule} filtre correctement
     * les réservations selon l'identifiant du véhicule fourni.
     */
    @Test
    void listerParVehicule_DoitRetournerReservationsDuVehicule() {
        // ARRANGE
        Long vehiculeId = 1L;
        when(reservationRepository.findByVehiculeId(vehiculeId)).thenReturn(List.of(new Reservation()));

        // ACT
        List<Reservation> result = listeReservationService.listerParVehicule(vehiculeId);

        // ASSERT
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findByVehiculeId(vehiculeId);
    }
}