package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.*;
import com.diginamic.wemouv.enums.Disponibilite;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeDeServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.diginamic.wemouv.enums.Marque;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier gérant la logique des véhicules de service de la flotte.
 * <p>
 * Assure la gestion du cycle de vie des véhicules (CRUD), le filtrage dynamique
 * pour l'administration, le calcul des disponibilités pour les réservations,
 * ainsi que les règles de gestion d'annulation de trajets en cas de panne.
 * </p>
 */
@Service
public class VehiculeDeServiceService {

    /** Dépôt pour la gestion des véhicules de service. */
    private final VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /** Dépôt pour la vérification des covoiturages impactés. */
    private final CovoiturageRepository covoiturageRepository;

    /** Dépôt pour le suivi des réservations professionnelles. */
    private final ReservationRepository reservationRepository;

    /** Service de notification par e-mail. */
    private final EmailService emailService;

    /**
     * Constructeur injectant l'ensemble des dépendances nécessaires.
     * * @param vehiculeDeServiceRepository dépôt des véhicules
     * @param covoiturageRepository dépôt des covoiturages
     * @param reservationRepository dépôt des réservations
     * @param emailService service pour l'envoi de mails
     */
    public VehiculeDeServiceService(VehiculeDeServiceRepository vehiculeDeServiceRepository,
                                    CovoiturageRepository covoiturageRepository,
                                    ReservationRepository reservationRepository,
                                    EmailService emailService) {
        this.vehiculeDeServiceRepository = vehiculeDeServiceRepository;
        this.covoiturageRepository = covoiturageRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    /**
     * Récupère l'intégralité des véhicules de service enregistrés.
     *
     * @return la liste brute de tous les véhicules de service
     */
    public List<VehiculeDeService> findAll() {
        return vehiculeDeServiceRepository.findAll();
    }

    /**
     * Recherche un véhicule de service par son identifiant unique.
     *
     * @param id l'identifiant du véhicule recherché
     * @return le véhicule de service correspondant
     * @throws RuntimeException si le véhicule est introuvable
     */
    public VehiculeDeService findById(Long id) {
        return vehiculeDeServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule de service introuvable"));
    }

    /**
     * Récupère les véhicules de la flotte en appliquant les filtres exclusifs de l'admin.
     *
     * @param immatriculation fragment de plaque recherché (optionnel)
     * @param marque fragment de marque recherché (optionnel)
     * @return la liste des véhicules correspondants filtrés
     */
    public List<VehiculeDeService> getVehiculesFlotte(String immatriculation, String marque) {

        // 1. Filtrage par immatriculation (String classique)
        if (immatriculation != null && !immatriculation.trim().isEmpty()) {
            return vehiculeDeServiceRepository.findByImmatriculationContainingIgnoreCase(immatriculation.trim());
        }

        // 2. Filtrage par marque (Conversion du texte vers l'Enum)
        if (marque != null && !marque.trim().isEmpty()) {
            try {
                // Conversion propre vers l'énumération
                Marque marqueEnum = Marque.valueOf(marque.trim().toUpperCase());
                return vehiculeDeServiceRepository.findByMarque(marqueEnum);
            } catch (IllegalArgumentException e) {
                // Si la marque saisie n'existe pas du tout dans l'Enum, on renvoie une liste vide de sécurité
                return java.util.Collections.emptyList();
            }
        }

        // 3. Aucun filtre actif, on liste tout le parc
        return vehiculeDeServiceRepository.findAll();
    }

    /**
     * Recherche les véhicules de service disponibles sur une plage de dates.
     * <p>
     * Un véhicule est considéré comme disponible lorsqu'il est marqué comme DISPONIBLE
     * et qu'il ne possède aucune réservation chevauchant la période demandée.
     * </p>
     *
     * @param dateDebut date et heure de début de la plage demandée
     * @param dateFin date et heure de fin de la plage demandée
     * @return la liste des véhicules de service disponibles
     */
    public List<VehiculeDeService> findAllAvailable(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return vehiculeDeServiceRepository.findAll()
                .stream()
                // Filtrer par statut disponible
                .filter(v -> v.getDisponibilite() == Disponibilite.DISPONIBLE)
                // Filtrer l'absence de chevauchement de réservation
                .filter(v -> {
                    List<Reservation> reservations = reservationRepository.findByVehiculeId(v.getId());
                    return reservations.stream()
                            .noneMatch(r -> r.getDateDebut().isBefore(dateFin)
                                    && r.getDateFin().isAfter(dateDebut));
                })
                .toList();
    }

    /**
     * Enregistre un nouveau véhicule de service et initialise son statut par défaut.
     *
     * @param vehicule l'entité véhicule à insérer
     * @return le véhicule de service persisté
     */
    public VehiculeDeService create(VehiculeDeService vehicule) {
        vehicule.setDisponibilite(Disponibilite.DISPONIBLE);
        return vehiculeDeServiceRepository.save(vehicule);
    }

    /**
     * Met à jour les caractéristiques d'un véhicule de service et gère les impacts de statut.
     * <p>
     * Si le véhicule bascule vers un état d'indisponibilité technique (Réparation ou Hors Service),
     * une procédure d'annulation en cascade et de notification des utilisateurs est enclenchée.
     * </p>
     *
     * @param id l'identifiant du véhicule à mettre à jour
     * @param details les nouvelles données à appliquer
     * @return le véhicule mis à jour et sauvegardé
     * @throws RuntimeException si le véhicule cible est introuvable
     */
    @Transactional
    public VehiculeDeService update(Long id, VehiculeDeService details) {
        VehiculeDeService vehicule = vehiculeDeServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule de service introuvable pour mise à jour"));

        Disponibilite ancienneDispo = vehicule.getDisponibilite();
        Disponibilite nouvelleDispo = details.getDisponibilite();

        // Mise à jour de la fiche technique
        vehicule.setImmatriculation(details.getImmatriculation());
        vehicule.setMarque(details.getMarque());
        vehicule.setModele(details.getModele());
        vehicule.setMotorisation(details.getMotorisation());
        vehicule.setNbPlace(details.getNbPlace());
        vehicule.setPhotoUrl(details.getPhotoUrl());
        vehicule.setCo2Km(details.getCo2Km());
        vehicule.setCategorie(details.getCategorie());
        vehicule.setDisponibilite(nouvelleDispo);

        VehiculeDeService saved = vehiculeDeServiceRepository.save(vehicule);

        // Règle critique : Détection d'une mise en indisponibilité
        if (ancienneDispo != nouvelleDispo &&
                (nouvelleDispo == Disponibilite.EN_REPARATION || nouvelleDispo == Disponibilite.HORS_SERVICE)) {
            annulerTrajetsFuturs(saved);
        }

        return saved;
    }

    /**
     * Annule l'ensemble des covoiturages futurs rattachés à un véhicule indisponible.
     *
     * @param vehicule le véhicule immobilisé
     */
    private void annulerTrajetsFuturs(Vehicule vehicule) {
        List<Covoiturage> covoiturages = covoiturageRepository
                .findByVehiculeAndDateDepartAfter(vehicule, LocalDateTime.now());

        for (Covoiturage c : covoiturages) {
            c.setStatut(Statut.ANNULE);
            covoiturageRepository.save(c);
            notifierImpactes(c);
        }
    }

    /**
     * Envoie des e-mails d'alerte automatiques à l'organisateur et aux passagers d'un trajet annulé.
     *
     * @param c le covoiturage faisant l'objet de l'annulation
     */
    private void notifierImpactes(Covoiturage c) {
        // Notification de l'organisateur (Conducteur)
        emailService.sendMail(
                c.getOrganisateur().getEmail(),
                "Covoiturage annulé",
                "Le trajet du " + c.getDateDepart() + " a été annulé car le véhicule est indisponible."
        );

        // Notification de l'ensemble des passagers inscrits
        for (ParticipationCovoiturage p : c.getParticipations()) {
            emailService.sendMail(
                    p.getUtilisateur().getEmail(),
                    "Covoiturage annulé",
                    "Le trajet auquel vous participiez a été annulé."
            );
        }
    }


    /**
     * Passe un véhicule de service en HORS_SERVICE (suppression logique).
     *
     * @param id l'identifiant du véhicule à archiver
     * @throws RuntimeException si le véhicule est introuvable
     */
    @Transactional
    public void delete(Long id) {
        //  On récupère le vrai véhicule
        VehiculeDeService vehiculeDeService = vehiculeDeServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule de service introuvable pour suppression"));

        //  On change son statut
        vehiculeDeService.setDisponibilite(Disponibilite.HORS_SERVICE);

        //  On sauvegarde les modifications
        vehiculeDeServiceRepository.save(vehiculeDeService);
    }

    @Transactional
    public void changerStatut(Long id, Disponibilite nouvelleDisponibilite) {
        VehiculeDeService vehicule = vehiculeDeServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        vehicule.setDisponibilite(nouvelleDisponibilite);
        vehiculeDeServiceRepository.save(vehicule);
    }
}