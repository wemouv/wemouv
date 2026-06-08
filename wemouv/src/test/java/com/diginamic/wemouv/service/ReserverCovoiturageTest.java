package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.*;
import com.diginamic.wemouv.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link ReserverCovoiturage}.
 * <p>
 * Cette classe valide la logique métier de réservation d'un covoiturage,
 * garantissant que les contraintes de disponibilité des places et
 * l'intégrité de la participation sont respectées.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ReserverCovoiturageTest {

    @Mock private CovoiturageRepository covoiturageRepository;
    @Mock private ParticipationCovoiturageIdRepository participationRepository;
    @Mock private UtilisateurRepository utilisateurRepository;

    @InjectMocks private ReserverCovoiturage reserverService;

    /**
     * Vérifie qu'une réservation valide décrémente correctement le nombre
     * de places disponibles et persiste la nouvelle participation.
     */
    @Test
    void reserver_QuandValide_DoitRetournerParticipationEtDecrementerPlaces() {
        Long covoiturageId = 1L;
        Long utilisateurId = 10L;

        // Conducteur (ID != utilisateurId)
        Utilisateur conducteur = new Utilisateur();
        conducteur.setId(99L);

        Covoiturage covoiturage = new Covoiturage();
        covoiturage.setNbPlacesRestantes(2);

        Utilisateur organisateur = new Utilisateur();
        organisateur.setId(99L);
        covoiturage.setOrganisateur(organisateur);

        // CORRECTION ICI : Ajout du conducteur au covoiturage
        covoiturage.setConducteur(conducteur);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurId);

        when(covoiturageRepository.findById(covoiturageId)).thenReturn(Optional.of(covoiturage));
        when(utilisateurRepository.findById(utilisateurId)).thenReturn(Optional.of(utilisateur));
        when(participationRepository.existsById(any(ParticipationCovoiturageId.class))).thenReturn(false);
        when(participationRepository.save(any(ParticipationCovoiturage.class))).thenAnswer(i -> i.getArguments()[0]);

        ParticipationCovoiturage result = reserverService.reserver(covoiturageId, utilisateurId);

        assertNotNull(result);
        assertEquals(1, covoiturage.getNbPlacesRestantes());
        verify(covoiturageRepository).save(covoiturage);
    }

    /**
     * Vérifie qu'une exception est levée lorsqu'un utilisateur tente de réserver
     * une place sur un covoiturage déjà complet.
     */
    @Test
    void reserver_QuandComplet_DoitLancerException() {
        // Conducteur
        Utilisateur conducteur = new Utilisateur();
        conducteur.setId(99L);

        Covoiturage covoiturage = new Covoiturage();
        covoiturage.setNbPlacesRestantes(0);
        covoiturage.setConducteur(conducteur); // CORRECT !

        when(covoiturageRepository.findById(1L)).thenReturn(Optional.of(covoiturage));
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(new Utilisateur()));

        assertThrows(IllegalStateException.class, () -> reserverService.reserver(1L, 10L));
    }

    /**
     * Vérifie qu'un organisateur ne peut pas réserver une place sur le covoiturage qu'il a publié.
     */
    @Test
    void reserver_QuandOrganisateur_DoitLancerException() {
        Long covoiturageId = 1L;
        Long organisateurId = 5L;

        Covoiturage covoiturage = new Covoiturage();
        covoiturage.setNbPlacesRestantes(2);

        Utilisateur organisateur = new Utilisateur();
        organisateur.setId(organisateurId);
        covoiturage.setOrganisateur(organisateur);

        // CORRECTION ICI : L'organisateur est aussi le conducteur dans ce test
        covoiturage.setConducteur(organisateur);

        when(covoiturageRepository.findById(covoiturageId)).thenReturn(Optional.of(covoiturage));
        when(utilisateurRepository.findById(organisateurId)).thenReturn(Optional.of(organisateur));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> reserverService.reserver(covoiturageId, organisateurId));

        assertNotNull(ex.getMessage());
        verify(participationRepository, never()).save(any());
        verify(covoiturageRepository, never()).save(any());
    }
}