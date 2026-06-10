package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.entity.Utilisateur;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageIdRepository;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier dédié à la réservation d'une place dans un covoiturage.
 */
@Service
public class ReserverCovoiturage {

    private final CovoiturageRepository covoiturageRepository;
    private final ParticipationCovoiturageIdRepository participationRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Constructeur avec injection des dépendances.
     */
    public ReserverCovoiturage(
            CovoiturageRepository covoiturageRepository,
            ParticipationCovoiturageIdRepository participationRepository,
            UtilisateurRepository utilisateurRepository) {
        this.covoiturageRepository = covoiturageRepository;
        this.participationRepository = participationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Réserve une place pour un utilisateur dans un covoiturage identifié.
     *
     * @param covoiturageId l'identifiant du covoiturage à rejoindre
     * @param utilisateurId l'identifiant de l'utilisateur qui réserve une place
     * @return la participation créée et persistée en base
     */
    @Transactional
    public ParticipationCovoiturage reserver(Long covoiturageId, Long utilisateurId) {

        // Étape 1 : Récupération du covoiturage et de l'utilisateur
        Covoiturage covoiturage = covoiturageRepository.findById(covoiturageId)
                .orElseThrow(() -> new IllegalArgumentException("Covoiturage introuvable avec l'ID : " + covoiturageId));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec l'ID : " + utilisateurId));

        // Étape 2 : SÉCURITÉ - L'organisateur ne peut pas participer à son propre trajet
        if (covoiturage.getOrganisateur() != null && covoiturage.getOrganisateur().getId().equals(utilisateurId)) {
            throw new IllegalStateException("L'organisateur ne peut pas participer à son propre trajet.");
        }

        // Étape 3 : SÉCURITÉ - Le conducteur ne peut pas s'inscrire comme passager
        if (covoiturage.getConducteur() != null && covoiturage.getConducteur().getId().equals(utilisateurId)) {
            throw new IllegalStateException("Le conducteur ne peut pas s'inscrire comme passager à son propre trajet.");
        }

        // Étape 4 : SÉCURITÉ - Vérifier qu'il reste de la place
        if (covoiturage.getNbPlacesRestantes() <= 0) {
            throw new IllegalStateException("Le covoiturage est complet.");
        }

        // Étape 5 : SÉCURITÉ - Vérifier que l'utilisateur n'est pas déjà inscrit
        ParticipationCovoiturageId cle = new ParticipationCovoiturageId();
        cle.setCovoiturageId(covoiturageId);
        cle.setUtilisateurId(utilisateurId);

        if (participationRepository.existsById(cle)) {
            throw new IllegalStateException("Cet utilisateur participe déjà à ce covoiturage.");
        }

        // Étape 6 : Décrémenter le nombre de places et sauvegarder le trajet
        covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesRestantes() - 1);
        covoiturageRepository.save(covoiturage);

        // Étape 7 : Créer et sauvegarder la nouvelle participation
        ParticipationCovoiturage participation = new ParticipationCovoiturage();
        participation.setId(cle);
        participation.setUtilisateur(utilisateur);
        participation.setCovoiturage(covoiturage);

        return participationRepository.save(participation);
    }
}