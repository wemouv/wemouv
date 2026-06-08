package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.Reservation;
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
 * Tests d'intégration pour {@link ReservationRepository}.
 * <p>
 * Vérifie les requêtes Spring Data sur les emprunts de véhicules de service :
 * filtrage par utilisateur, véhicule, statut et période de début.
 * </p>
 */
@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTests {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /**
     * Vérifie {@link ReservationRepository#findByUtilisateurId(Long)} :
     * ne renvoie que les réservations de l'emprunteur ciblé.
     */
    @Test
    void findByUtilisateurId_DoitRetournerUniquementLesReservationsDeLUtilisateur() {
        // ARRANGE — user1 : 2 réservations ; user2 : 1 réservation
        Utilisateur user1 = utilisateurRepository.save(creerUtilisateur("user1@test.com"));
        Utilisateur user2 = utilisateurRepository.save(creerUtilisateur("user2@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-601-AA"));

        reservationRepository.save(creerReservation(user1, vehicule,
                LocalDateTime.of(2026, 7, 1, 8, 0), LocalDateTime.of(2026, 7, 3, 18, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user1, vehicule,
                LocalDateTime.of(2026, 8, 1, 9, 0), LocalDateTime.of(2026, 8, 2, 17, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user2, vehicule,
                LocalDateTime.of(2026, 9, 1, 10, 0), LocalDateTime.of(2026, 9, 2, 16, 0), Statut.CONFIRME));

        // ACT
        List<Reservation> result = reservationRepository.findByUtilisateurId(user1.getId());

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getUtilisateur().getId().equals(user1.getId())));
    }

    /**
     * Vérifie {@link ReservationRepository#findByVehiculeId(Long)} :
     * ne renvoie que les réservations du véhicule ciblé.
     */
    @Test
    void findByVehiculeId_DoitRetournerUniquementLesReservationsDuVehicule() {
        // ARRANGE — vehicule1 : 2 réservations ; vehicule2 : 1 réservation
        Utilisateur user = utilisateurRepository.save(creerUtilisateur("veh-user@test.com"));
        VehiculeDeService vehicule1 = vehiculeDeServiceRepository.save(creerVehicule("AB-602-BB"));
        VehiculeDeService vehicule2 = vehiculeDeServiceRepository.save(creerVehicule("AB-603-CC"));

        reservationRepository.save(creerReservation(user, vehicule1,
                LocalDateTime.of(2026, 7, 5, 8, 0), LocalDateTime.of(2026, 7, 6, 18, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user, vehicule1,
                LocalDateTime.of(2026, 8, 5, 9, 0), LocalDateTime.of(2026, 8, 6, 17, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user, vehicule2,
                LocalDateTime.of(2026, 9, 5, 10, 0), LocalDateTime.of(2026, 9, 6, 16, 0), Statut.CONFIRME));

        // ACT
        List<Reservation> result = reservationRepository.findByVehiculeId(vehicule1.getId());

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getVehicule().getId().equals(vehicule1.getId())));
    }

    /**
     * Vérifie {@link ReservationRepository#findByStatut(Statut)} :
     * sépare les réservations selon leur état.
     */
    @Test
    void findByStatut_DoitFiltrerParStatut() {
        // ARRANGE — une CONFIRME, une ANNULE
        Utilisateur user = utilisateurRepository.save(creerUtilisateur("statut-res@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-604-DD"));

        reservationRepository.save(creerReservation(user, vehicule,
                LocalDateTime.of(2026, 10, 1, 8, 0), LocalDateTime.of(2026, 10, 2, 18, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user, vehicule,
                LocalDateTime.of(2026, 10, 3, 9, 0), LocalDateTime.of(2026, 10, 4, 17, 0), Statut.ANNULE));

        // ACT & ASSERT
        assertEquals(1, reservationRepository.findByStatut(Statut.CONFIRME).size());
        assertEquals(1, reservationRepository.findByStatut(Statut.ANNULE).size());
    }

    /**
     * Vérifie {@link ReservationRepository#findByDateDebut(LocalDateTime)} :
     * correspondance exacte sur la date/heure de début.
     */
    @Test
    void findByDateDebut_DoitMatcherLaDateExacte() {
        // ARRANGE — deux débuts différents
        Utilisateur user = utilisateurRepository.save(creerUtilisateur("date-exact@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-605-EE"));
        LocalDateTime debutCible = LocalDateTime.of(2026, 11, 10, 14, 30);

        reservationRepository.save(creerReservation(user, vehicule,
                debutCible, LocalDateTime.of(2026, 11, 12, 18, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user, vehicule,
                LocalDateTime.of(2026, 11, 11, 8, 0), LocalDateTime.of(2026, 11, 13, 17, 0), Statut.CONFIRME));

        // ACT
        List<Reservation> result = reservationRepository.findByDateDebut(debutCible);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(debutCible, result.get(0).getDateDebut());
    }

    /**
     * Vérifie {@link ReservationRepository#findByDateDebutBetween(LocalDateTime, LocalDateTime)} :
     * intervalle inclusif {@code BETWEEN debut AND fin} sur {@code dateDebut}.
     */
    @Test
    void findByDateDebutBetween_DoitRetournerLesReservationsDansLIntervalle() {
        // ARRANGE — 3 réservations : avant, dans, après l'intervalle [10/01, 20/01]
        Utilisateur user = utilisateurRepository.save(creerUtilisateur("between@test.com"));
        VehiculeDeService vehicule = vehiculeDeServiceRepository.save(creerVehicule("AB-606-FF"));
        LocalDateTime intervalleDebut = LocalDateTime.of(2026, 1, 10, 0, 0);
        LocalDateTime intervalleFin = LocalDateTime.of(2026, 1, 20, 23, 59);

        reservationRepository.save(creerReservation(user, vehicule,
                LocalDateTime.of(2026, 1, 5, 8, 0), LocalDateTime.of(2026, 1, 6, 18, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user, vehicule,
                LocalDateTime.of(2026, 1, 15, 9, 0), LocalDateTime.of(2026, 1, 16, 17, 0), Statut.CONFIRME));
        reservationRepository.save(creerReservation(user, vehicule,
                LocalDateTime.of(2026, 1, 25, 10, 0), LocalDateTime.of(2026, 1, 26, 16, 0), Statut.CONFIRME));

        // ACT
        List<Reservation> result = reservationRepository.findByDateDebutBetween(intervalleDebut, intervalleFin);

        // ASSERT — uniquement celle du 15/01
        assertEquals(1, result.size());
        assertEquals(LocalDateTime.of(2026, 1, 15, 9, 0), result.get(0).getDateDebut());
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

    private Reservation creerReservation(
            Utilisateur utilisateur,
            VehiculeDeService vehicule,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Statut statut) {
        Reservation r = new Reservation();
        r.setUtilisateur(utilisateur);
        r.setVehicule(vehicule);
        r.setDateDebut(dateDebut);
        r.setDateFin(dateFin);
        r.setStatut(statut);
        return r;
    }
}
