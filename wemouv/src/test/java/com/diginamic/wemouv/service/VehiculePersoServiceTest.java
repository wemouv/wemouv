package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.VehiculePersoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link VehiculePersoService}.
 */
@ExtendWith(MockitoExtension.class)
class VehiculePersoServiceTest {

    @Mock private VehiculePersoRepository vehiculePersoRepository;
    @Mock private CovoiturageRepository covoiturageRepository;

    @InjectMocks private VehiculePersoService vehiculePersoService;

    @Test
    void findAll_DoitRetournerListe() {
        VehiculePerso v = new VehiculePerso();
        when(vehiculePersoRepository.findAll()).thenReturn(Collections.singletonList(v));

        List<VehiculePerso> result = vehiculePersoService.findAll();

        assertEquals(1, result.size());
        verify(vehiculePersoRepository).findAll();
    }

    @Test
    void findById_QuandExiste_DoitRetournerVehicule() {
        Long id = 1L;
        VehiculePerso v = new VehiculePerso();
        when(vehiculePersoRepository.findById(id)).thenReturn(Optional.of(v));

        VehiculePerso result = vehiculePersoService.findById(id);

        assertNotNull(result);
        assertEquals(v, result);
    }

    @Test
    void findById_QuandExistePas_DoitLancerException() {
        Long id = 1L;
        when(vehiculePersoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehiculePersoService.findById(id));
    }

    @Test
    void create_DoitSauvegarderVehicule() {
        VehiculePerso v = new VehiculePerso();
        when(vehiculePersoRepository.save(v)).thenReturn(v);

        VehiculePerso result = vehiculePersoService.create(v);

        assertNotNull(result);
        verify(vehiculePersoRepository).save(v);
    }

    @Test
    void update_QuandExiste_DoitMettreAJourEtSauvegarder() {
        Long id = 1L;
        VehiculePerso existing = new VehiculePerso();
        existing.setImmatriculation("AA-123-AA");

        VehiculePerso details = new VehiculePerso();
        details.setImmatriculation("BB-456-BB");
        details.setProprietaire(new Utilisateur());

        when(vehiculePersoRepository.findById(id)).thenReturn(Optional.of(existing));
        when(vehiculePersoRepository.save(any(VehiculePerso.class))).thenAnswer(i -> i.getArguments()[0]);

        VehiculePerso result = vehiculePersoService.update(id, details);

        assertEquals("BB-456-BB", result.getImmatriculation());
        assertNotNull(result.getProprietaire());
        verify(vehiculePersoRepository).save(existing);
    }

    @Test
    void update_QuandExistePas_DoitLancerException() {
        Long id = 1L;
        VehiculePerso details = new VehiculePerso();
        when(vehiculePersoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehiculePersoService.update(id, details));
    }

    @Test
    void delete_QuandExiste_DoitSupprimer() {
        Long id = 1L;
        when(vehiculePersoRepository.existsById(id)).thenReturn(true);

        vehiculePersoService.delete(id);

        verify(vehiculePersoRepository).deleteById(id);
    }

    @Test
    void delete_QuandExistePas_DoitLancerException() {
        Long id = 1L;
        when(vehiculePersoRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> vehiculePersoService.delete(id));
    }

    @Test
    void findByProprietaire_DoitRetournerListe() {
        Long proprietaireId = 1L;
        VehiculePerso v = new VehiculePerso();
        when(vehiculePersoRepository.findByProprietaireId(proprietaireId)).thenReturn(Collections.singletonList(v));

        List<VehiculePerso> result = vehiculePersoService.findByProprietaire(proprietaireId);

        assertEquals(1, result.size());
        verify(vehiculePersoRepository).findByProprietaireId(proprietaireId);
    }

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