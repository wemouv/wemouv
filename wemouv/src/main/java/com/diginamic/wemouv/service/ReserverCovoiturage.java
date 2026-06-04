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
 * <p>
 * Ce service centralise la logique métier liée à l'inscription d'un utilisateur
 * à un trajet partagé. Il persiste une ligne dans la table de jointure
 * (clé composite {@code utilisateur_id} + {@code covoiturage_id}) et met à jour
 * le nombre de places restantes du covoiturage concerné.
 * </p>
 * <p>
 * Il agit en complément de la recherche : une fois le trajet trouvé,
 * ce service valide et confirme la participation de manière sécurisée.
 * </p>
 */
@Service
public class ReserverCovoiturage {

    /**
     * Dépôt d'accès aux données des covoiturages.
     */
    private final CovoiturageRepository covoiturageRepository;

    /**
     * Dépôt d'accès aux données des participations (clés composites).
     */
    private final ParticipationCovoiturageIdRepository participationRepository;

    /**
     * Dépôt d'accès aux données des utilisateurs.
     */
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Constructeur avec injection des dépôts nécessaires à la réservation.
     * * @param covoiturageRepository   dépôt pour accéder aux covoiturages
     *
     * @param participationRepository dépôt pour gérer les participations
     * @param utilisateurRepository   dépôt pour accéder aux utilisateurs
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
     * <p>
     * La méthode effectue plusieurs contrôles (existence en base, places disponibles,
     * doublon de participation, et propriété du trajet) avant de valider l'inscription.
     * </p>
     *
     * @param covoiturageId l'identifiant du covoiturage à rejoindre
     * @param utilisateurId l'identifiant de l'utilisateur qui réserve une place
     * @return la participation créée et persistée en base
     * @throws RuntimeException      si le covoiturage ou l'utilisateur est introuvable
     * @throws IllegalStateException s'il ne reste plus de place, si l'utilisateur participe déjà,
     *                               ou s'il est le conducteur du trajet
     */
    @Transactional
    public ParticipationCovoiturage reserver(Long covoiturageId, Long utilisateurId) {

        // Étape 1 : récupération du covoiturage cible
        Covoiturage covoiturage = covoiturageRepository.findById(covoiturageId)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));

        // Étape 2 : récupération de l'utilisateur participant
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Étape 3 : RÈGLE MÉTIER - Le conducteur ne peut pas réserver son propre trajet
        if (covoiturage.getConducteur().getId().equals(utilisateurId)) {
            throw new IllegalStateException("Le conducteur ne peut pas s'inscrire comme passager à son propre trajet");
        }

        // Étape 4 : contrôle de la disponibilité des places
        if (covoiturage.getNbPlacesRestantes() <= 0) {
            throw new IllegalStateException("Aucune place disponible pour ce covoiturage");
        }

        // Étape 5 : contrôle de l'absence d'une participation déjà existante
        ParticipationCovoiturageId cle = new ParticipationCovoiturageId(utilisateurId, covoiturageId);
        if (participationRepository.existsById(cle)) {
            throw new IllegalStateException("Cet utilisateur participe déjà à ce covoiturage");
        }

        // Étape 6 : création de l'entité de participation avec sa clé composite
        ParticipationCovoiturage participation = new ParticipationCovoiturage();
        participation.setId(cle);
        participation.setUtilisateur(utilisateur);
        participation.setCovoiturage(covoiturage);

        // Étape 7 : enregistrement de la participation en base
        ParticipationCovoiturage participationSauvegardee = participationRepository.save(participation);

        // Étape 8 : mise à jour du nombre de places restantes sur le covoiturage
        covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesRestantes() - 1);
        covoiturageRepository.save(covoiturage);

        return participationSauvegardee;
    }
}