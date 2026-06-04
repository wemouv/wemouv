package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.ReservationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UtilisateurService utilisateurService;
    private final VehiculeRepository vehiculeRepository;

    public ReservationService(ReservationRepository reservationRepository, UtilisateurService utilisateurService, VehiculeRepository vehiculeRepository) {
        this.reservationRepository = reservationRepository;
        this.utilisateurService = utilisateurService;
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
    }

    public Reservation create(
            ReservationRequest request,
            String email) {

        Utilisateur utilisateur =
                utilisateurService
                        .findByEmail(email);

        Vehicule vehicule =
                vehiculeRepository
                        .findById(
                                request.getVehiculeId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicule introuvable"));

        // vérifier disponibilité
        boolean indisponible =
                reservationRepository
                        .findByVehiculeId(
                                vehicule.getId())
                        .stream()
                        .anyMatch(r ->
                                r.getDateDebut()
                                        .isBefore(
                                                request.getDateFin())
                                        &&
                                        r.getDateFin()
                                                .isAfter(
                                                        request.getDateDebut())
                        );

        if (indisponible) {
            throw new RuntimeException(
                    "Vehicule indisponible");
        }

        Reservation reservation =
                new Reservation();

        reservation.setDateDebut(
                request.getDateDebut());

        reservation.setDateFin(
                request.getDateFin());

        reservation.setVehicule(
                vehicule);

        reservation.setUtilisateur(
                utilisateur);

        reservation.setStatut(
                Statut.CONFIRME);

        return reservationRepository
                .save(reservation);
    }

    public Reservation annuler(Long id) {

        Reservation reservation =
                findById(id);

        reservation.setStatut(
                Statut.ANNULE);

        return reservationRepository
                .save(reservation);
    }

    public Reservation confirmer(Long id) {

        Reservation reservation =
                findById(id);

        reservation.setStatut(
                Statut.CONFIRME);

        return reservationRepository
                .save(reservation);
    }

    public Reservation update(Long id,Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> findByUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    public List<Reservation> findByVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId);
    }
}

