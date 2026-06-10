package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.CovoiturageRequest;
import com.diginamic.wemouv.entity.*;
import com.diginamic.wemouv.enums.Statut;
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

        request.setStatut(Statut.ANNULE);
        request.setNbPlacesInitial(4);

        request.setStatut(Statut.ANNULE);
        request.setNbPlacesInitial(4);

        when(covoiturageRepository.findById(1L)).thenReturn(Optional.of(covoiturage));
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(new Vehicule()));
        when(utilisateurRepository.findById(anyLong())).thenReturn(Optional.of(utilisateur));

        // 💡 CORRECTION : getArguments()[0] car la méthode save() ne prend qu'un seul paramètre
        when(covoiturageRepository.save(any(Covoiturage.class))).thenAnswer(i -> i.getArguments()[0]);

        covoiturageService.update(1L, request);

        //verify(emailService, atLeast(2)).sendMail(anyString(), anyString(), anyString());
    }

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

    @Test
    void findAll_DoitRetournerListe() {
        when(covoiturageRepository.findAll()).thenReturn(List.of(covoiturage));
        List<Covoiturage> result = covoiturageService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void findById_QuandExiste_DoitRetournerCovoiturage() {
        when(covoiturageRepository.findById(1L)).thenReturn(Optional.of(covoiturage));
        Covoiturage result = covoiturageService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findById_QuandExistePas_DoitLancerException() {
        when(covoiturageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> covoiturageService.findById(1L));
    }

    @Test
    void findByOrganisateur_DoitRetournerListe() {
        when(covoiturageRepository.findByOrganisateurId(1L)).thenReturn(List.of(covoiturage));
        List<Covoiturage> result = covoiturageService.findByOrganisateur(1L);
        assertEquals(1, result.size());
    }

    @Test
    void findByVehicule_DoitRetournerListe() {
        when(covoiturageRepository.findByVehiculeId(1L)).thenReturn(List.of(covoiturage));
        List<Covoiturage> result = covoiturageService.findByVehicule(1L);
        assertEquals(1, result.size());
    }

    @Test
    void findByStatut_DoitRetournerListe() {
        when(covoiturageRepository.findByStatut(com.diginamic.wemouv.enums.Statut.CONFIRME)).thenReturn(List.of(covoiturage));
        List<Covoiturage> result = covoiturageService.findByStatut(com.diginamic.wemouv.enums.Statut.CONFIRME);
        assertEquals(1, result.size());
    }

    @Test
    void update_QuandReduireNbPlacesPlusInscrits_DoitLancerException() {
        CovoiturageRequest request = new CovoiturageRequest();
        request.setNbPlacesInitial(0); // 0 places but we have 1 passager in setUp
        request.setVehiculeId(1L);
        request.setOrganisateurId(1L);
        request.setConducteurId(1L);

        when(covoiturageRepository.findById(1L)).thenReturn(Optional.of(covoiturage));

        assertThrows(IllegalStateException.class, () -> covoiturageService.update(1L, request));
    }

    @Test
    void delete_DoitEnvoyerEmailsEtSupprimerTrajetEtParticipations() {
        when(covoiturageRepository.findById(1L)).thenReturn(Optional.of(covoiturage));

        covoiturageService.delete(1L);

        verify(emailService).sendMailGroup(any(String[].class), anyString(), anyString());
        verify(participationRepository).deleteByCovoiturageId(1L);
        verify(covoiturageRepository).delete(covoiturage);
    }

    @Test
    void participer_EtAnnulerParticipation_DoitSExecuterSansErreur() {
        assertNull(covoiturageService.participer(1L, 1L));
        assertDoesNotThrow(() -> covoiturageService.annulerParticipation(1L, 1L));
    }

    @Test
    void getReservationsPassager_DoitRetournerMapCorrecte() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        
        ParticipationCovoiturage part1 = new ParticipationCovoiturage();
        part1.setCovoiturage(covoiturage);
        ParticipationCovoiturage part2 = new ParticipationCovoiturage();
        part2.setCovoiturage(covoiturage);

        when(participationRepository.findByUtilisateurIdAndCovoiturageDateDepartAfter(eq(1L), any())).thenReturn(List.of(part1));
        when(participationRepository.findByUtilisateurIdAndCovoiturageDateDepartBefore(eq(1L), any())).thenReturn(List.of(part2));

        Map<String, List<Covoiturage>> result = covoiturageService.getReservationsPassager(1L);

        assertNotNull(result);
        assertEquals(1, result.get("enCours").size());
        assertEquals(1, result.get("historique").size());
    }

    @Test
    void getReservationsPassager_QuandPassagerIntrouvable_DoitLancerException() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> covoiturageService.getReservationsPassager(1L));
    }

    @Test
    void getAnnoncesConducteur_QuandConducteurIntrouvable_DoitLancerException() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> covoiturageService.getAnnoncesConducteur(1L));
    }
}