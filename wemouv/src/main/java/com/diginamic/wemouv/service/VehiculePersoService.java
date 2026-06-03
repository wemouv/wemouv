package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.VehiculePersoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehiculePersoService {

    private final VehiculePersoRepository vehiculePersoRepository;
    private final CovoiturageRepository covoiturageRepository;

    public VehiculePersoService(VehiculePersoRepository vehiculePersoRepository, CovoiturageRepository covoiturageRepository) {
        this.vehiculePersoRepository = vehiculePersoRepository;
        this.covoiturageRepository = covoiturageRepository;
    }

    public List<VehiculePerso> findAll() {
        return vehiculePersoRepository.findAll();
    }

    public VehiculePerso findById(Long id) {
        return vehiculePersoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule perso introuvable"));
    }

    public VehiculePerso create(VehiculePerso vehiculePerso) {

        return vehiculePersoRepository.save(vehiculePerso);
    }

    public VehiculePerso update(Long id, VehiculePerso vehiculePerso) {
        VehiculePerso existing = findById(id);

        existing.setImmatriculation(vehiculePerso.getImmatriculation());
        existing.setMarque(vehiculePerso.getMarque());
        existing.setMotorisation(vehiculePerso.getMotorisation());
        existing.setNbPlace(vehiculePerso.getNbPlace());
        existing.setPhotoUrl(vehiculePerso.getPhotoUrl());
        existing.setCo2Km(vehiculePerso.getCo2Km());
        existing.setCategorie(vehiculePerso.getCategorie());
        if(vehiculePerso.getProprietaire()!= null){existing.setProprietaire(vehiculePerso.getProprietaire());};

        return vehiculePersoRepository.save(existing);
    }

    public void delete(Long id) {
        vehiculePersoRepository.deleteById(id);
    }

    public List<VehiculePerso> findByProprietaire(Long proprietaireId) {
        return vehiculePersoRepository.findByProprietaireId(proprietaireId);
    }

    /**
     * Recherche les véhicules personnels disponibles sur une plage de dates.
     *
     * <p>
     * Un véhicule est considéré comme disponible lorsqu'il n'est associé
     * à aucun covoiturage dont la période chevauche la plage demandée.
     * </p>
     *
     * <p>
     * Le chevauchement est détecté selon la règle suivante :
     * <ul>
     *     <li>dateDepartCovoiturage &lt; dateFinDemandee</li>
     *     <li>et dateFinCovoiturage &gt; dateDebutDemandee</li>
     * </ul>
     * </p>
     *
     * <p>
     * La date de fin du covoiturage est calculée à partir de sa date de départ
     * et de sa durée estimée.
     * </p>
     *
     * @param dateDebut date et heure de début de la période recherchée
     * @param dateFin date et heure de fin de la période recherchée
     * @return la liste des véhicules personnels disponibles sur la période demandée
     */
    public List<VehiculePerso> findAllAvailable(
            LocalDateTime dateDebut,
            LocalDateTime dateFin) {

        return vehiculePersoRepository.findAll()
                .stream()

                // aucun covoiturage sur la période
                .filter(v -> {

                    List<Covoiturage> covoiturages =
                            covoiturageRepository
                                    .findByVehiculeId(
                                            v.getId());

                    return covoiturages.stream()
                            .noneMatch(c -> {

                                LocalDateTime dateFinCovoit =
                                        c.getDateDepart()
                                                .plusMinutes(
                                                        (long)
                                                                (c.getDureeTrajet() * 60));

                                return c.getDateDepart()
                                        .isBefore(dateFin)
                                        &&
                                        dateFinCovoit
                                                .isAfter(dateDebut);
                            });
                })

                .toList();
    }
}
