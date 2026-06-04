package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.VehiculePersoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link VehiculePersoService}.
 * <p>
 * Valide les opérations CRUD et la logique de calcul de disponibilité
 * des véhicules personnels en fonction des trajets planifiés.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class VehiculePersoServiceTest {

    @Mock private VehiculePersoRepository vehiculePersoRepository;
    @Mock private CovoiturageRepository covoiturageRepository;

    @InjectMocks private VehiculePersoService vehiculePersoService;

    /**
     * Vérifie qu'un véhicule personnel est correctement exclu de la liste
     * des disponibilités s'il est déjà engagé dans un covoiturage sur la période.
     */
    @Test
    void findAllAvailable_QuandCovoiturageExiste_DoitExclureLeVehicule() {
        // ARRANGE
        VehiculePerso v1 = new VehiculePerso();
        v1.setId(1L);

        Covoiturage covoit = new Covoiturage();
        covoit.setDateDepart(LocalDateTime.now().plusHours(1));
        covoit.setDureeTrajet(2.0); // Finit à J+3h

        when(vehiculePersoRepository.findAll()).thenReturn(List.of(v1));
        when(covoiturageRepository.findByVehiculeId(1L)).thenReturn(List.of(covoit));

        // ACT : Cherche une disponibilité chevauchant le trajet (entre H+0 et H+2)
        List<VehiculePerso> dispo = vehiculePersoService.findAllAvailable(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        // ASSERT
        assertTrue(dispo.isEmpty(), "Le véhicule devrait être indisponible à cause du chevauchement");
    }

    /**
     * Vérifie qu'un véhicule sans covoiturage associé est bien considéré comme disponible.
     */
    @Test
    void findAllAvailable_QuandAucunCovoiturage_DoitRetournerLeVehicule() {
        // ARRANGE
        VehiculePerso v1 = new VehiculePerso();
        v1.setId(1L);

        when(vehiculePersoRepository.findAll()).thenReturn(List.of(v1));
        when(covoiturageRepository.findByVehiculeId(1L)).thenReturn(List.of());

        // ACT
        List<VehiculePerso> dispo = vehiculePersoService.findAllAvailable(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );

        // ASSERT
        assertEquals(1, dispo.size());
        assertEquals(v1, dispo.get(0));
    }
}