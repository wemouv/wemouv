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

 * Service dédié à la réservation d'une place dans un covoiturage.

 * <p>

 * Ce service centralise la logique métier liée à l'inscription d'un utilisateur

 * à un trajet partagé. Il persiste une ligne dans la table {@code Participation_Covoiturage}

 * (clé composite {@code utilisateur_id} + {@code covoiturage_id}) et met à jour

 * le nombre de places restantes du covoiturage concerné.
 
 * Ce service complète {@link RechercheCovoiturage} dans le parcours utilisateur :

 * la recherche permet de trouver un trajet disponible, la réservation permet

 * de confirmer la participation à ce trajet.

 * </p>

 */

@Service

public class ReserverCovoiturage {

    private final CovoiturageRepository covoiturageRepository;
    private final ParticipationCovoiturageIdRepository participationRepository;
    private final UtilisateurRepository utilisateurRepository;



    /**

     * Constructeur avec injection des repositories nécessaires à la réservation.
     * @param covoiturageRepository   repository utilisé pour accéder aux covoiturages
     * @param participationRepository repository utilisé pour gérer les participations
     * @param utilisateurRepository   repository utilisé pour accéder aux utilisateurs
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
     * La méthode effectue les vérifications suivantes avant toute persistance :
     * </p>
     * <ol>
     *     <li>existence du covoiturage en base</li>
     *     <li>existence de l'utilisateur en base</li>
     *    <li>disponibilité d'au moins une place restante</li>
     *     <li>absence d'une participation déjà enregistrée pour ce couple utilisateur/covoiturage</li>
     * </ol>
     * <p>
     * Si toutes les conditions sont remplies, une participation est créée dans
     * {@code Participation_Covoiturage} et {@code nbPlacesRestantes} est décrémenté
     * sur le covoiturage. L'ensemble des opérations est exécuté dans une transaction.
     * </p>
     *

     * @param covoiturageId identifiant du covoiturage à rejoindre
     * @param utilisateurId identifiant de l'utilisateur qui réserve une place
     * @return la participation créée et persistée en base
     * @throws RuntimeException      si le covoiturage ou l'utilisateur est introuvable
     * @throws IllegalStateException s'il ne reste plus de place ou si l'utilisateur participe déjà

     */

    @Transactional

    public ParticipationCovoiturage reserver(Long covoiturageId, Long utilisateurId) {
        
        // Étape 1 : récupération du covoiturage cible
        Covoiturage covoiturage = covoiturageRepository.findById(covoiturageId)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));

        // Étape 2 : récupération de l'utilisateur participant
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Étape 3 : contrôle de la disponibilité des places
        if (covoiturage.getNbPlacesRestantes() <= 0) {
            throw new IllegalStateException("Aucune place disponible pour ce covoiturage");
        }



        // Étape 4 : contrôle de l'absence d'une participation déjà existante

        ParticipationCovoiturageId cle = new ParticipationCovoiturageId(utilisateurId, covoiturageId);

        if (participationRepository.existsById(cle)) {

            throw new IllegalStateException("Cet utilisateur participe déjà à ce covoiturage");

        }

        // Étape 5 : création de l'entité de participation avec sa clé composite

        ParticipationCovoiturage participation = new ParticipationCovoiturage();

        participation.setId(cle);

        participation.setUtilisateur(utilisateur);

        participation.setCovoiturage(covoiturage);

        // Étape 6 : enregistrement de la participation en base

        ParticipationCovoiturage participationSauvegardee = participationRepository.save(participation);



        // Étape 7 : mise à jour du nombre de places restantes sur le covoiturage

        covoiturage.setNbPlacesRestantes(covoiturage.getNbPlacesRestantes() - 1);

        covoiturageRepository.save(covoiturage);

        return participationSauvegardee;

    }

}


