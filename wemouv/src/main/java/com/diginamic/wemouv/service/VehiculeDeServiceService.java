package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.*;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeDeServiceRepository;
import org.apache.catalina.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehiculeDeServiceService {

    private final VehiculeDeServiceRepository vehiculeDeServiceRepository;
    private final CovoiturageRepository covoiturageRepository;
    private final ReservationRepository reservationRepository;
    private final EmailService emailService;

    public VehiculeDeServiceService(VehiculeDeServiceRepository vehiculeDeServiceRepository,
                                    CovoiturageRepository covoiturageRepository,
                                    ReservationRepository reservationRepository,
                                    EmailService emailService
    ) {
        this.vehiculeDeServiceRepository = vehiculeDeServiceRepository;
        this.covoiturageRepository =covoiturageRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    public List<VehiculeDeService> findAll() {
        return vehiculeDeServiceRepository.findAll();
    }

    public VehiculeDeService findById(Long id) {
        return vehiculeDeServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule de service introuvable"));
    }


    /**
     * Recherche les véhicules de service disponibles sur une plage de dates.
     *
     * <p>
     * Un véhicule est considéré comme disponible lorsqu'il :
     * <ul>
     *     <li>est marqué comme DISPONIBLE</li>
     *     <li>ne possède aucune réservation chevauchant la période demandée</li>
     * </ul>
     * </p>
     *
     * <p>
     * Deux périodes sont considérées comme chevauchantes lorsque :
     * <ul>
     *     <li>dateDebutReservation &lt; dateFinDemandee</li>
     *     <li>et dateFinReservation &gt; dateDebutDemandee</li>
     * </ul>
     * </p>
     *
     * @param dateDebut date et heure de début recherchées
     * @param dateFin date et heure de fin recherchées
     * @return la liste des véhicules de service disponibles
     */
    public List<VehiculeDeService> findAllAvailable(
            LocalDateTime dateDebut,
            LocalDateTime dateFin) {

        return vehiculeDeServiceRepository.findAll()
                .stream()

                // véhicule disponible
                .filter(v ->
                        v.getStatut()
                                == Disponibilite.DISPONIBLE)

                // aucune réservation sur la période
                .filter(v -> {

                    List<Reservation> reservations =
                            reservationRepository
                                    .findByVehiculeId(
                                            v.getId());

                    return reservations.stream()

                            // aucun chevauchement
                            .noneMatch(r ->
                                    r.getDateDebut()
                                            .isBefore(dateFin)
                                            &&
                                            r.getDateFin()
                                                    .isAfter(dateDebut)
                            );
                })

                .toList();
    }


    public VehiculeDeService create(
            VehiculeDeService vehicule
    ) {


        vehicule.setStatut(
                Disponibilite.DISPONIBLE
        );


        return vehiculeDeServiceRepository.save(vehicule);
    }


    @Transactional
    public VehiculeDeService update(
            Long id,
            VehiculeDeService details
    ) {

        VehiculeDeService vehicule =
                vehiculeDeServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicule introuvable"
                                ));

        Disponibilite ancienneDispo =
                vehicule.getStatut();

        Disponibilite nouvelleDispo =
                details.getStatut();

        // update fiche
        vehicule.setImmatriculation(
                details.getImmatriculation()
        );
        vehicule.setMarque(
                details.getMarque()
        );
        vehicule.setMotorisation(
                details.getMotorisation()
        );
        vehicule.setNbPlace(
                details.getNbPlace()
        );
        vehicule.setPhotoUrl(
                details.getPhotoUrl()
        );
        vehicule.setCo2Km(
                details.getCo2Km()
        );
        vehicule.setCategorie(
                details.getCategorie()
        );

        // update statut
        vehicule.setStatut(
                nouvelleDispo
        );

        VehiculeDeService saved =
                vehiculeDeServiceRepository.save(
                        vehicule
                );

        // règle critique
        if (
                ancienneDispo != nouvelleDispo
                        &&
                        (
                                nouvelleDispo ==
                                        Disponibilite.EN_REPARATION
                                        ||
                                        nouvelleDispo ==
                                                Disponibilite.HORS_SERVICE
                        )
        ) {

            annulerTrajetsFuturs(saved);
        }

        return saved;
    }

    private void annulerTrajetsFuturs(
            Vehicule vehicule
    ) {

        List<Covoiturage> covoiturages =
                covoiturageRepository
                        .findByVehiculeAndDateDepartAfter(
                                vehicule,
                                LocalDateTime.now()
                        );

        for (
                Covoiturage c
                : covoiturages
        ) {

            c.setStatut(
                    Statut.ANNULE
            );

            covoiturageRepository.save(c);

            notifierImpactes(c);
        }
    }

    private void notifierImpactes(
            Covoiturage c
    ) {

        // organisateur
        emailService.sendMail(
                c.getOrganisateur()
                        .getEmail(),
                "Covoiturage annulé",
                "Le trajet du "
                        + c.getDateDepart()
                        + " a été annulé car le véhicule est indisponible."
        );

        // passagers
        for (
                ParticipationCovoiturage p
                : c.getParticipations()
        ) {

            emailService.sendMail(
                    p.getUtilisateur()
                            .getEmail(),
                    "Covoiturage annulé",
                    "Le trajet auquel vous participiez a été annulé."
            );
        }
    }


    public void delete(Long id) {
        vehiculeDeServiceRepository.deleteById(id);
    }



}
