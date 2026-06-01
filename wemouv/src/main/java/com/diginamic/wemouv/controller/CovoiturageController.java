package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des covoiturages et des participations passagers.
 */
@RestController
@RequestMapping("/api/covoiturages")
public class CovoiturageController {

    private final CovoiturageRepository covoiturageRepository;
    private final ParticipationCovoiturageRepository participationCovoiturageRepository;

    /**
     * Constructeur avec injection des deux repositories nécessaires.
     */
    public CovoiturageController(CovoiturageRepository covoiturageRepository,
                                 ParticipationCovoiturageRepository participationCovoiturageRepository) {
        this.covoiturageRepository = covoiturageRepository;
        this.participationCovoiturageRepository = participationCovoiturageRepository;
    }

    /**
     * Récupère la liste complète de tous les covoiturages enregistrés.
     */
    @GetMapping
    public List<Covoiturage> getAllCovoiturages() {
        return covoiturageRepository.findAll();
    }

    /**
     * Récupère un covoiturage spécifique par son identifiant unique.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Covoiturage> getCovoiturageById(@PathVariable Long id) {
        return covoiturageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée et enregistre un nouveau covoiturage.
     */
    @PostMapping
    public ResponseEntity<Covoiturage> createCovoiturage(@RequestBody Covoiturage covoiturage) {
        covoiturage.setDateCreation(LocalDateTime.now());
        covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesInitial());
        
        Covoiturage savedCovoiturage = covoiturageRepository.save(covoiturage);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCovoiturage);
    }

    /**
     * Met à jour les informations d'un covoiturage existant.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Covoiturage> updateCovoiturage(@PathVariable Long id, @RequestBody Covoiturage covoiturageDetails) {
        return covoiturageRepository.findById(id).map(covoiturage -> {
            covoiturage.setAdresseDepart(covoiturageDetails.getAdresseDepart());
            covoiturage.setAdresseArrive(covoiturageDetails.getAdresseArrive());
            covoiturage.setDateDepart(covoiturageDetails.getDateDepart());
            covoiturage.setDureeTrajet(covoiturageDetails.getDureeTrajet());
            covoiturage.setDistanceKm(covoiturageDetails.getDistanceKm());
            covoiturage.setNbPlacesInitial(covoiturageDetails.getNbPlacesInitial());
            covoiturage.setNbPlacesRestantes(covoiturageDetails.getNbPlacesRestantes());
            covoiturage.setStatut(covoiturageDetails.getStatut());
            covoiturage.setVehicule(covoiturageDetails.getVehicule());
            
            Covoiturage updatedCovoiturage = covoiturageRepository.save(covoiturage);
            return ResponseEntity.ok(updatedCovoiturage);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime définitivement un covoiturage du système.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCovoiturage(@PathVariable Long id) {
        return covoiturageRepository.findById(id).map(covoiturage -> {
            covoiturageRepository.delete(covoiturage);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Permet à un utilisateur de réserver une place (participer) dans un covoiturage.
     */
    @PostMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<ParticipationCovoiturage> participer(
            @PathVariable Long covoiturageId, 
            @PathVariable Long utilisateurId) {
            
        return covoiturageRepository.findById(covoiturageId).map(covoiturage -> {
            if (covoiturage.getNbPlacesRestantes() <= 0) {
                return ResponseEntity.badRequest().<ParticipationCovoiturage>build();
            }

            // Décrémenter les places restantes
            covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesRestantes() - 1);
            covoiturageRepository.save(covoiturage);

            // Créer la participation
            ParticipationCovoiturage participation = new ParticipationCovoiturage();
            participation.getId().setCovoiturageId(covoiturageId);
            participation.getId().setUtilisateurId(utilisateurId);
            participation.setStatut("VALIDE");

            ParticipationCovoiturage saved = participationCovoiturageRepository.save(participation);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Permet à un utilisateur d'annuler sa participation à un covoiturage.
     */
    @DeleteMapping("/{covoiturageId}/participer/{utilisateurId}")
    public ResponseEntity<Void> annulerParticipation(
            @PathVariable Long covoiturageId, 
            @PathVariable Long utilisateurId) {
            
        ParticipationCovoiturageId id = new ParticipationCovoiturageId();
        id.setCovoiturageId(covoiturageId);
        id.setUtilisateurId(utilisateurId);

        return participationCovoiturageRepository.findById(id).map(participation -> {
            participationCovoiturageRepository.delete(participation);

            // Ré-augmenter le nombre de places sur le trajet
            covoiturageRepository.findById(covoiturageId).ifPresent(covoiturage -> {
                covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesRestantes() + 1);
                covoiturageRepository.save(covoiturage);
            });

            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}