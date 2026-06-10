package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link RechercheCovoiturage}.
 * <p>
 * Cette classe vérifie la logique de filtrage des covoiturages (places disponibles,
 * statuts) pour s'assurer que seuls les trajets pertinents sont retournés
 * aux utilisateurs lors d'une recherche.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class RechercheCovoiturageTest {

    @Mock
    private CovoiturageRepository covoiturageRepository;

    @InjectMocks
    private RechercheCovoiturage rechercheCovoiturage;

    /**
     * Vérifie que la recherche exclut automatiquement les covoiturages
     * dont le nombre de places restantes est égal à zéro.
     */
    @Test
    void rechercher_DoitExclureTrajetsComplets() {
        // ARRANGE
        Covoiturage complet = new Covoiturage();
        complet.setNbPlacesRestantes(0);

        Covoiturage libre = new Covoiturage();
        libre.setNbPlacesRestantes(2);

        when(covoiturageRepository.findAll()).thenReturn(List.of(complet, libre));

        // ACT
        List<Covoiturage> result = rechercheCovoiturage.rechercher(null, null, null, null);

        // ASSERT
        assertEquals(1, result.size(), "Seul le trajet libre doit être retourné");
        assertEquals(libre, result.get(0));
    }

    /**
     * Vérifie que le filtrage par statut est correctement appliqué par le service
     * même si le dépôt renvoie une liste plus large.
     */
    @Test
    void rechercher_QuandStatutFourni_DoitFiltrerEnMemoire() {
        // ARRANGE
        Covoiturage c1 = new Covoiturage();
        c1.setStatut(Statut.CONFIRME);
        c1.setNbPlacesRestantes(1);

        Covoiturage c2 = new Covoiturage();
        c2.setStatut(Statut.ANNULE);
        c2.setNbPlacesRestantes(1);

        when(covoiturageRepository.findByStatut(Statut.CONFIRME)).thenReturn(List.of(c1, c2));

        // ACT
        List<Covoiturage> result = rechercheCovoiturage.rechercher(null, null, null, Statut.CONFIRME);

        // ASSERT
        assertEquals(1, result.size(), "Le filtre doit éliminer les trajets annulés");
        assertEquals(Statut.CONFIRME, result.get(0).getStatut());
    }

    @Test
    void rechercher_QuandStatutEtDateFournis_DoitRechercherParLesDeux() {
        LocalDateTime date = LocalDateTime.now();
        Covoiturage c = new Covoiturage();
        c.setStatut(Statut.CONFIRME);
        c.setDateDepart(date);
        c.setNbPlacesRestantes(2);

        when(covoiturageRepository.findByStatutAndDateDepart(Statut.CONFIRME, date)).thenReturn(List.of(c));

        List<Covoiturage> result = rechercheCovoiturage.rechercher(null, null, date, Statut.CONFIRME);

        assertEquals(1, result.size());
        verify(covoiturageRepository).findByStatutAndDateDepart(Statut.CONFIRME, date);
    }

    @Test
    void rechercher_QuandDepartFourni_DoitFiltrerParDepart() {
        Covoiturage c = new Covoiturage();
        c.setAdresseDepart("Montpellier");
        c.setNbPlacesRestantes(2);

        when(covoiturageRepository.findByAdresseDepartContainingIgnoreCase("Montpellier")).thenReturn(List.of(c));

        List<Covoiturage> result = rechercheCovoiturage.rechercher("Montpellier", null, null, null);

        assertEquals(1, result.size());
        verify(covoiturageRepository).findByAdresseDepartContainingIgnoreCase("Montpellier");
    }

    @Test
    void rechercher_QuandArriveeFournie_DoitFiltrerParArrivee() {
        Covoiturage c = new Covoiturage();
        c.setAdresseArrive("Nîmes");
        c.setNbPlacesRestantes(1);

        when(covoiturageRepository.findByAdresseArriveContainingIgnoreCase("Nîmes")).thenReturn(List.of(c));

        List<Covoiturage> result = rechercheCovoiturage.rechercher(null, "Nîmes", null, null);

        assertEquals(1, result.size());
        verify(covoiturageRepository).findByAdresseArriveContainingIgnoreCase("Nîmes");
    }

    @Test
    void rechercher_QuandDateFournie_DoitFiltrerParDate() {
        LocalDateTime date = LocalDateTime.now();
        Covoiturage c = new Covoiturage();
        c.setDateDepart(date);
        c.setNbPlacesRestantes(3);

        when(covoiturageRepository.findByDateDepart(date)).thenReturn(List.of(c));

        List<Covoiturage> result = rechercheCovoiturage.rechercher(null, null, date, null);

        assertEquals(1, result.size());
        verify(covoiturageRepository).findByDateDepart(date);
    }

    @Test
    void rechercher_QuandDepartEtArriveeVides_DoitToutRetourner() {
        Covoiturage c = new Covoiturage();
        c.setNbPlacesRestantes(1);

        when(covoiturageRepository.findAll()).thenReturn(List.of(c));

        List<Covoiturage> result = rechercheCovoiturage.rechercher(" ", "   ", null, null);

        assertEquals(1, result.size());
        verify(covoiturageRepository).findAll();
    }
}