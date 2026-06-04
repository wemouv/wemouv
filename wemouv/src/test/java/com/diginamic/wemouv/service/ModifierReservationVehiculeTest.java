package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link ModifierReservationVehicule}.
 * <p>
 * Cette classe valide la logique métier liée à la modification d'une réservation,
 * incluant la mise à jour partielle des champs et le respect des règles de
 * cohérence temporelle (dates).
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ModifierReservationVehiculeTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private VehiculeRepository vehiculeRepository;

    @InjectMocks private ModifierReservationVehicule modifierService;

    /**
     * Vérifie que la mise à jour est correctement appliquée et persistée
     * lorsque les nouvelles dates de réservation sont valides.
     */
    @Test
    void modifier_QuandDatesValides_DoitMettreAJourEtSauvegarder() {
        // ARRANGE
        Long id = 1L;
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDateTime.now());
        reservation.setDateFin(LocalDateTime.now().plusDays(2));

        ReservationModificationRequest request = new ReservationModificationRequest();
        request.setDateFin(LocalDateTime.now().plusDays(3));

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Reservation result = modifierService.modifier(id, request);

        // ASSERT
        assertEquals(request.getDateFin(), result.getDateFin());
        verify(reservationRepository).save(reservation);
    }

    /**
     * Vérifie qu'une exception {@link IllegalStateException} est levée
     * si les dates fournies sont incohérentes (ex: date de fin avant date de début).
     */
    @Test
    void modifier_QuandDatesIncoherentes_DoitLancerIllegalStateException() {
        // ARRANGE
        Long id = 1L;
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDateTime.now().plusDays(5));
        reservation.setDateFin(LocalDateTime.now().plusDays(2));

        ReservationModificationRequest request = new ReservationModificationRequest();
        request.setDateFin(LocalDateTime.now().minusDays(1));

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> modifierService.modifier(id, request));
    }

    /**
     * Vérifie qu'une exception {@link IllegalStateException} est levée
     * si aucune donnée de modification n'est présente dans la requête.
     */
    @Test
    void modifier_QuandAucunChampFourni_DoitLancerIllegalStateException() {
        // ARRANGE
        Long id = 1L;
        Reservation reservation = new Reservation();
        ReservationModificationRequest request = new ReservationModificationRequest();

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> modifierService.modifier(id, request));
    }
}