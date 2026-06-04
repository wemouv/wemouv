package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}