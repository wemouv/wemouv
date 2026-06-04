package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageIdRepository;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link AnnuleParticiaptionCovoiturage}.
 * <p>
 * Cette classe valide la logique métier liée à l'annulation d'une participation :
 * vérification de l'existence des données, suppression sécurisée de la jointure
 * et mise à jour cohérente du nombre de places disponibles sur le covoiturage.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class AnnuleParticiaptionCovoiturageTest {

    @Mock private CovoiturageRepository covoiturageRepository;
    @Mock private ParticipationCovoiturageIdRepository participationRepository;
    @Mock private UtilisateurRepository utilisateurRepository;

    @InjectMocks private AnnuleParticiaptionCovoiturage annulationService;

    /**
     * Vérifie que lors d'une annulation valide :
     * <ul>
     * <li>La participation est supprimée en base.</li>
     * <li>Le compteur de places restantes du covoiturage est correctement incrémenté.</li>
     * <li>L'état mis à jour du covoiturage est persisté.</li>
     * </ul>
     */
    @Test
    void annuler_QuandToutEstValide_DoitSupprimerParticipationEtIncrementerPlaces() {
        // ARRANGE
        Long covoiturageId = 1L;
        Long utilisateurId = 10L;

        Covoiturage covoiturage = new Covoiturage();
        covoiturage.setId(covoiturageId);
        covoiturage.setNbPlacesInitial(4);
        covoiturage.setNbPlacesRestantes(2); // Initialement 2 places libres

        when(covoiturageRepository.findById(covoiturageId)).thenReturn(Optional.of(covoiturage));
        when(utilisateurRepository.existsById(utilisateurId)).thenReturn(true);
        when(participationRepository.existsById(any(ParticipationCovoiturageId.class))).thenReturn(true);

        // ACT
        annulationService.annuler(covoiturageId, utilisateurId);

        // ASSERT
        // Vérifie la suppression physique de la participation
        verify(participationRepository).deleteById(any(ParticipationCovoiturageId.class));

        // Vérifie la logique métier : le nombre de places libres doit augmenter
        assertEquals(3, covoiturage.getNbPlacesRestantes(), "Le compteur devrait passer de 2 à 3");

        // Vérifie la persistance du changement d'état
        verify(covoiturageRepository).save(covoiturage);
    }
}