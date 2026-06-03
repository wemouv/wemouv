package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageIdRepository;
import com.diginamic.wemouv.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à l'annulation d'une participation à un covoiturage.
 * <p>
 * Ce service centralise la logique métier liée au retrait d'un utilisateur
 * d'un trajet partagé. Il supprime la ligne correspondante dans la table
 * {@code Participation_Covoiturage} et libère une place en incrémentant
 * {@code nbPlacesRestantes} sur le covoiturage concerné.
 * </p>
 *
 * <p>
 * Ce service complète {@link ReserverCovoiturage} dans le parcours utilisateur :
 * la réservation inscrit un passager, l'annulation retire cette inscription.
 * </p>
 */
@Service
public class AnnuleParticiaptionCovoiturage {

    private final CovoiturageRepository covoiturageRepository;
    private final ParticipationCovoiturageIdRepository participationRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Constructeur avec injection des repositories nécessaires à l'annulation.
     *
     * @param covoiturageRepository   repository utilisé pour accéder aux covoiturages
     * @param participationRepository repository utilisé pour gérer les participations
     * @param utilisateurRepository   repository utilisé pour accéder aux utilisateurs
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
     * La méthode effectue les vérifications suivantes avant toute suppression :
     * </p>
     * <ol>
     *     <li>existence du covoiturage en base</li>
     *     <li>existence de l'utilisateur en base</li>
     *     <li>existence d'une participation enregistrée pour ce couple utilisateur/covoiturage</li>
     * </ol>
     * <p>
     * Si toutes les conditions sont remplies, la participation est supprimée de
     * {@code Participation_Covoiturage} et {@code nbPlacesRestantes} est incrémenté
     * sur le covoiturage (sans dépasser {@code nbPlacesInitial}).
     * L'ensemble des opérations est exécuté dans une transaction.
     * </p>
     *
     * @param covoiturageId identifiant du covoiturage concerné
     * @param utilisateurId identifiant de l'utilisateur qui annule sa participation
     * @throws RuntimeException si le covoiturage, l'utilisateur ou la participation est introuvable
     */
    @Transactional //Demmarage de la transaction en base
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

