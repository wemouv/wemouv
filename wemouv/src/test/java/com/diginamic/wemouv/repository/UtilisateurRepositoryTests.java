package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour {@link UtilisateurRepository}.
 * <p>
 * Vérifie les requêtes Spring Data sur les collaborateurs :
 * recherche par e-mail, statut de compte, nom, prénom et recherche partielle insensible à la casse.
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class UtilisateurRepositoryTests {

    @Autowired private UtilisateurRepository utilisateurRepository;

    /**
     * Vérifie {@link UtilisateurRepository#findByEmail(String)} :
     * retourne l'utilisateur correspondant ou un Optional vide.
     */
    @Test
    void findByEmail_DoitRetournerLUtilisateurOuOptionalVide() {
        // ARRANGE
        Utilisateur user = utilisateurRepository.save(creerUtilisateur(
                "Dupont", "Jean", "jean.dupont@test.com", true));

        // ACT & ASSERT — e-mail existant
        Optional<Utilisateur> trouve = utilisateurRepository.findByEmail("jean.dupont@test.com");
        assertTrue(trouve.isPresent());
        assertEquals(user.getId(), trouve.get().getId());

        // ACT & ASSERT — e-mail inconnu
        assertTrue(utilisateurRepository.findByEmail("inconnu@test.com").isEmpty());
    }

    /**
     * Vérifie {@link UtilisateurRepository#findByCompteActif(Boolean)} :
     * sépare les comptes actifs des comptes désactivés.
     */
    @Test
    void findByCompteActif_DoitFiltrerParStatut() {
        // ARRANGE — 2 actifs, 1 inactif
        utilisateurRepository.save(creerUtilisateur("Martin", "Alice", "alice@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Martin", "Bob", "bob@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Martin", "Charlie", "charlie@test.com", false));

        // ACT & ASSERT
        List<Utilisateur> actifs = utilisateurRepository.findByCompteActif(true);
        List<Utilisateur> inactifs = utilisateurRepository.findByCompteActif(false);

        assertEquals(2, actifs.size());
        assertTrue(actifs.stream().allMatch(u -> Boolean.TRUE.equals(u.getCompteActif())));
        assertEquals(1, inactifs.size());
        assertEquals("charlie@test.com", inactifs.get(0).getEmail());
    }

    /**
     * Vérifie {@link UtilisateurRepository#findByNom(String)} :
     * correspondance exacte sur le nom de famille.
     */
    @Test
    void findByNom_DoitRetournerLesUtilisateursDuMemeNom() {
        // ARRANGE — 2 Dupont, 1 Durand
        utilisateurRepository.save(creerUtilisateur("Dupont", "Jean", "jean@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Dupont", "Marie", "marie@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Durand", "Paul", "paul@test.com", true));

        // ACT
        List<Utilisateur> result = utilisateurRepository.findByNom("Dupont");

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> "Dupont".equals(u.getNom())));
    }

    /**
     * Vérifie {@link UtilisateurRepository#findByPrenom(String)} :
     * correspondance exacte sur le prénom.
     */
    @Test
    void findByPrenom_DoitRetournerLesUtilisateursDuMemePrenom() {
        // ARRANGE — 2 prénomés Marie, 1 prénomée Sophie
        utilisateurRepository.save(creerUtilisateur("Dupont", "Marie", "marie.d@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Martin", "Marie", "marie.m@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Durand", "Sophie", "sophie@test.com", true));

        // ACT
        List<Utilisateur> result = utilisateurRepository.findByPrenom("Marie");

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> "Marie".equals(u.getPrenom())));
    }

    /**
     * Vérifie {@link UtilisateurRepository#findByNomContainingIgnoreCase(String)} :
     * recherche partielle insensible à la casse sur le nom.
     */
    @Test
    void findByNomContainingIgnoreCase_DoitRetournerLesNomsCorrespondants() {
        // ARRANGE
        utilisateurRepository.save(creerUtilisateur("Bernard", "Luc", "luc@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Bernardin", "Anne", "anne@test.com", true));
        utilisateurRepository.save(creerUtilisateur("Moreau", "Tom", "tom@test.com", true));

        // ACT — fragment en minuscules
        List<Utilisateur> result = utilisateurRepository.findByNomContainingIgnoreCase("bern");

        // ASSERT — Bernard et Bernardin, pas Moreau
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getNom().toLowerCase().contains("bern")));
    }

    private Utilisateur creerUtilisateur(String nom, String prenom, String email, boolean compteActif) {
        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setMotDePasse("hash");
        u.setRole(Role.USER);
        u.setCompteActif(compteActif);
        return u;
    }
}
