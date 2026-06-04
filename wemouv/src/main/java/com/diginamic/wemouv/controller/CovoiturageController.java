package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.dto.CovoiturageRequest;
import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.service.AnnuleParticiaptionCovoiturage;
import com.diginamic.wemouv.service.CovoiturageService;
import com.diginamic.wemouv.service.RechercheCovoiturage;
import com.diginamic.wemouv.service.ReserverCovoiturage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des covoiturages et des participations passagers.
 * * <p>Ce contrôleur expose les API permettant de gérer le cycle de vie des trajets
 * (CRUD), ainsi que les inscriptions et consultations de réservations ou d'annonces.</p>
 */
@RestController
@RequestMapping("/api/covoiturages")
public class CovoiturageController {

    /** Le service métier contenant la logique des covoiturages. */
    private final CovoiturageService covoiturageService;
    private final RechercheCovoiturage rechercheCovoiturage;
    private final ReserverCovoiturage reserverCovoiturage;
    private final AnnuleParticiaptionCovoiturage annuleParticiaptionCovoiturage;

    /**
     * Constructeur avec injection des services dédiés.
     *
     * @param covoiturageService le service gérant la logique métier des covoiturages
     */
    public CovoiturageController(CovoiturageService covoiturageService,
                                 RechercheCovoiturage rechercheCovoiturage,
                                 ReserverCovoiturage reserverCovoiturage,
                                 AnnuleParticiaptionCovoiturage annuleParticiaptionCovoiturage) {
        this.covoiturageService = covoiturageService;
        this.rechercheCovoiturage = rechercheCovoiturage;
        this.reserverCovoiturage = reserverCovoiturage;
        this.annuleParticiaptionCovoiturage = annuleParticiaptionCovoiturage;
    }

    /**
     * Récupère la liste complète de tous les covoiturages enregistrés en base de données.
     *
     * @return la liste de l'intégralité des covoiturages
     */
    @GetMapping
    public List<Covoiturage> getAllCovoiturages() {
        return covoiturageService.findAll();
    }

    /**
     * Recherche des covoiturages selon des critères optionnels.
     */
    @GetMapping("/recherche")
    public List<Covoiturage> rechercherCovoiturages(
            @RequestParam(value = "depart", required = false) String depart,
            @RequestParam(value = "arrivee", required = false) String arrivee,
            @RequestParam(value = "date", required = false) LocalDateTime date,
            @RequestParam(value = "statut", required = false) Statut statut) {
        return rechercheCovoiturage.rechercher(depart, arrivee, date, statut);
    }

    /**
     * Récupère un covoiturage spécifique par son identifiant unique.
     *
     * @param id l'identifiant unique du covoiturage recherché
     * @return un {@link ResponseEntity} contenant le covoiturage s'il est trouvé (HTTP 200),
     * ou une réponse vide avec le statut HTTP 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Covoiturage> getCovoiturageById(@PathVariable("id") Long id) {
        try {
            Covoiturage covoiturage = covoiturageService.findById(id);
            return ResponseEntity.ok(covoiturage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Crée et enregistre un nouveau covoiturage dans le système.
     *
     * @param request DTO contenant les informations du trajet à créer
     * @return un ResponseEntity contenant le covoiturage créé avec le statut HTTP 201 (Created)
     */
    @PostMapping
    public ResponseEntity<?> createCovoiturage(
            @RequestBody CovoiturageRequest request
    ) {

        try {

            Covoiturage savedCovoiturage =
                    covoiturageService.create(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedCovoiturage);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }



    /**
     * Met à jour un covoiturage existant.
     *
     * @param id identifiant du covoiturage à modifier
     * @param request DTO contenant les nouvelles informations du trajet
     * @return le covoiturage mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCovoiturage(
            @PathVariable("id") Long id,
            @RequestBody CovoiturageRequest request
    ) {

        try {

            Covoiturage updatedCovoiturage =
                    covoiturageService.update(id, request);

            return ResponseEntity.ok(updatedCovoiturage);

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Supprime définitivement un covoiturage du système.
     *
     * @param id identifiant du covoiturage à supprimer
     * @return HTTP 204 si suppression réussie,
     *         HTTP 404 si covoiturage introuvable,
     *         HTTP 500 en cas d'erreur serveur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCovoiturage(
            @PathVariable("id") Long id
    ) {

        try {

            covoiturageService.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Permet à un utilisateur de réserver une place (participer) dans un covoiturage.
     *
     * @param covoiturageId l'identifiant du trajet sur lequel s'inscrire
     * @param utilisateurId l'identifiant du passager qui effectue la réservation
     * @return un {@link ResponseEntity} contenant l'objet participation créé (HTTP 201),
     * un statut HTTP 400 (Bad Request) avec le message d'erreur si le trajet est complet,
     * ou un statut HTTP 404 (Not Found) si les ressources sont introuvables
     */
    @PostMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<?> participer(
            @PathVariable("covoiturageId") Long covoiturageId,
            @PathVariable("utilisateurId") Long utilisateurId) {
        try {
            ParticipationCovoiturage participation = reserverCovoiturage.reserver(covoiturageId, utilisateurId);
            return ResponseEntity.status(HttpStatus.CREATED).body(participation);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permet à un utilisateur d'annuler sa participation à un covoiturage.
     *
     * @param covoiturageId l'identifiant du trajet concerné
     * @param utilisateurId l'identifiant du passager qui annule sa réservation
     * @return un {@link ResponseEntity} vide avec le statut HTTP 24 (No Content) en cas de succès,
     * ou un statut HTTP 404 (Not Found) si la participation n'existe pas
     */
    @DeleteMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<?> annulerParticipation(
            @PathVariable("covoiturageId") Long covoiturageId,
            @PathVariable("utilisateurId") Long utilisateurId) {
        try {
            annuleParticiaptionCovoiturage.annuler(covoiturageId, utilisateurId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * TÂCHE 4 : Récupère les réservations d'un passager.
     * Cette méthode extrait les trajets d'un utilisateur sous forme de dictionnaire (Map)
     * répartis entre les covoiturages futures ("enCours") et passés ("historique").
     *
     * @param utilisateurId l'identifiant du passager connecté
     * @return un {@link ResponseEntity} contenant la Map structurée des trajets (HTTP 200)
     */
    @GetMapping("/mes-reservations/{utilisateurId}")
    public ResponseEntity<Map<String, List<Covoiturage>>> getMesReservations(@PathVariable Long utilisateurId) {
        Map<String, List<Covoiturage>> reservations = covoiturageService.getReservationsPassager(utilisateurId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * <b>TÂCHE 7 :</b> Permet à l'organisateur/conducteur de voir les covoiturages qu'il propose.
     * * <p>Cette méthode extrait les annonces d'un chauffeur sous forme de dictionnaire (Map)
     * répartis entre ses trajets planifiés ("enCours") et ses anciens trajets ("historique").</p>
     *
     * @param conducteurId l'identifiant unique du chauffeur connecté
     * @return un {@link ResponseEntity} contenant la Map structurée de ses annonces (HTTP 200)
     */
    @GetMapping("/mes-annonces/{conducteurId}")
    public ResponseEntity<Map<String, List<Covoiturage>>> getMesAnnonces(@PathVariable Long conducteurId) {
        Map<String, List<Covoiturage>> annonces = covoiturageService.getAnnoncesConducteur(conducteurId);
        return ResponseEntity.ok(annonces);
    }
}
