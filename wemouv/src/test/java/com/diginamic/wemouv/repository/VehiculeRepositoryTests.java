package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour {@link VehiculeRepository}.
 * <p>
 * Vérifie les requêtes Spring Data sur l'entité racine {@link Vehicule}
 * (requêtes polymorphes sur l'ensemble des véhicules).
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class VehiculeRepositoryTests {

    @Autowired private VehiculeRepository vehiculeRepository;
    @Autowired private VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /**
     * Vérifie {@link VehiculeRepository#findByImmatriculation(String)} :
     * retourne le véhicule ou un Optional vide.
     */
    @Test
    void findByImmatriculation_DoitRetournerLeVehiculeOuOptionalVide() {
        // ARRANGE
        VehiculeDeService v = vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-100-AA", Marque.RENAULT, Motorisation.ESSENCE, Categorie.BERLINE, 5));

        // ACT & ASSERT — existant
        Optional<Vehicule> trouve = vehiculeRepository.findByImmatriculation("VV-100-AA");
        assertTrue(trouve.isPresent());
        assertEquals(v.getId(), trouve.get().getId());

        // ACT & ASSERT — inconnu
        assertTrue(vehiculeRepository.findByImmatriculation("INCONNU").isEmpty());
    }

    /**
     * Vérifie {@link VehiculeRepository#findByMarque(Marque)} :
     * filtre tous les véhicules par marque.
     */
    @Test
    void findByMarque_DoitFiltrerParMarque() {
        // ARRANGE — 2 RENAULT, 1 PEUGEOT
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-101-BB", Marque.RENAULT, Motorisation.ESSENCE, Categorie.BERLINE, 5));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-102-CC", Marque.RENAULT, Motorisation.DIESEL, Categorie.UTILITAIRE, 2));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-103-DD", Marque.PEUGEOT, Motorisation.ESSENCE, Categorie.CITADINE, 4));

        // ACT
        List<Vehicule> result = vehiculeRepository.findByMarque(Marque.RENAULT);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getMarque() == Marque.RENAULT));
    }

    /**
     * Vérifie {@link VehiculeRepository#findByMotorisation(Motorisation)} :
     * filtre tous les véhicules par motorisation.
     */
    @Test
    void findByMotorisation_DoitFiltrerParMotorisation() {
        // ARRANGE — 2 ESSENCE, 1 ELECTRIQUE
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-104-EE", Marque.RENAULT, Motorisation.ESSENCE, Categorie.BERLINE, 5));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-105-FF", Marque.PEUGEOT, Motorisation.ESSENCE, Categorie.SUV, 5));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-106-GG", Marque.TOYOTA, Motorisation.ELECTRIQUE, Categorie.CITADINE, 4));

        // ACT
        List<Vehicule> result = vehiculeRepository.findByMotorisation(Motorisation.ESSENCE);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getMotorisation() == Motorisation.ESSENCE));
    }

    /**
     * Vérifie {@link VehiculeRepository#findByCategorie(Categorie)} :
     * filtre tous les véhicules par catégorie.
     */
    @Test
    void findByCategorie_DoitFiltrerParCategorie() {
        // ARRANGE — 2 BERLINE, 1 SUV
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-107-HH", Marque.RENAULT, Motorisation.ESSENCE, Categorie.BERLINE, 5));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-108-II", Marque.PEUGEOT, Motorisation.DIESEL, Categorie.BERLINE, 5));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-109-JJ", Marque.TOYOTA, Motorisation.HYBRIDE, Categorie.SUV, 5));

        // ACT
        List<Vehicule> result = vehiculeRepository.findByCategorie(Categorie.BERLINE);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getCategorie() == Categorie.BERLINE));
    }

    /**
     * Vérifie {@link VehiculeRepository#findByNbPlaceGreaterThanEqual(int)} :
     * condition {@code nb_place >= n}.
     */
    @Test
    void findByNbPlaceGreaterThanEqual_DoitRetournerLesVehiculesAvecAssezDePlaces() {
        // ARRANGE — 2 places, 5 places, 7 places
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-110-KK", Marque.RENAULT, Motorisation.ESSENCE, Categorie.UTILITAIRE, 2));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-111-LL", Marque.PEUGEOT, Motorisation.DIESEL, Categorie.BERLINE, 5));
        vehiculeDeServiceRepository.save(creerVehiculeDeService(
                "VV-112-MM", Marque.TOYOTA, Motorisation.HYBRIDE, Categorie.MINIBUS, 7));

        // ACT
        List<Vehicule> result = vehiculeRepository.findByNbPlaceGreaterThanEqual(5);

        // ASSERT — 5 et 7 uniquement
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getNbPlace() >= 5));
    }

    private VehiculeDeService creerVehiculeDeService(
            String immatriculation,
            Marque marque,
            Motorisation motorisation,
            Categorie categorie,
            int nbPlace) {
        VehiculeDeService v = new VehiculeDeService();
        v.setImmatriculation(immatriculation);
        v.setMarque(marque);
        v.setMotorisation(motorisation);
        v.setNbPlace(nbPlace);
        v.setCategorie(categorie);
        v.setStatut(Disponibilite.DISPONIBLE);
        v.setLocalisation("Parking A");
        return v;
    }
}
