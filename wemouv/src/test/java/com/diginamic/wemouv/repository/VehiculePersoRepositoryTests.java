package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import com.diginamic.wemouv.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour {@link VehiculePersoRepository}.
 * <p>
 * Vérifie les requêtes Spring Data sur les véhicules personnels :
 * recherche par propriétaire (id du {@link Utilisateur}).
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class VehiculePersoRepositoryTests {

    @Autowired private VehiculePersoRepository vehiculePersoRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;

    /**
     * Vérifie {@link VehiculePersoRepository#findByProprietaireId(Long)} :
     * ne renvoie que les véhicules du propriétaire ciblé.
     */
    @Test
    void findByProprietaireId_DoitRetournerUniquementLesVehiculesDuProprietaire() {
        // ARRANGE — owner1 : 2 véhicules ; owner2 : 1 véhicule
        Utilisateur owner1 = utilisateurRepository.save(creerUtilisateur("owner1@test.com"));
        Utilisateur owner2 = utilisateurRepository.save(creerUtilisateur("owner2@test.com"));

        vehiculePersoRepository.save(creerVehiculePerso("PP-101-AA", owner1));
        vehiculePersoRepository.save(creerVehiculePerso("PP-102-BB", owner1));
        vehiculePersoRepository.save(creerVehiculePerso("PP-103-CC", owner2));

        // ACT
        List<VehiculePerso> result = vehiculePersoRepository.findByProprietaireId(owner1.getId());

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(v -> v.getProprietaire().getId().equals(owner1.getId())));
    }

    /**
     * Vérifie {@link VehiculePersoRepository#findByProprietaireId(Long)} :
     * retourne une liste vide si le propriétaire n'a aucun véhicule.
     */
    @Test
    void findByProprietaireId_DoitRetournerUneListeVideSiAucunVehicule() {
        // ARRANGE — owner sans véhicules
        Utilisateur owner = utilisateurRepository.save(creerUtilisateur("no-veh@test.com"));

        // ACT
        List<VehiculePerso> result = vehiculePersoRepository.findByProprietaireId(owner.getId());

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private Utilisateur creerUtilisateur(String email) {
        Utilisateur u = new Utilisateur();
        u.setNom("Nom");
        u.setPrenom("Prenom");
        u.setEmail(email);
        u.setMotDePasse("hash");
        u.setRole(Role.USER);
        u.setCompteActif(true);
        return u;
    }

    private VehiculePerso creerVehiculePerso(String immatriculation, Utilisateur proprietaire) {
        VehiculePerso v = new VehiculePerso();
        v.setImmatriculation(immatriculation);
        v.setMarque(Marque.RENAULT);
        v.setMotorisation(Motorisation.ESSENCE);
        v.setNbPlace(5);
        v.setCategorie(Categorie.BERLINE);
        v.setProprietaire(proprietaire);
        return v;
    }
}
