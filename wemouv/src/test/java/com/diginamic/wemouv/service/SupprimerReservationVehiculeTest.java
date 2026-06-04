package com.diginamic.wemouv.service;

import com.diginamic.wemouv.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link SupprimerReservationVehicule}.
 * <p>
 * Cette classe valide la logique de suppression sécurisée d'une réservation,
 * en s'assurant que le service vérifie l'existence de la donnée avant toute
 * tentative de suppression en base.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class SupprimerReservationVehiculeTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private SupprimerReservationVehicule supprimerService;

    /**
     * Vérifie que si une réservation existe, le service appelle correctement
     * la méthode de suppression du repository.
     */
    @Test
    void supprimer_QuandExiste_DoitAppelerDelete() {
        // ARRANGE
        Long id = 1L;
        when(reservationRepository.existsById(id)).thenReturn(true);

        // ACT
        supprimerService.supprimer(id);

        // ASSERT
        verify(reservationRepository, times(1)).deleteById(id);
    }

    /**
     * Vérifie que si aucune réservation n'est trouvée pour l'identifiant fourni,
     * le service lève une exception et ne tente aucune opération de suppression.
     */
    @Test
    void supprimer_QuandNExistePas_DoitLancerException() {
        // ARRANGE
        Long id = 99L;
        when(reservationRepository.existsById(id)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> supprimerService.supprimer(id));
        verify(reservationRepository, never()).deleteById(anyLong());
    }
}