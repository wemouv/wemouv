package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import com.diginamic.wemouv.enums.Role;
import com.diginamic.wemouv.enums.Statut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour {@link ParticipationCovoiturageRepository}.
 * <p>
 * Ce repository expose des requêtes métier sur la table de jointure
 * {@link ParticipationCovoiturage} : trajets futurs / passés d'un passager
 * (via jointure implicite sur {@code covoiturage.dateDepart}) et suppression
 * en cascade par covoiturage.
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class ParticipationCovoiturageRepositoryTests {

    /** Repository testé : recherches métier et suppression par covoiturage. */
    @Autowired private ParticipationCovoiturageRepository participationRepository;

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private VehiculeDeServiceRepository vehiculeDeServiceRepository;
    @Autowired private CovoiturageRepository covoiturageRepository;

    /**
     * Vérifie {@link ParticipationCovoiturageRepository#findByUtilisateurIdAndCovoiturageDateDepartAfter(Long, LocalDateTime)} :
     * jointure participation → covoiturage, filtre {@code After} ({@code >}) et par passager.
     */
    @Test
    void findByUtilisateurIdAndCovoiturageDateDepartAfter_DoitRetournerLesTrajetsFutursDuPassager() {
        // ARRANGE — référence 15/06 ; passager1 : 2 futurs + 1 passé ; passager2 : 1 futur ignoré
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga-futur@test.com"));
        Utilisateur passager1 = utilisateurRepository.save(creerUtilisateur("passager1@test.com"));
        Utilisateur passager2 = utilisateurRepository.save(creerUtilisateur("passager2@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-501-AA"));
        LocalDateTime reference = LocalDateTime.of(2026, 6, 15, 12, 0);

        Covoiturage futur1 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "F1", "Lyon",
                        LocalDateTime.of(2026, 6, 20, 8, 0)));
        Covoiturage futur2 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "F2", "Marseille",
                        LocalDateTime.of(2026, 7, 1, 9, 0)));
        Covoiturage passe = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Passe", "Nantes",
                        LocalDateTime.of(2026, 5, 1, 8, 0)));
        Covoiturage futurAutrePassager = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Autre", "Bordeaux",
                        LocalDateTime.of(2026, 8, 1, 10, 0)));

        participationRepository.save(creerParticipation(passager1, futur1));
        participationRepository.save(creerParticipation(passager1, futur2));
        participationRepository.save(creerParticipation(passager1, passe));
        participationRepository.save(creerParticipation(passager2, futurAutrePassager));

        // ACT
        List<ParticipationCovoiturage> result = participationRepository
                .findByUtilisateurIdAndCovoiturageDateDepartAfter(passager1.getId(), reference);

        // ASSERT — uniquement F1 et F2 pour passager1
        assertEquals(2, result.size());
        assertEquals("F1", result.get(0).getCovoiturage().getAdresseDepart());
        assertEquals("F2", result.get(1).getCovoiturage().getAdresseDepart());
    }

    /**
     * Vérifie {@link ParticipationCovoiturageRepository#findByUtilisateurIdAndCovoiturageDateDepartBefore(Long, LocalDateTime)} :
     * historique passager ({@code Before} = {@code <}), futurs exclus.
     */
    @Test
    void findByUtilisateurIdAndCovoiturageDateDepartBefore_DoitRetournerLHistoriqueDuPassager() {
        // ARRANGE — référence 15/06 ; 2 passés + 1 futur
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga-hist@test.com"));
        Utilisateur passager = utilisateurRepository.save(creerUtilisateur("passager-hist@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-502-BB"));
        LocalDateTime reference = LocalDateTime.of(2026, 6, 15, 12, 0);

        Covoiturage h1 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "H1", "Lyon",
                        LocalDateTime.of(2026, 5, 1, 8, 0)));
        Covoiturage h2 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "H2", "Marseille",
                        LocalDateTime.of(2026, 6, 1, 9, 0)));
        Covoiturage futur = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Futur", "Nantes",
                        LocalDateTime.of(2026, 8, 1, 10, 0)));

        participationRepository.save(creerParticipation(passager, h1));
        participationRepository.save(creerParticipation(passager, h2));
        participationRepository.save(creerParticipation(passager, futur));

        // ACT
        List<ParticipationCovoiturage> result = participationRepository
                .findByUtilisateurIdAndCovoiturageDateDepartBefore(passager.getId(), reference);

        // ASSERT — H1 et H2 uniquement
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .map(p -> p.getCovoiturage().getAdresseDepart())
                .toList()
                .containsAll(List.of("H1", "H2")));
    }

    /**
     * Vérifie {@link ParticipationCovoiturageRepository#deleteByCovoiturageId(Long)} :
     * supprime toutes les participations d'un trajet sans toucher aux autres.
     */
    @Test
    void deleteByCovoiturageId_DoitSupprimerUniquementLesParticipationsDuTrajet() {
        // ARRANGE — trajet1 : 2 passagers ; trajet2 : 1 passager
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga-del@test.com"));
        Utilisateur passager1 = utilisateurRepository.save(creerUtilisateur("passager-del1@test.com"));
        Utilisateur passager2 = utilisateurRepository.save(creerUtilisateur("passager-del2@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-503-CC"));

        Covoiturage trajet1 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "T1", "Lyon",
                        LocalDateTime.of(2026, 7, 1, 8, 0)));
        Covoiturage trajet2 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "T2", "Marseille",
                        LocalDateTime.of(2026, 7, 2, 9, 0)));

        participationRepository.save(creerParticipation(passager1, trajet1));
        participationRepository.save(creerParticipation(passager2, trajet1));
        participationRepository.save(creerParticipation(passager1, trajet2));

        // ACT — suppression ciblée sur trajet1
        participationRepository.deleteByCovoiturageId(trajet1.getId());

        // ASSERT — participations trajet1 supprimées, trajet2 conservé
        assertFalse(participationRepository.existsById(
                new ParticipationCovoiturageId(passager1.getId(), trajet1.getId())));
        assertFalse(participationRepository.existsById(
                new ParticipationCovoiturageId(passager2.getId(), trajet1.getId())));
        assertTrue(participationRepository.existsById(
                new ParticipationCovoiturageId(passager1.getId(), trajet2.getId())));
        assertEquals(1, participationRepository.count());
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

    private VehiculeDeService creerVehicule(String immatriculation) {
        VehiculeDeService v = new VehiculeDeService();
        v.setImmatriculation(immatriculation);
        v.setMarque(Marque.RENAULT);
        v.setMotorisation(Motorisation.ESSENCE);
        v.setNbPlace(5);
        v.setCategorie(Categorie.BERLINE);
        v.setStatut(Disponibilite.DISPONIBLE);
        v.setLocalisation("Parking A");
        return v;
    }

    private Covoiturage creerCovoiturage(
            Utilisateur organisateur,
            VehiculeDeService vehicule,
            String depart,
            String arrivee,
            LocalDateTime dateDepart) {
        Covoiturage c = new Covoiturage();
        c.setAdresseDepart(depart);
        c.setAdresseArrive(arrivee);
        c.setDateDepart(dateDepart);
        c.setDateCreation(LocalDateTime.of(2026, 1, 1, 0, 0));
        c.setNbPlacesInitial(3);
        c.setNbPlacesRestantes(2);
        c.setStatut(Statut.OUVERT);
        c.setVehicule(vehicule);
        c.setOrganisateur(organisateur);
        c.setConducteur(organisateur);
        return c;
    }

    private ParticipationCovoiturage creerParticipation(Utilisateur passager, Covoiturage covoiturage) {
        ParticipationCovoiturageId cle = new ParticipationCovoiturageId(
                passager.getId(), covoiturage.getId());
        ParticipationCovoiturage participation = new ParticipationCovoiturage();
        participation.setId(cle);
        participation.setUtilisateur(passager);
        participation.setCovoiturage(covoiturage);
        return participation;
    }
}
