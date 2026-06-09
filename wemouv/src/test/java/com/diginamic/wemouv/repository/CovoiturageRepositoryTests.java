package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Covoiturage;
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
 * Tests d'intégration pour {@link CovoiturageRepository}.
 * <p>
 * Contrairement aux tests Mockito des controllers, ce fichier charge un contexte JPA
 * réduit via {@code @DataJpaTest} : seuls les repositories et la couche persistence
 * sont initialisés (pas de controllers, pas de sécurité JWT complète).
 * </p>
 * <p>
 * Le profil {@code test} active H2 en mémoire ({@code application-test.properties}) :
 * Hibernate crée le schéma à la volée ({@code ddl-auto=create-drop}), chaque test
 * part d'une base vide grâce au rollback transactionnel de {@code @DataJpaTest}.
 * </p>
 * <p>
 * Chaque scénario persiste d'abord les entités liées obligatoires
 * ({@link Utilisateur}, {@link VehiculeDeService}) puis des {@link Covoiturage},
 * avant d'appeler une méthode "derived query" du repository et de vérifier le résultat.
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class CovoiturageRepositoryTests {

    /** Repository testé : requêtes générées par Spring Data à partir des noms de méthodes. */
    @Autowired private CovoiturageRepository covoiturageRepository;

    /** Dépendance pour créer organisateur et conducteur (clés étrangères du covoiturage). */
    @Autowired private UtilisateurRepository utilisateurRepository;

    /** Dépendance pour créer le véhicule lié au trajet (relation ManyToOne). */
    @Autowired private VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /**
     * Vérifie que {@link CovoiturageRepository#findByOrganisateurId(Long)} ne renvoie
     * que les trajets du bon organisateur.
     */
    @Test
    void findByOrganisateurId_DoitRetournerUniquementLesTrajetsDeLOrganisateur() {
        // ARRANGE — deux organisateurs, un trajet chacun
        Utilisateur orga1 = utilisateurRepository.save(creerUtilisateur("orga1@test.com"));
        Utilisateur orga2 = utilisateurRepository.save(creerUtilisateur("orga2@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-111-AA"));

        covoiturageRepository.save(creerCovoiturage(orga1, vehicule, "Paris", "Lyon",
                LocalDateTime.of(2026, 7, 1, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga2, vehicule, "Nantes", "Bordeaux",
                LocalDateTime.of(2026, 7, 2, 9, 0), Statut.EN_ATTENTE));

        // ACT
        List<Covoiturage> result = covoiturageRepository.findByOrganisateurId(orga1.getId());

        // ASSERT — un seul trajet, celui de orga1
        assertEquals(1, result.size());
        assertEquals("Paris", result.get(0).getAdresseDepart());
    }

    /**
     * Vérifie que {@link CovoiturageRepository#findByStatut(Statut)} sépare bien
     * les annonces selon leur état (EN_ATTENTE vs CONFIRME).
     */
    @Test
    void findByStatut_DoitFiltrerParStatut() {
        // ARRANGE — deux trajets, statuts différents
        Utilisateur orga = utilisateurRepository.save(creerUtilisateur("statut@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-222-BB"));

        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "A", "B",
                LocalDateTime.of(2026, 8, 1, 10, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "C", "D",
                LocalDateTime.of(2026, 8, 2, 11, 0), Statut.CONFIRME));

        // ACT & ASSERT — une ligne par statut
        assertEquals(1, covoiturageRepository.findByStatut(Statut.EN_ATTENTE).size());
        assertEquals(1, covoiturageRepository.findByStatut(Statut.CONFIRME).size());
    }

    /**
     * Vérifie que {@link CovoiturageRepository#findByAdresseDepartContainingIgnoreCase(String)}
     * génère un LIKE insensible à la casse ({@code %PARIS%} doit matcher "Gare du Nord Paris").
     */
    @Test
    void findByAdresseDepartContainingIgnoreCase_DoitIgnorerLaCasse() {
        // ARRANGE — une adresse contenant "Paris", une autre ville sans ce mot
        Utilisateur orga = utilisateurRepository.save(creerUtilisateur("adresse@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-333-CC"));

        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "Gare du Nord Paris", "Lyon",
                LocalDateTime.of(2026, 9, 1, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "Centre Lyon", "Marseille",
                LocalDateTime.of(2026, 9, 2, 9, 0), Statut.EN_ATTENTE));

        // ACT — critère en majuscules
        List<Covoiturage> result = covoiturageRepository.findByAdresseDepartContainingIgnoreCase("PARIS");

        // ASSERT
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAdresseDepart().toLowerCase().contains("paris"));
    }

    /**
     * Vérifie que {@link CovoiturageRepository#findByOrganisateurIdAndStatut(Long, Statut)}
     * applique les deux filtres en même temps (AND).
     */
    @Test
    void findByOrganisateurIdAndStatut_DoitCombinerLesCriteres() {
        // ARRANGE — même organisateur : un EN_ATTENTE, un ANNULE
        Utilisateur orga = utilisateurRepository.save(creerUtilisateur("combo@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-444-DD"));

        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "X", "Y",
                LocalDateTime.of(2026, 10, 1, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "Z", "W",
                LocalDateTime.of(2026, 10, 2, 9, 0), Statut.ANNULE));

        // ACT
        List<Covoiturage> result = covoiturageRepository.findByOrganisateurIdAndStatut(
                orga.getId(), Statut.EN_ATTENTE);

        // ASSERT — le trajet annulé est exclu
        assertEquals(1, result.size());
        assertEquals(Statut.EN_ATTENTE, result.get(0).getStatut());
    }

    /**
     * Vérifie {@link CovoiturageRepository#findByOrganisateurIdAndDateDepartAfterOrderByDateDepartAsc(Long, LocalDateTime)} :
     * trajets strictement après la date de référence ({@code After} = {@code >}, pas {@code >=}),
     * triés du départ le plus proche au plus lointain.
     */
    @Test
    void findByOrganisateurIdAndDateDepartAfterOrderByDateDepartAsc_DoitTrierLesTrajetsFuturs() {
        // ARRANGE — référence 15/06 ; 3 futurs (20/06, 01/07, 20/07) + 1 passé (01/05)
        Utilisateur orga = utilisateurRepository.save(creerUtilisateur("futur@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-555-EE"));
        LocalDateTime reference = LocalDateTime.of(2026, 6, 15, 12, 0);

        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "T3", "U3",
                LocalDateTime.of(2026, 7, 20, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "T1", "U1",
                LocalDateTime.of(2026, 6, 20, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "T2", "U2",
                LocalDateTime.of(2026, 7, 1, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "Passe", "U0",
                LocalDateTime.of(2026, 5, 1, 8, 0), Statut.EN_ATTENTE));

        // ACT
        List<Covoiturage> result = covoiturageRepository
                .findByOrganisateurIdAndDateDepartAfterOrderByDateDepartAsc(orga.getId(), reference);

        // ASSERT — ordre chronologique T1, T2, T3
        assertEquals(3, result.size());
        assertEquals("T1", result.get(0).getAdresseDepart());
        assertEquals("T2", result.get(1).getAdresseDepart());
        assertEquals("T3", result.get(2).getAdresseDepart());
    }

    /**
     * Vérifie {@link CovoiturageRepository#findByOrganisateurIdAndDateDepartBeforeOrderByDateDepartDesc(Long, LocalDateTime)} :
     * historique ({@code Before} = {@code <}) trié du plus récent au plus ancien.
     */
    @Test
    void findByOrganisateurIdAndDateDepartBeforeOrderByDateDepartDesc_DoitTrierLHistorique() {
        // ARRANGE — référence 15/06 ; deux passés + un futur à exclure
        Utilisateur orga = utilisateurRepository.save(creerUtilisateur("hist@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-666-FF"));
        LocalDateTime reference = LocalDateTime.of(2026, 6, 15, 12, 0);

        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "H1", "I1",
                LocalDateTime.of(2026, 5, 1, 8, 0), Statut.TERMINE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "H2", "I2",
                LocalDateTime.of(2026, 6, 1, 8, 0), Statut.TERMINE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule, "Futur", "I3",
                LocalDateTime.of(2026, 8, 1, 8, 0), Statut.EN_ATTENTE));

        // ACT
        List<Covoiturage> result = covoiturageRepository
                .findByOrganisateurIdAndDateDepartBeforeOrderByDateDepartDesc(orga.getId(), reference);

        // ASSERT — H2 (01/06) avant H1 (01/05) en ordre décroissant
        assertEquals(2, result.size());
        assertEquals("H2", result.get(0).getAdresseDepart());
        assertEquals("H1", result.get(1).getAdresseDepart());
    }

    /**
     * Vérifie {@link CovoiturageRepository#findByVehiculeAndDateDepartAfter(com.diginamic.wemouv.entity.Vehicule, LocalDateTime)} :
     * utile pour annuler en cascade les trajets futurs d'un véhicule en panne
     * (filtre sur l'entité véhicule, pas seulement son id).
     */
    @Test
    void findByVehiculeAndDateDepartAfter_DoitFiltrerParVehiculeEtDate() {
        // ARRANGE — deux véhicules ; pour vehicule1 : un futur + un passé ; vehicule2 ignoré
        Utilisateur orga = utilisateurRepository.save(creerUtilisateur("veh@test.com"));
        VehiculeDeService vehicule1 = vehiculeDeServiceRepository.save(creerVehicule("AB-777-GG"));
        VehiculeDeService vehicule2 = vehiculeDeServiceRepository.save(creerVehicule("AB-888-HH"));
        LocalDateTime reference = LocalDateTime.of(2026, 6, 10, 0, 0);

        covoiturageRepository.save(creerCovoiturage(orga, vehicule1, "V1", "W1",
                LocalDateTime.of(2026, 7, 1, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule2, "V2", "W2",
                LocalDateTime.of(2026, 7, 2, 8, 0), Statut.EN_ATTENTE));
        covoiturageRepository.save(creerCovoiturage(orga, vehicule1, "V1-passe", "W0",
                LocalDateTime.of(2026, 5, 1, 8, 0), Statut.EN_ATTENTE));

        // ACT
        List<Covoiturage> result = covoiturageRepository.findByVehiculeAndDateDepartAfter(vehicule1, reference);

        // ASSERT — seul le trajet futur du bon véhicule
        assertEquals(1, result.size());
        assertEquals("V1", result.get(0).getAdresseDepart());
    }

    /**
     * Fabrique un utilisateur minimal valide pour les contraintes JPA (email unique par test).
     */
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

    /**
     * Fabrique un véhicule de service avec des valeurs d'enum fixes (évite les null sur champs obligatoires).
     */
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

    /**
     * Fabrique un covoiturage lié à un organisateur et un véhicule déjà persistés.
     * <p>
     * Les libellés {@code depart} / {@code arrivee} servent aussi d'identifiants lisibles
     * dans les assertions (ex. "T1", "H2", "V1-passe").
     * Organisateur et conducteur sont le même utilisateur, comme dans les cas métier simples.
     * </p>
     */
    private Covoiturage creerCovoiturage(
            Utilisateur organisateur,
            VehiculeDeService vehicule,
            String depart,
            String arrivee,
            LocalDateTime dateDepart,
            Statut statut) {
        Covoiturage c = new Covoiturage();
        c.setAdresseDepart(depart);
        c.setAdresseArrive(arrivee);
        c.setDateDepart(dateDepart);
        c.setDateCreation(LocalDateTime.of(2026, 1, 1, 0, 0));
        c.setNbPlacesInitial(3);
        c.setNbPlacesRestantes(2);
        c.setStatut(statut);
        c.setVehicule(vehicule);
        c.setOrganisateur(organisateur);
        c.setConducteur(organisateur);
        return c;
    }
}
