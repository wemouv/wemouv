package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier dédié à la recherche multicritère d'annonces de covoiturage.
 * <p>
 * Ce service implémente une logique de filtrage hybride :
 * il exécute d'abord une requête principale via le dépôt (Repository) pour dégrossir les résultats,
 * puis applique des filtres complémentaires en mémoire (via l'API Stream de Java)
 * pour combiner tous les critères renseignés par l'utilisateur.
 * </p>
 */
@Service
public class RechercheCovoiturage {

    /** Dépôt d'accès aux données des covoiturages. */
    private final CovoiturageRepository covoiturageRepository;

    /**
     * Constructeur avec injection du dépôt des covoiturages.
     *
     * @param covoiturageRepository le dépôt utilisé pour l'accès aux données des trajets
     */
    public RechercheCovoiturage(CovoiturageRepository covoiturageRepository) {
        this.covoiturageRepository = covoiturageRepository;
    }

    /**
     * Recherche et filtre les covoiturages selon les critères optionnels fournis.
     * <p>
     * Peu importe les filtres appliqués, cette méthode garantit que seuls
     * les covoiturages disposant d'au moins une place restante sont retournés.
     * </p>
     *
     * @param depart  fragment ou totalité de l'adresse de départ (peut être {@code null})
     * @param arrivee fragment ou totalité de l'adresse d'arrivée (peut être {@code null})
     * @param date    date et heure exactes de départ (peut être {@code null})
     * @param statut  état actuel du covoiturage recherché (peut être {@code null})
     * @return la liste des covoiturages correspondant à tous les critères actifs et ayant des places disponibles
     */
    public List<Covoiturage> rechercher(String depart, String arrivee, LocalDateTime date, Statut statut) {

        List<Covoiturage> resultats;

        // Étape 1 : Requête principale en base (on choisit la méthode findBy la plus sélective possible)
        if (statut != null && date != null) {
            resultats = covoiturageRepository.findByStatutAndDateDepart(statut, date);
        } else if (depart != null && !depart.isBlank()) {
            resultats = covoiturageRepository.findByAdresseDepartContainingIgnoreCase(depart);
        } else if (arrivee != null && !arrivee.isBlank()) {
            resultats = covoiturageRepository.findByAdresseArriveContainingIgnoreCase(arrivee);
        } else if (statut != null) {
            resultats = covoiturageRepository.findByStatut(statut);
        } else if (date != null) {
            resultats = covoiturageRepository.findByDateDepart(date);
        } else {
            resultats = covoiturageRepository.findAll();
        }

        // Étape 2 : Filtres complémentaires en mémoire (pour croiser plusieurs critères)

        if (depart != null && !depart.isBlank()) {
            resultats = resultats.stream()
                    .filter(c -> c.getAdresseDepart().toLowerCase().contains(depart.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (arrivee != null && !arrivee.isBlank()) {
            resultats = resultats.stream()
                    .filter(c -> c.getAdresseArrive().toLowerCase().contains(arrivee.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (statut != null) {
            resultats = resultats.stream()
                    .filter(c -> c.getStatut() == statut)
                    .collect(Collectors.toList());
        }

        if (date != null) {
            resultats = resultats.stream()
                    .filter(c -> c.getDateDepart().equals(date))
                    .collect(Collectors.toList());
        }

        // Étape 3 : Filtre final métier (places obligatoirement disponibles)
        return resultats.stream()
                .filter(c -> c.getNbPlacesRestantes() > 0)
                .collect(Collectors.toList());
    }
}