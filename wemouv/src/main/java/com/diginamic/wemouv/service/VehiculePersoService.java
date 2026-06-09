package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.VehiculePersoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier gérant la logique des véhicules personnels des collaborateurs.
 * <p>
 * Ce service assure les opérations CRUD classiques ainsi que le calcul de
 * disponibilité des véhicules en fonction des covoiturages déjà programmés.
 * </p>
 */
@Service
public class VehiculePersoService {

    /** Dépôt d'accès aux données des véhicules personnels. */
    private final VehiculePersoRepository vehiculePersoRepository;

    /** Dépôt d'accès aux données des covoiturages pour vérifier les disponibilités. */
    private final CovoiturageRepository covoiturageRepository;

    /**
     * Constructeur avec injection des dépendances requises.
     *
     * @param vehiculePersoRepository dépôt des véhicules personnels
     * @param covoiturageRepository dépôt des covoiturages
     */
    public VehiculePersoService(VehiculePersoRepository vehiculePersoRepository, CovoiturageRepository covoiturageRepository) {
        this.vehiculePersoRepository = vehiculePersoRepository;
        this.covoiturageRepository = covoiturageRepository;
    }

    /**
     * Récupère la liste complète de tous les véhicules personnels enregistrés.
     *
     * @return la liste globale des véhicules personnels
     */
    public List<VehiculePerso> findAll() {
        return vehiculePersoRepository.findAll();
    }

    /**
     * Recherche un véhicule personnel par son identifiant unique.
     *
     * @param id l'identifiant recherché
     * @return le véhicule correspondant
     * @throws RuntimeException si le véhicule n'existe pas en base
     */
    public VehiculePerso findById(Long id) {
        return vehiculePersoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule personnel introuvable"));
    }

    /**
     * Enregistre un nouveau véhicule personnel en base de données.
     *
     * @param vehiculePerso l'entité véhicule à insérer
     * @return le véhicule sauvegardé
     */
    public VehiculePerso create(VehiculePerso vehiculePerso) {
        return vehiculePersoRepository.save(vehiculePerso);
    }

    /**
     * Met à jour les caractéristiques d'un véhicule personnel existant.
     *
     * @param id l'identifiant du véhicule à modifier
     * @param vehiculePerso les nouvelles données techniques à appliquer
     * @return le véhicule mis à jour
     * @throws RuntimeException si le véhicule est introuvable
     */
    public VehiculePerso update(Long id, VehiculePerso vehiculePerso) {
        VehiculePerso existing = findById(id);

        existing.setImmatriculation(vehiculePerso.getImmatriculation());
        existing.setMarque(vehiculePerso.getMarque());
        existing.setModele(vehiculePerso.getModele());
        existing.setMotorisation(vehiculePerso.getMotorisation());
        existing.setNbPlace(vehiculePerso.getNbPlace());
        existing.setPhotoUrl(vehiculePerso.getPhotoUrl());
        existing.setCo2Km(vehiculePerso.getCo2Km());
        existing.setCategorie(vehiculePerso.getCategorie());

        if (vehiculePerso.getProprietaire() != null) {
            existing.setProprietaire(vehiculePerso.getProprietaire());
        }

        return vehiculePersoRepository.save(existing);
    }

    /**
     * Supprime définitivement un véhicule personnel de la base de données.
     *
     * @param id l'identifiant du véhicule à supprimer
     * @throws RuntimeException si le véhicule est introuvable
     */
    public void delete(Long id) {
        if (!vehiculePersoRepository.existsById(id)) {
            throw new RuntimeException("Véhicule personnel introuvable pour suppression");
        }
        vehiculePersoRepository.deleteById(id);
    }

    /**
     * Récupère la liste des véhicules appartenant à un collaborateur précis.
     *
     * @param proprietaireId l'identifiant de l'utilisateur (propriétaire)
     * @return la liste de ses véhicules personnels
     */
    public List<VehiculePerso> findByProprietaire(Long proprietaireId) {
        return vehiculePersoRepository.findByProprietaireId(proprietaireId);
    }

    /**
     * Recherche les véhicules personnels disponibles sur une plage de dates.
     * <p>
     * Un véhicule est considéré comme disponible lorsqu'il n'est associé
     * à aucun covoiturage dont la période chevauche la plage demandée.
     * Le chevauchement est détecté si :
     * {@code dateDepartCovoiturage < dateFinDemandee && dateFinCovoiturage > dateDebutDemandee}
     * </p>
     * <p>
     * La date de fin du covoiturage est calculée à partir de sa date de départ
     * et de sa durée estimée (en heures décimales converties en minutes).
     * </p>
     *
     * @param dateDebut date et heure de début de la période recherchée
     * @param dateFin date et heure de fin de la période recherchée
     * @return la liste des véhicules personnels disponibles sur la période demandée
     */
    public List<VehiculePerso> findAllAvailable(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return vehiculePersoRepository.findAll()
                .stream()
                .filter(v -> {
                    // Récupérer tous les covoiturages liés à ce véhicule
                    List<Covoiturage> covoiturages = covoiturageRepository.findByVehiculeId(v.getId());

                    // Vérifier qu'aucun covoiturage ne chevauche la période demandée
                    return covoiturages.stream()
                            .noneMatch(c -> {
                                // Calcul de la date de fin du covoiturage (dureeTrajet est en heures)
                                LocalDateTime dateFinCovoit = c.getDateDepart()
                                        .plusMinutes((long) (c.getDureeTrajet() * 60));

                                return c.getDateDepart().isBefore(dateFin) && dateFinCovoit.isAfter(dateDebut);
                            });
                })
                .toList();
    }
}