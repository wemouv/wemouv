package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link VehiculeService}.
 * <p>
 * Cette classe valide le comportement du service de gestion des véhicules génériques,
 * en s'assurant que les opérations de lecture, modification et suppression
 * interagissent correctement avec le dépôt {@link VehiculeRepository}.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class VehiculeServiceTest {

    @Mock
    private VehiculeRepository vehiculeRepository;

    @InjectMocks
    private VehiculeService vehiculeService;

    /**
     * Vérifie que la recherche d'un véhicule par son identifiant
     * retourne bien l'instance attendue lorsqu'elle est présente en base.
     */
    @Test
    void findById_QuandExiste_DoitRetournerVehicule() {
        Vehicule vehicule = new Vehicule();
        vehicule.setId(1L);
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(vehicule));

        Vehicule result = vehiculeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    /**
     * Vérifie que la mise à jour d'un véhicule existant déclenche bien
     * une persistance via le repository et que l'identifiant est conservé.
     */
    @Test
    void update_QuandVehiculeExiste_DoitSauvegarder() {
        Vehicule vehiculeExistant = new Vehicule();
        vehiculeExistant.setId(1L);
        Vehicule modifs = new Vehicule();

        when(vehiculeRepository.existsById(1L)).thenReturn(true);
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(modifs);

        Vehicule result = vehiculeService.update(1L, modifs);

        verify(vehiculeRepository).save(any(Vehicule.class));
        assertEquals(1L, modifs.getId(), "L'identifiant du véhicule doit être préservé après mise à jour");
    }

    /**
     * Vérifie que la demande de suppression d'un véhicule existant
     * appelle correctement la méthode de suppression du repository.
     */
    @Test
    void delete_QuandVehiculeExiste_DoitSupprimer() {
        when(vehiculeRepository.existsById(1L)).thenReturn(true);

        vehiculeService.delete(1L);

        verify(vehiculeRepository).deleteById(1L);
    }
}