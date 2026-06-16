package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour {@link VehiculeDeServiceRepository}.
 * <p>
 * Vérifie les requêtes Spring Data sur la flotte de service :
 * filtrage par localisation, disponibilité, immatriculation partielle et marque.
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class VehiculeDeServiceRepositoryTests {

    @Autowired private VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /**
     * Vérifie {@link VehiculeDeServiceRepository#findByLocalisation(String)} :
     * correspondance exacte sur le parking ou la localisation d'attache.
     */
    @Test
    void findByLocalisation_DoitRetournerLesVehiculesDuParking() {
        // ARRANGE — 2 au Parking A, 1 au Parking B
        vehiculeDeServiceRepository.save(creerVehicule("AB-701-AA", Marque.RENAULT,
                Disponibilite.DISPONIBLE, "Parking A"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-702-BB", Marque.PEUGEOT,
                Disponibilite.DISPONIBLE, "Parking A"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-703-CC", Marque.CITROEN,
                Disponibilite.DISPONIBLE, "Parking B"));

        // ACT
        List<VehiculeDeService> result = vehiculeDeServiceRepository.findByLocalisation("Parking A");

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> "Parking A".equals(v.getLocalisation())));
    }

    /**
     * Vérifie {@link VehiculeDeServiceRepository#findByStatut(Disponibilite)} :
     * sépare les véhicules selon leur disponibilité technique.
     */
    @Test
    void findByStatut_DoitFiltrerParDisponibilite() {
        // ARRANGE — 2 DISPONIBLE, 1 EN_REPARATION
        vehiculeDeServiceRepository.save(creerVehicule("AB-704-DD", Marque.RENAULT,
                Disponibilite.DISPONIBLE, "Parking A"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-705-EE", Marque.RENAULT,
                Disponibilite.DISPONIBLE, "Parking B"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-706-FF", Marque.RENAULT,
                Disponibilite.EN_REPARATION, "Atelier"));

        // ACT & ASSERT
        List<VehiculeDeService> disponibles = vehiculeDeServiceRepository.findByStatut(Disponibilite.DISPONIBLE);
        List<VehiculeDeService> enReparation = vehiculeDeServiceRepository.findByStatut(Disponibilite.EN_REPARATION);

        assertEquals(2, disponibles.size());
        assertTrue(disponibles.stream().allMatch(v -> v.getDisponibilite() == Disponibilite.DISPONIBLE));
        assertEquals(1, enReparation.size());
        assertEquals("AB-706-FF", enReparation.get(0).getImmatriculation());
    }

    /**
     * Vérifie {@link VehiculeDeServiceRepository#findByImmatriculationContainingIgnoreCase(String)} :
     * recherche partielle insensible à la casse sur la plaque.
     */
    @Test
    void findByImmatriculationContainingIgnoreCase_DoitRetournerLesPlaquesCorrespondantes() {
        // ARRANGE
        vehiculeDeServiceRepository.save(creerVehicule("AB-801-GG", Marque.RENAULT,
                Disponibilite.DISPONIBLE, "Parking A"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-802-HH", Marque.PEUGEOT,
                Disponibilite.DISPONIBLE, "Parking A"));
        vehiculeDeServiceRepository.save(creerVehicule("CD-900-II", Marque.TOYOTA,
                Disponibilite.DISPONIBLE, "Parking B"));

        // ACT — fragment en minuscules
        List<VehiculeDeService> result = vehiculeDeServiceRepository
                .findByImmatriculationContainingIgnoreCase("ab-80");

        // ASSERT — AB-801 et AB-802, pas CD-900
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getImmatriculation().toUpperCase().contains("AB-80")));
    }

    /**
     * Vérifie {@link VehiculeDeServiceRepository#findByMarque(Marque)} :
     * filtre les véhicules par constructeur.
     */
    @Test
    void findByMarque_DoitRetournerLesVehiculesDeLaMarque() {
        // ARRANGE — 2 RENAULT, 1 PEUGEOT
        vehiculeDeServiceRepository.save(creerVehicule("AB-901-JJ", Marque.RENAULT,
                Disponibilite.DISPONIBLE, "Parking A"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-902-KK", Marque.RENAULT,
                Disponibilite.DISPONIBLE, "Parking B"));
        vehiculeDeServiceRepository.save(creerVehicule("AB-903-LL", Marque.PEUGEOT,
                Disponibilite.DISPONIBLE, "Parking A"));

        // ACT
        List<VehiculeDeService> result = vehiculeDeServiceRepository.findByMarque(Marque.RENAULT);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.getMarque() == Marque.RENAULT));
    }

    private VehiculeDeService creerVehicule(
            String immatriculation,
            Marque marque,
            Disponibilite statut,
            String localisation) {
        VehiculeDeService v = new VehiculeDeService();
        v.setImmatriculation(immatriculation);
        v.setMarque(marque);
        v.setMotorisation(Motorisation.ESSENCE);
        v.setNbPlace(5);
        v.setCategorie(Categorie.BERLINE);
        v.setDisponibilite(statut);
        v.setLocalisation(localisation);
        return v;
    }
}
