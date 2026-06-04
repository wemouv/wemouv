package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageIdRepository;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier dédié à l'annulation d'une participation à un covoiturage.
 * <p>
 * Ce service centralise la logique liée au retrait d'un utilisateur
 * d'un trajet partagé. Il supprime la ligne correspondante dans la table de jointure
 * et libère une place en incrémentant le compteur du covoiturage concerné.
 * </p>
 */
@Service
public class AnnuleParticiaptionCovoiturage {

    /** Dépôt d'accès aux données des covoiturages. */
    private final CovoiturageRepository covoiturageRepository;

    /** Dépôt d'accès aux données des participations (clés composites). */
    private final ParticipationCovoiturageIdRepository participationRepository;

    /** Dépôt d'accès aux données des utilisateurs. */
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Constructeur avec injection des dépôts nécessaires à l'annulation.
     *
     * @param covoiturageRepository   dépôt pour accéder aux covoiturages
     * @param participationRepository dépôt pour gérer les participations
     * @param utilisateurRepository   dépôt pour accéder aux utilisateurs
     */
    public AnnuleParticiaptionCovoiturage(
            CovoiturageRepository covoiturageRepository,
            ParticipationCovoiturageIdRepository participationRepository,
            UtilisateurRepository utilisateurRepository) {
        this.covoiturageRepository = covoiturageRepository;
        this.participationRepository = participationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Annule la participation d'un utilisateur à un covoiturage identifié.
     * <p>
     * La méthode effectue les vérifications d'existence (Covoiturage, Utilisateur, Participation)
     * avant toute suppression. Si tout est valide, la participation est détruite et
     * une place est libérée dans le véhicule.
     * L'ensemble des opérations est exécuté dans une transaction SQL sécurisée.
     * </p>
     *
     * @param covoiturageId l'identifiant du covoiturage concerné
     * @param utilisateurId l'identifiant de l'utilisateur qui annule sa réservation
     * @throws RuntimeException si le covoiturage, l'utilisateur ou la participation est introuvable
     */
    @Transactional // Démarrage de la transaction en base pour garantir l'intégrité des données
    public void annuler(Long covoiturageId, Long utilisateurId) {

        // Étape 1 : récupération du covoiturage cible
        Covoiturage covoiturage = covoiturageRepository.findById(covoiturageId)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));

        // Étape 2 : vérification de l'existence de l'utilisateur
        if (!utilisateurRepository.existsById(utilisateurId)) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        // Étape 3 : contrôle de l'existence de la participation à annuler
        ParticipationCovoiturageId cle = new ParticipationCovoiturageId(utilisateurId, covoiturageId);
        if (!participationRepository.existsById(cle)) {
            throw new RuntimeException("Participation introuvable");
        }

        // Étape 4 : suppression de la participation en base
        participationRepository.deleteById(cle);

        // Étape 5 : libération d'une place sur le covoiturage (sans dépasser le maximum initial)
        if (covoiturage.getNbPlacesRestantes() < covoiturage.getNbPlacesInitial()) {
            covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesRestantes() + 1);
            covoiturageRepository.save(covoiturage);
        }
    }
}