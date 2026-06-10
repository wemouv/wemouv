package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.CovoiturageRequest;
import com.diginamic.wemouv.entity.*;
import com.diginamic.wemouv.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link CovoiturageService}.
 */
@ExtendWith(MockitoExtension.class)
class CovoiturageServiceTest {

    @Mock private CovoiturageRepository covoiturageRepository;
    @Mock private ParticipationCovoiturageRepository participationRepository;
    @Mock private VehiculeRepository vehiculeRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private EmailService emailService;

    @InjectMocks private CovoiturageService covoiturageService;

    private Covoiturage covoiturage;
    private Utilisateur utilisateur;

    /**
     * Initialisation des données de test avant chaque exécution.
     */
    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setEmail("test@test.com");
        utilisateur.setId(1L);

        covoiturage = new Covoiturage();
        covoiturage.setId(1L);
        covoiturage.setOrganisateur(utilisateur);
        covoiturage.setNbPlacesRestantes(3);

        ParticipationCovoiturage participation = new ParticipationCovoiturage();
        participation.setUtilisateur(utilisateur);
        participation.setCovoiturage(covoiturage);

        covoiturage.setParticipations(List.of(participation));
    }

    /**
     * Vérifie que la création d'un covoiturage sauvegarde bien l'entité.
     */
    @Test
    void create_DoitSauvegarderCovoiturage_AvecReferencesValides() {
        CovoiturageRequest request = new CovoiturageRequest();
        request.setVehiculeId(1L);
        request.setOrganisateurId(1L);
        request.setConducteurId(1L);
        request.setNbPlacesRestantes(3);
        request.setNbPlacesInitial(3);

        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(new Vehicule()));
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(covoiturageRepository.save(any(Covoiturage.class))).thenAnswer(i -> i.getArguments()[0]);

        Covoiturage result = covoiturageService.create(request);

        assertNotNull(result);
        verify(covoiturageRepository).save(any(Covoiturage.class));
    }

    /**
     * Vérifie qu'une mise à jour (update) entraîne l'envoi de notifications par e-mail.
     */
    @Test
    void update_DoitEnvoyerEmails_AOrganisateurEtPassagers() {
        CovoiturageRequest request = new CovoiturageRequest();
        request.setVehiculeId(1L);
        request.setOrganisateurId(1L);
        request.setConducteurId(1L);
        request.setNbPlacesRestantes(3);
        request.setNbPlacesInitial(3);

        when(covoiturageRepository.findById(1L)).thenReturn(Optional.of(covoiturage));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(new Vehicule()));
        when(utilisateurRepository.findById(anyLong())).thenReturn(Optional.of(utilisateur));

        // 💡 CORRECTION : getArguments()[0] car la méthode save() ne prend qu'un seul paramètre
        when(covoiturageRepository.save(any(Covoiturage.class))).thenAnswer(i -> i.getArguments()[0]);

        covoiturageService.update(1L, request);

        // Vérifie que la méthode sendMail a bien été appelée
        verify(emailService, atLeast(1)).sendMailGroup(any(String[].class), anyString(), anyString());
    }

    /**
     * Vérifie que la récupération des annonces d'un conducteur retourne bien
     * une structure de données (Map) correcte avec les trajets classés.
     */
    @Test
    void getAnnoncesConducteur_DoitRetournerMapCorrecte() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(covoiturageRepository.findByOrganisateurIdAndDateDepartAfterOrderByDateDepartAsc(eq(1L), any()))
                .thenReturn(List.of(covoiturage));

        Map<String, List<Covoiturage>> result = covoiturageService.getAnnoncesConducteur(1L);

        assertNotNull(result);
        assertTrue(result.containsKey("enCours"));
        assertEquals(1, result.get("enCours").size());
    }
}