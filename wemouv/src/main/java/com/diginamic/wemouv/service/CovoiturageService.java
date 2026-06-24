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
import com.diginamic.wemouv.specification.CovoiturageSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service métier gérant la logique principale des covoiturages.
 * <p>
 * Cette classe fait l'interface entre les contrôleurs REST et les dépôts (repositories)
 * pour assurer la cohérence des données lors des recherches, créations, modifications
 * et annulations de trajets. Elle s'occupe également de déclencher les notifications par e-mail.
 * </p>
 */
@Service
public class CovoiturageService {

    private final CovoiturageRepository covoiturageRepository;
    private final ParticipationCovoiturageRepository participationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;

    /**
     * Constructeur injectant l'ensemble des dépendances et dépôts requis.
     *
     * @param covoiturageRepository   Dépôt d'accès aux données des covoiturages
     * @param participationRepository Dépôt d'accès aux données des participations
     * @param vehiculeRepository      Dépôt d'accès aux données des véhicules
     * @param utilisateurRepository   Dépôt d'accès aux données des utilisateurs
     * @param emailService            Service d'envoi de courriers électroniques
     */
    public CovoiturageService(CovoiturageRepository covoiturageRepository,
                              ParticipationCovoiturageRepository participationRepository,
                              VehiculeRepository vehiculeRepository,
                              UtilisateurRepository utilisateurRepository,
                              EmailService emailService
    ) {
        this.covoiturageRepository = covoiturageRepository;
        this.participationRepository = participationRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
    }

    /**
     * Récupère l'ensemble des covoiturages enregistrés dans l'application.
     *
     * @return Une liste contenant tous les covoiturages.
     */
    public List<Covoiturage> findAll() {
        return covoiturageRepository.findAll();
    }

    /**
     * Recherche un covoiturage spécifique par son identifiant unique.
     *
     * @param id L'identifiant du covoiturage recherché.
     * @return Le covoiturage correspondant.
     * @throws RuntimeException Si aucun covoiturage ne correspond à cet ID.
     */
    public Covoiturage findById(Long id) {
        return covoiturageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));
    }

    /**
     * Liste tous les covoiturages créés par un organisateur spécifique.
     *
     * @param organisateurId L'identifiant du créateur des trajets.
     * @return Une liste des covoiturages organisés par cet utilisateur.
     */
    public List<Covoiturage> findByOrganisateur(Long organisateurId) {
        return covoiturageRepository.findByOrganisateurId(organisateurId);
    }

    /**
     * Liste tous les covoiturages programmés avec un véhicule précis.
     *
     * @param vehiculeId L'identifiant du véhicule.
     * @return Une liste des covoiturages utilisant ce véhicule.
     */
    public List<Covoiturage> findByVehicule(Long vehiculeId) {
        return covoiturageRepository.findByVehiculeId(vehiculeId);
    }

    /**
     * Filtre les covoiturages selon leur état actuel.
     *
     * @param statut Le statut recherché (ex: EN_ATTENTE, ANNULE, COMPLET).
     * @return Une liste des covoiturages possédant ce statut.
     */
    public List<Covoiturage> findByStatut(Statut statut) {
        return covoiturageRepository.findByStatut(statut);
    }

    /**
     * Crée et enregistre un nouveau covoiturage en base de données.
     *
     * @param request L'objet de transfert de données (DTO) contenant les détails de l'annonce.
     * @return Le covoiturage nouvellement sauvegardé avec son ID généré.
     * @throws RuntimeException Si le véhicule, l'organisateur ou le conducteur sont introuvables en base.
     */
    public Covoiturage create(CovoiturageRequest request) {

        Vehicule vehicule = vehiculeRepository
                .findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable"));

        Utilisateur organisateur = utilisateurRepository
                .findById(request.getOrganisateurId())
                .orElseThrow(() -> new RuntimeException("Organisateur introuvable"));

        Utilisateur conducteur = utilisateurRepository
                .findById(request.getConducteurId())
                .orElseThrow(() -> new RuntimeException("Conducteur introuvable"));

        Covoiturage covoiturage = new Covoiturage();

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
     * Met à jour un covoiturage existant, recalcule les places et notifie les utilisateurs.
     * <p>
     * Cette méthode détecte les changements par rapport à l'ancienne version du trajet
     * et envoie un e-mail récapitulatif formaté en HTML à l'organisateur et aux passagers.
     * </p>
     *
     * @param id      L'identifiant du covoiturage à modifier.
     * @param request Les nouvelles données à appliquer.
     * @return Le covoiturage mis à jour.
     * @throws RuntimeException      Si le covoiturage ou les relations (véhicule, utilisateurs) n'existent pas.
     * @throws IllegalStateException Si la réduction du nombre de places initiales est inférieure au nombre de passagers déjà inscrits.
     */
    public Covoiturage update(Long id, CovoiturageRequest request) {

        Covoiturage covoiturage = covoiturageRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));

        // --- DÉTECTION DES CHANGEMENTS (AVANT DE METTRE À JOUR) ---
        List<String> changements = new ArrayList<>();

        if (!Objects.equals(covoiturage.getAdresseDepart(), request.getAdresseDepart())) {
            changements.add("<li><strong>Départ :</strong> " + covoiturage.getAdresseDepart() + " ➔ " + request.getAdresseDepart() + "</li>");
        }
        if (!Objects.equals(covoiturage.getAdresseArrive(), request.getAdresseArrive())) {
            changements.add("<li><strong>Arrivée :</strong> " + covoiturage.getAdresseArrive() + " ➔ " + request.getAdresseArrive() + "</li>");
        }
        if (!Objects.equals(covoiturage.getDateDepart(), request.getDateDepart())) {
            changements.add("<li><strong>Date/Heure :</strong> " + covoiturage.getDateDepart() + " ➔ " + request.getDateDepart() + "</li>");
        }
        if (!Objects.equals(covoiturage.getNbPlacesInitial(), request.getNbPlacesInitial())) {
            changements.add("<li><strong>Places totales :</strong> " + covoiturage.getNbPlacesInitial() + " ➔ " + request.getNbPlacesInitial() + "</li>");
        }

        String detailsChangementsHtml;
        if (changements.isEmpty()) {
            detailsChangementsHtml = "<p><em>Aucune information principale n'a été modifiée (mise à jour technique).</em></p>";
        } else {
            detailsChangementsHtml = "<ul style='margin: 0; padding-left: 20px;'>" + String.join("", changements) + "</ul>";
        }
        // -----------------------------------------------------------

        // 1. Mise à jour des informations de base
        covoiturage.setAdresseDepart(request.getAdresseDepart());
        covoiturage.setAdresseArrive(request.getAdresseArrive());
        covoiturage.setDateDepart(request.getDateDepart());
        covoiturage.setDateCreation(request.getDateCreation());
        covoiturage.setDureeTrajet(request.getDureeTrajet());
        covoiturage.setDistanceKm(request.getDistanceKm());
        covoiturage.setStatut(request.getStatut());

        // 2. Gestion stricte des places
        covoiturage.setNbPlacesInitial(request.getNbPlacesInitial());

        int nombreInscrits = covoiturage.getParticipations().size();
        int nouvellesPlacesRestantes = request.getNbPlacesInitial() - nombreInscrits;

        if (nouvellesPlacesRestantes < 0) {
            throw new IllegalStateException("Impossible de réduire à " + request.getNbPlacesInitial() +
                    " places car il y a déjà " + nombreInscrits + " passagers inscrits.");
        }
        covoiturage.setNbPlacesRestantes(nouvellesPlacesRestantes);

        // 3. Mise à jour des relations
        Vehicule vehicule = vehiculeRepository
                .findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable"));

        Utilisateur organisateur = utilisateurRepository
                .findById(request.getOrganisateurId())
                .orElseThrow(() -> new RuntimeException("Organisateur introuvable"));

        Utilisateur conducteur = utilisateurRepository
                .findById(request.getConducteurId())
                .orElseThrow(() -> new RuntimeException("Conducteur introuvable"));

        covoiturage.setVehicule(vehicule);
        covoiturage.setOrganisateur(organisateur);
        covoiturage.setConducteur(conducteur);

        // 4. Notifications groupées au format HTML
        List<String> destinataires = new ArrayList<>();
        destinataires.add(covoiturage.getOrganisateur().getEmail());
        for (ParticipationCovoiturage participation : covoiturage.getParticipations()) {
            destinataires.add(participation.getUtilisateur().getEmail());
        }
        String[] tableauDestinataires = destinataires.toArray(new String[0]);

        String contenuHtml = "<h2>🚗 Mise à jour de votre trajet !</h2>"
                + "<p>Bonjour,</p>"
                + "<p>Nous vous informons qu'une modification a été apportée à votre covoiturage.</p>"
                + "<div style='background-color: #e8f4f8; padding: 15px; border-radius: 5px; margin-top: 15px; border-left: 4px solid #007bff;'>"
                + "  <h3 style='margin-top: 0; color: #007bff; font-size: 16px;'>Voici ce qui a changé :</h3>"
                + detailsChangementsHtml
                + "</div>"
                + "<p style='color: #888888; font-size: 12px; margin-top: 20px;'>L'équipe Wemouv</p>";

        emailService.sendMailGroup(
                tableauDestinataires,
                "Wemouv - Modification de votre covoiturage",
                contenuHtml
        );

        // 5. Sauvegarde
        return covoiturageRepository.save(covoiturage);
    }

    /**
     * Supprime définitivement un covoiturage de la base de données.
     * <p>
     * Opération transactionnelle : supprime d'abord les participations associées pour
     * éviter les conflits de clés étrangères, puis supprime le covoiturage, et enfin
     * notifie l'organisateur et les passagers de l'annulation.
     * </p>
     *
     * @param id L'identifiant du covoiturage à supprimer.
     * @throws RuntimeException Si le covoiturage est introuvable.
     */
    @Transactional
    public void delete(Long id) {

        Covoiturage covoiturage = covoiturageRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));

        // Notifications groupées au format HTML pour la suppression
        List<String> destinataires = new ArrayList<>();
        destinataires.add(covoiturage.getOrganisateur().getEmail());
        for (ParticipationCovoiturage participation : covoiturage.getParticipations()) {
            destinataires.add(participation.getUtilisateur().getEmail());
        }
        String[] tableauDestinataires = destinataires.toArray(new String[0]);

        String contenuHtml = "<h2>❌ Annulation de trajet</h2>"
                + "<p>Bonjour,</p>"
                + "<p>Nous vous informons que le covoiturage prévu entre <strong>" + covoiturage.getAdresseDepart() + "</strong> et <strong>" + covoiturage.getAdresseArrive() + "</strong> a été annulé.</p>"
                + "<p style='color: #888888; font-size: 12px; margin-top: 20px;'>L'équipe Wemouv</p>";

        emailService.sendMailGroup(
                tableauDestinataires,
                "Wemouv - Annulation de votre covoiturage",
                contenuHtml
        );

        participationRepository.deleteByCovoiturageId(id);
        covoiturageRepository.delete(covoiturage);
    }

    /**
     * Gère l'inscription d'un utilisateur à un covoiturage en tant que passager.
     * (Méthode à implémenter prochainement).
     *
     * @param covoiturageId L'identifiant du covoiturage.
     * @param utilisateurId L'identifiant de l'utilisateur qui souhaite participer.
     * @return La nouvelle participation créée.
     */
    public ParticipationCovoiturage participer(Long covoiturageId, Long utilisateurId) {
        return null;
    }

    /**
     * Gère la désinscription d'un passager d'un trajet de covoiturage.
     * (Méthode à implémenter prochainement).
     *
     * @param covoiturageId L'identifiant du covoiturage concerné.
     * @param utilisateurId L'identifiant de l'utilisateur annulant sa place.
     */
    public void annulerParticipation(Long covoiturageId, Long utilisateurId) {
    }

    /**
     * Récupère l'historique et les réservations futures d'un passager.
     * <p>
     * Trie les participations en deux listes : "enCours" (trajets à venir)
     * et "historique" (trajets passés) selon la date actuelle.
     * </p>
     *
     * @param utilisateurId L'identifiant du passager.
     * @return Une Map contenant les listes de covoiturages sous les clés "enCours" et "historique".
     * @throws RuntimeException Si l'utilisateur n'existe pas.
     */
    public Map<String, List<Covoiturage>> getReservationsPassager(Long utilisateurId) {

        utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Passager introuvable avec l'ID : " + utilisateurId));

        LocalDateTime maintenant = LocalDateTime.now();

        List<ParticipationCovoiturage> futures = participationRepository
                .findByUtilisateurIdAndCovoiturageDateDepartAfter(utilisateurId, maintenant);

        List<ParticipationCovoiturage> passees = participationRepository
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
     * Récupère les annonces de covoiturage créées par un conducteur.
     * <p>
     * Trie les annonces en deux listes : "enCours" (trajets à venir)
     * et "historique" (trajets passés) selon la date actuelle.
     * </p>
     *
     * @param conducteurId L'identifiant du conducteur.
     * @return Une Map contenant les listes de covoiturages sous les clés "enCours" et "historique".
     * @throws RuntimeException Si le conducteur n'existe pas.
     */
    public Map<String, List<Covoiturage>> getAnnoncesConducteur(Long conducteurId) {

        utilisateurRepository.findById(conducteurId)
                .orElseThrow(() -> new RuntimeException("Conducteur introuvable avec l'ID : " + conducteurId));

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

    /**
     *
     * @param depart
     * @param arrivee
     * @param date
     * @param statut
     * @return
     */
    public List<Covoiturage> filtrer(String depart, String arrivee, String categorieVehicule, Statut statut) {

        Specification<Covoiturage> spec = Specification
                .where(CovoiturageSpecifications.hasDepart(depart))
                .and(CovoiturageSpecifications.hasArrivee(arrivee))
                .and(CovoiturageSpecifications.hasCategorieVehicule(categorieVehicule))
                .and(CovoiturageSpecifications.hasStatut(statut));

        return covoiturageRepository.findAll(spec);
    }




}