package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.*;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link VehiculeDeServiceService}.
 * <p>
 * Cette classe valide les règles métier complexes liées à la flotte de véhicules,
 * notamment la gestion des indisponibilités techniques et la vérification
 * de disponibilité temporelle des véhicules.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class VehiculeDeServiceServiceTest {

    @Mock private VehiculeDeServiceRepository vehiculeRepository;
    @Mock private CovoiturageRepository covoiturageRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private EmailService emailService;

    @InjectMocks private VehiculeDeServiceService vehiculeService;

    /**
     * Vérifie que le passage d'un véhicule en état d'indisponibilité
     * (Réparation ou Hors Service) déclenche automatiquement :
     * <ul>
     * <li>L'annulation des covoiturages futurs associés.</li>
     * <li>L'envoi de notifications aux utilisateurs impactés.</li>
     * </ul>
     */
    @Test
    void update_QuandVehiculePasseEnReparation_DoitAnnulerTrajetsEtNotifier() {
        // ARRANGE
        Long id = 1L;
        VehiculeDeService vehicule = new VehiculeDeService();
        vehicule.setStatut(Disponibilite.DISPONIBLE);

        VehiculeDeService modifs = new VehiculeDeService();
        modifs.setStatut(Disponibilite.EN_REPARATION);

        // Initialisation de l'utilisateur avec email pour éviter les erreurs NullPointerException
        Utilisateur organisateur = new Utilisateur();
        organisateur.setEmail("organisateur@test.com");

        Covoiturage trajetImpacte = new Covoiturage();
        trajetImpacte.setOrganisateur(organisateur);
        trajetImpacte.setDateDepart(LocalDateTime.now().plusDays(5));
        trajetImpacte.setParticipations(List.of());

        when(vehiculeRepository.findById(id)).thenReturn(Optional.of(vehicule));
        when(vehiculeRepository.save(any())).thenReturn(vehicule);
        when(covoiturageRepository.findByVehiculeAndDateDepartAfter(eq(vehicule), any()))
                .thenReturn(List.of(trajetImpacte));

        // ACT
        vehiculeService.update(id, modifs);

        // ASSERT
        assertEquals(Statut.ANNULE, trajetImpacte.getStatut());
        verify(covoiturageRepository).save(trajetImpacte);
        verify(emailService, atLeastOnce()).sendMail(anyString(), anyString(), anyString());
    }

    /**
     * Vérifie que la recherche de véhicules disponibles exclut correctement
     * ceux dont une réservation existante chevauche la période demandée.
     */
    @Test
    void findAllAvailable_DoitFiltrerLesReservationsChevauchantes() {
        // ARRANGE
        VehiculeDeService v1 = new VehiculeDeService();
        v1.setId(1L);
        v1.setStatut(Disponibilite.DISPONIBLE);

        Reservation res = new Reservation();
        res.setDateDebut(LocalDateTime.now().plusDays(1));
        res.setDateFin(LocalDateTime.now().plusDays(3));

        when(vehiculeRepository.findAll()).thenReturn(List.of(v1));
        when(reservationRepository.findByVehiculeId(1L)).thenReturn(List.of(res));

        // ACT : Cherche une disponibilité sur la période [J+1, J+2]
        List<VehiculeDeService> dispo = vehiculeService.findAllAvailable(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        // ASSERT
        assertTrue(dispo.isEmpty(), "Le véhicule devrait être indisponible car réservé");
    }
}