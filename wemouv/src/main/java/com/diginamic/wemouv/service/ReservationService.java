package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.ReservationModificationRequest;
import com.diginamic.wemouv.dto.ReservationRequest;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.ReservationRepository;
import com.diginamic.wemouv.repository.VehiculeDeServiceRepository;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier gérant la logique des réservations de véhicules de service.
 * <p>
 * Cette classe assure la création, la modification, l'annulation et la consultation
 * des réservations. Elle garantit le respect des règles métier, notamment
 * la prévention des conflits de dates (chevauchement) lors de l'emprunt d'un véhicule.
 * </p>
 */
@Service
public class ReservationService {

    /** Dépôt d'accès aux données des réservations. */
    private final ReservationRepository reservationRepository;

    /** Service permettant d'accéder aux profils utilisateurs. */
    private final UtilisateurService utilisateurService;

    private final VehiculeDeServiceRepository vehiculeDeServiceRepository;

    /**
     * Constructeur avec injection des dépendances requises.
     *
     * @param reservationRepository dépôt pour les réservations
     * @param utilisateurService service pour les utilisateurs
     */
    public ReservationService(ReservationRepository reservationRepository,
                              UtilisateurService utilisateurService,
                              VehiculeDeServiceRepository vehiculeDeServiceRepository) {
        this.reservationRepository = reservationRepository;
        this.utilisateurService = utilisateurService;
        this.vehiculeDeServiceRepository = vehiculeDeServiceRepository;
    }

    /**
     * Récupère toutes les réservations enregistrées.
     *
     * @return la liste globale des réservations
     */
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    /**
     * Récupère une réservation spécifique par son identifiant.
     *
     * @param id l'identifiant recherché
     * @return la réservation correspondante
     * @throws RuntimeException si la réservation est introuvable
     */
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
    }

    /**
     * Crée une nouvelle réservation en s'assurant que le véhicule est disponible.
     * <p>
     * Le véhicule est considéré indisponible si une réservation existante
     * chevauche la période demandée (date de début et date de fin croisées).
     * </p>
     *
     * @param request l'objet contenant les dates et l'ID du véhicule ciblé
     * @param email l'email de l'utilisateur connecté qui effectue la demande
     * @return la réservation sauvegardée avec le statut CONFIRME
     * @throws RuntimeException si le véhicule est introuvable ou déjà réservé sur cette période
     */
    public Reservation create(ReservationRequest request, String email) {

        Utilisateur utilisateur = utilisateurService.findByEmail(email);

        VehiculeDeService vehicule = vehiculeDeServiceRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        // Vérification de la disponibilité (chevauchement de dates)
        boolean indisponible = reservationRepository.findByVehiculeId(vehicule.getId())
                .stream()
                .anyMatch(r -> r.getDateDebut().isBefore(request.getDateFin())
                        && r.getDateFin().isAfter(request.getDateDebut()));

        if (indisponible) {
            throw new RuntimeException("Véhicule indisponible sur cette période");
        }

        Reservation reservation = new Reservation();
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setVehicule(vehicule);
        reservation.setUtilisateur(utilisateur);
        reservation.setStatut(Statut.EN_ATTENTE);//  une nouvelle resa de vehicule et par defaut en attente de
                                                    // confirmation par l'admin

        return reservationRepository.save(reservation);
    }

    /**
     * Annule "logiquement" une réservation en modifiant son statut.
     *
     * @param id l'identifiant de la réservation à annuler
     * @return la réservation mise à jour
     * @throws RuntimeException si la réservation est introuvable
     */
    public Reservation annuler(Long id) {
        Reservation reservation = findById(id);
        reservation.setStatut(Statut.ANNULE);
        return reservationRepository.save(reservation);
    }

    /**
     * Confirme une réservation existante.
     *
     * @param id l'identifiant de la réservation
     * @return la réservation mise à jour
     * @throws RuntimeException si la réservation est introuvable
     */
    public Reservation confirmer(Long id) {
        Reservation reservation = findById(id);
        reservation.setStatut(Statut.CONFIRME);
        return reservationRepository.save(reservation);
    }

    /**
     * Met à jour les informations d'une réservation existante.
     * <p>
     * Cette méthode réalise une fusion des données : seuls les champs modifiables
     * (dates, statut) sont mis à jour à partir de l'objet fourni.
     * Les relations essentielles, telles que l'utilisateur et le véhicule,
     * sont préservées pour maintenir l'intégrité de la base de données.
     * </p>
     *
     * @param id l'identifiant unique de la réservation à cibler
     * @param details l'objet contenant les nouvelles valeurs (dates, statut)
     * @return la réservation mise à jour et persistée
     * @throws RuntimeException si aucune réservation n'est trouvée pour l'ID fourni
     */
    public Reservation update(Long id, ReservationModificationRequest details) {
        Reservation existing = findById(id);

        if (details.getDateDebut() != null) {
            existing.setDateDebut(details.getDateDebut());
        }
        if (details.getDateFin() != null) {
            existing.setDateFin(details.getDateFin());
        }
        if (details.getVehiculeId() != null) {
            VehiculeDeService vehicule = vehiculeDeServiceRepository.findById(details.getVehiculeId())
        .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
            existing.setVehicule(vehicule);
        }

        return reservationRepository.save(existing);
    }

    /**
     * Supprime définitivement une réservation de la base de données.
     *
     * @param id l'identifiant de la réservation à supprimer
     * @throws RuntimeException si la réservation est introuvable
     */
    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Réservation introuvable pour suppression");
        }
        reservationRepository.deleteById(id);
    }

    /**
     * Liste les réservations d'un utilisateur donné.
     *
     * @param utilisateurId l'identifiant de l'utilisateur
     * @return ses réservations
     */
    public List<Reservation> findByUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    /**
     * Liste les réservations associées à un véhicule.
     *
     * @param vehiculeId l'identifiant du véhicule
     * @return les réservations du véhicule
     */
    public List<Reservation> findByVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId);
    }
}