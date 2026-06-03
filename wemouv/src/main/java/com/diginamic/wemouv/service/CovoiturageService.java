package com.diginamic.wemouv.service;

import com.diginamic.wemouv.dto.CovoiturageRequest;
import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageRepository;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service métier gérant la logique des covoiturages et des participations.
 * <p>
 * Cette classe fait l'interface entre les contrôleurs REST et les dépôts (repositories)
 * pour assurer la cohérence des données lors des recherches, créations, modifications
 * et annulations de trajets.
 * </p>
 */
@Service
public class CovoiturageService {

    /** Dépôt d'accès aux données des covoiturages. */
    private final CovoiturageRepository covoiturageRepository;

    /** Dépôt d'accès aux données des participations/réservations. */
    private final ParticipationCovoiturageRepository participationCovoiturageRepository;

    /** Dépôt d'accès aux données des vehicules */
    private final VehiculeRepository vehiculeRepository;

    /** Dépôt d'accès aux données des utilisateurs */
    private final UtilisateurRepository utilisateurRepository;


    /**
     * Constructeur injectant les repositories requis.
     *
     * @param covoiturageRepository dépôt pour les covoiturages
     * @param participationCovoiturageRepository dépôt pour les participations
     */
    public CovoiturageService(CovoiturageRepository covoiturageRepository,
                              ParticipationCovoiturageRepository participationCovoiturageRepository,
                              VehiculeRepository vehiculeRepository,
                              UtilisateurRepository utilisateurRepository
    ) {
        this.covoiturageRepository = covoiturageRepository;
        this.participationCovoiturageRepository = participationCovoiturageRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Récupère l'ensemble des covoiturages de l'application.
     *
     * @return la liste de tous les covoiturages
     */
    public List<Covoiturage> findAll() {
        return covoiturageRepository.findAll();
    }

    /**
     * Recherche un covoiturage par son identifiant unique.
     *
     * @param id l'identifiant recherché
     * @return le covoiturage correspondant
     * @throws RuntimeException si aucun covoiturage ne possède cet identifiant
     */
    public Covoiturage findById(Long id) {
        return covoiturageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));
    }

    /**
     * Liste tous les covoiturages créés par un organisateur spécifique.
     *
     * @param organisateurId l'identifiant unique du créateur du trajet
     * @return la liste des covoiturages associés
     */
    public List<Covoiturage> findByOrganisateur(Long organisateurId) {
        return covoiturageRepository.findByOrganisateurId(organisateurId);
    }

    /**
     * Liste tous les covoiturages programmés avec un véhicule donné.
     *
     * @param vehiculeId l'identifiant du véhicule utilisé
     * @return la liste des covoiturages concernés
     */
    public List<Covoiturage> findByVehicule(Long vehiculeId) {
        return covoiturageRepository.findByVehiculeId(vehiculeId);
    }

    /**
     * Filtre les covoiturages selon leur état actuel (OUVERT, COMPLET, ANNULE...).
     *
     * @param statut le statut recherché
     * @return la liste des covoiturages correspondants
     */
    public List<Covoiturage> findByStatut(Statut statut) {
        return covoiturageRepository.findByStatut(statut);
    }

    /**
     * Enregistre un nouveau covoiturage en base de données.
     *
     * @param request l'entité à persister
     * @return le covoiturage sauvegardé avec son ID généré
     */
    public Covoiturage create(
            CovoiturageRequest request
    ) {

        Vehicule vehicule =
                vehiculeRepository
                        .findById(request.getVehiculeId())
                        .orElseThrow(() ->
                                new RuntimeException("Vehicule introuvable"));

        Utilisateur organisateur =
                utilisateurRepository
                        .findById(request.getOrganisateurId())
                        .orElseThrow(() ->
                                new RuntimeException("Organisateur introuvable"));

        Utilisateur conducteur =
                utilisateurRepository
                        .findById(request.getConducteurId())
                        .orElseThrow(() ->
                                new RuntimeException("Conducteur introuvable"));

        Covoiturage covoiturage =
                new Covoiturage();

        covoiturage.setAdresseDepart(request.getAdresseDepart());
        covoiturage.setAdresseArrive(request.getAdresseArrive());
        covoiturage.setDateDepart(request.getDateDepart());
        covoiturage.setDateCreation(request.getDateCreation());
        covoiturage.setDureeTrajet(request.getDureeTrajet());
        covoiturage.setDistanceKm(request.getDistanceKm());
        covoiturage.setNbPlacesInitial(request.getNbPlacesInitial());
        covoiturage.setNbPlacesRestantes(request.getNbPlacesRestantes());
        covoiturage.setStatut(request.getStatut());

        covoiturage.setVehicule(vehicule);
        covoiturage.setOrganisateur(organisateur);
        covoiturage.setConducteur(conducteur);

        return covoiturageRepository.save(covoiturage);
    }

    /**
     * Met à jour un covoiturage existant.
     *
     * @param id l'identifiant de la ressource à modifier
     * @param covoiturage les nouvelles données à appliquer
     * @return l'entité mise à jour en base
     */
    public Covoiturage update(Long id, Covoiturage covoiturage) {
        // Idéalement, s'assurer que l'entité porte le bon ID avant le save
        covoiturage.setId(id);
        return covoiturageRepository.save(covoiturage);
    }

    /**
     * Supprime définitivement un covoiturage par son ID.
     *
     * @param id l'identifiant du covoiturage à détruire
     */
    public void delete(Long id) {
        covoiturageRepository.deleteById(id);
    }

    /**
     * Gère l'inscription d'un passager sur un trajet de covoiturage.
     * (Sera implémenté dans la Tâche 5).
     *
     * @param covoiturageId le trajet concerné
     * @param utilisateurId le passager demandeur
     * @return la participation créée
     */
    public ParticipationCovoiturage participer(Long covoiturageId, Long utilisateurId) {
        return null;
    }

    /**
     * Gère la désinscription d'un passager d'un trajet.
     *
     * @param covoiturageId le trajet concerné
     * @param utilisateurId le passager annulant sa réservation
     */
    public void annulerParticipation(Long covoiturageId, Long utilisateurId) {
    }

    /**
     * TÂCHE 4 : Récupère et sépare les réservations d'un passager.
     * <p>
     * Les trajets sont classés dans une Map selon deux clés :
     * <ul>
     * <li>"enCours" : Trajets dont la date de départ est dans le futur</li>
     * <li>"historique" : Trajets passés</li>
     * </ul>
     * </p>
     *
     * @param utilisateurId l'identifiant du passager connecté
     * @return une Map contenant les deux listes de covoiturages
     */
    public Map<String, List<Covoiturage>> getReservationsPassager(Long utilisateurId) {
        LocalDateTime maintenant = LocalDateTime.now();

        List<ParticipationCovoiturage> futures = participationCovoiturageRepository
                .findByUtilisateurIdAndCovoiturageDateDepartAfter(utilisateurId, maintenant);

        List<ParticipationCovoiturage> passees = participationCovoiturageRepository
                .findByUtilisateurIdAndCovoiturageDateDepartBefore(utilisateurId, maintenant);

        List<Covoiturage> enCours = futures.stream()
                .map(ParticipationCovoiturage::getCovoiturage)
                .collect(Collectors.toList());

        List<Covoiturage> historique = passees.stream()
                .map(ParticipationCovoiturage::getCovoiturage)
                .collect(Collectors.toList());

        Map<String, List<Covoiturage>> resultat = new HashMap<>();
        resultat.put("enCours", enCours);
        resultat.put("historique", historique);

        return resultat;
    }

    /**
     * TÂCHE 7 : Récupère et sépare les annonces publiées par un chauffeur.
     * <p>
     * Les annonces sont classées dans une Map selon deux clés :
     * <ul>
     * <li>"enCours" : Trajets planifiés triés par date chronologique ascendante</li>
     * <li>"historique" : Anciens trajets triés par date décroissante</li>
     * </ul>
     * </p>
     *
     * @param conducteurId l'identifiant de l'organisateur/conducteur connecté
     * @return une Map organisée contenant les annonces en cours et passées
     */
    public Map<String, List<Covoiturage>> getAnnoncesConducteur(Long conducteurId) {
        LocalDateTime maintenant = LocalDateTime.now();

        List<Covoiturage> enCours = covoiturageRepository
                .findByOrganisateurIdAndDateDepartAfterOrderByDateDepartAsc(conducteurId, maintenant);

        List<Covoiturage> historique = covoiturageRepository
                .findByOrganisateurIdAndDateDepartBeforeOrderByDateDepartDesc(conducteurId, maintenant);

        Map<String, List<Covoiturage>> resultat = new HashMap<>();
        resultat.put("enCours", enCours);
        resultat.put("historique", historique);

        return resultat;
    }
}