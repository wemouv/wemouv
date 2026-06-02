package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
    }

    public Reservation create(Reservation reservation) {
        return reservationRepository.save(reservation);
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

