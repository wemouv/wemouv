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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour {@link ParticipationCovoiturageIdRepository}.
 * <p>
 * Ce repository gère la table de jointure {@link ParticipationCovoiturage} avec une
 * clé primaire composite ({@link ParticipationCovoiturageId}). La méthode métier testée
 * ici — {@code existsByUtilisateurIdAndCovoiturageId} — sert à détecter un doublon
 * d'inscription avant réservation (voir {@link com.diginamic.wemouv.service.ReserverCovoiturage}).
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class ParticipationCovoiturageIdRepositoryTests {

    /** Repository testé : existence d'une participation par couple utilisateur / covoiturage. */
    @Autowired private ParticipationCovoiturageIdRepository participationRepository;

    /** Prérequis : organisateur et passager du trajet. */
    @Autowired private UtilisateurRepository utilisateurRepository;

    /** Prérequis : véhicule lié au covoiturage. */
    @Autowired private VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /** Prérequis : trajet auquel le passager s'inscrit. */
    @Autowired private CovoiturageRepository covoiturageRepository;

    /**
     * Vérifie que {@link ParticipationCovoiturageIdRepository#existsByUtilisateurIdAndCovoiturageId(Long, Long)}
     * renvoie {@code true} lorsque la ligne de jointure est bien persistée.
     */
    @Test
    void existsByUtilisateurIdAndCovoiturageId_QuandParticipationExiste_DoitRetournerTrue() {
        // ARRANGE — organisateur, passager, trajet, puis inscription
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga@test.com"));
        Utilisateur passager = utilisateurRepository.save(creerUtilisateur("passager@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-100-AA"));
        Covoiturage covoiturage = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Paris", "Lyon",
                        LocalDateTime.of(2026, 7, 1, 8, 0)));

        participationRepository.save(creerParticipation(passager, covoiturage));

        // ACT
        boolean existe = participationRepository.existsByUtilisateurIdAndCovoiturageId(
                passager.getId(), covoiturage.getId());

        // ASSERT
        assertTrue(existe);
    }

    /**
     * Vérifie que la méthode renvoie {@code false} si aucune participation n'a été enregistrée.
     */
    @Test
    void existsByUtilisateurIdAndCovoiturageId_QuandParticipationAbsente_DoitRetournerFalse() {
        // ARRANGE — trajet créé mais aucune inscription
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga2@test.com"));
        Utilisateur passager = utilisateurRepository.save(creerUtilisateur("passager2@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-200-BB"));
        Covoiturage covoiturage = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Nantes", "Bordeaux",
                        LocalDateTime.of(2026, 8, 1, 9, 0)));

        // ACT
        boolean existe = participationRepository.existsByUtilisateurIdAndCovoiturageId(
                passager.getId(), covoiturage.getId());

        // ASSERT
        assertFalse(existe);
    }

    /**
     * Vérifie que la méthode ne confond pas les utilisateurs : un autre passager
     * inscrit au même trajet ne doit pas faire croire que notre utilisateur participe.
     */
    @Test
    void existsByUtilisateurIdAndCovoiturageId_QuandAutreUtilisateurInscrit_DoitRetournerFalse() {
        // ARRANGE — passager A inscrit, on interroge pour passager B
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga3@test.com"));
        Utilisateur passagerA = utilisateurRepository.save(creerUtilisateur("passagerA@test.com"));
        Utilisateur passagerB = utilisateurRepository.save(creerUtilisateur("passagerB@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-300-CC"));
        Covoiturage covoiturage = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Lille", "Strasbourg",
                        LocalDateTime.of(2026, 9, 1, 10, 0)));

        participationRepository.save(creerParticipation(passagerA, covoiturage));

        // ACT — passager B n'est pas inscrit
        boolean existe = participationRepository.existsByUtilisateurIdAndCovoiturageId(
                passagerB.getId(), covoiturage.getId());

        // ASSERT
        assertFalse(existe);
    }

    /**
     * Vérifie que la méthode ne confond pas les trajets : un même passager inscrit
     * à un autre covoiturage ne doit pas compter pour celui interrogé.
     */
    @Test
    void existsByUtilisateurIdAndCovoiturageId_QuandAutreCovoiturage_DoitRetournerFalse() {
        // ARRANGE — passager inscrit au trajet 1, on interroge pour le trajet 2
        Utilisateur organisateur = utilisateurRepository.save(creerUtilisateur("orga4@test.com"));
        Utilisateur passager = utilisateurRepository.save(creerUtilisateur("passager4@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-400-DD"));
        Covoiturage trajet1 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Rennes", "Nantes",
                        LocalDateTime.of(2026, 10, 1, 8, 0)));
        Covoiturage trajet2 = covoiturageRepository.save(
                creerCovoiturage(organisateur, vehicule, "Toulouse", "Montpellier",
                        LocalDateTime.of(2026, 10, 2, 9, 0)));

        participationRepository.save(creerParticipation(passager, trajet1));

        // ACT — inscription sur trajet1 uniquement
        boolean existe = participationRepository.existsByUtilisateurIdAndCovoiturageId(
                passager.getId(), trajet2.getId());

        // ASSERT
        assertFalse(existe);
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

    /**
     * Crée une participation avec clé composite et relations {@code @MapsId},
     * comme dans {@link com.diginamic.wemouv.service.ReserverCovoiturage#reserver(Long, Long)}.
     */
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
