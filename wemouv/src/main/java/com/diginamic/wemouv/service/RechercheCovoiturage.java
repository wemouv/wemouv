package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service dédié à la recherche de covoiturages.
 * <p>
 * Une requête principale est d'abord exécutée via une méthode {@code findBy...}
 * du repository, puis des filtres complémentaires sont appliqués en Java pour
 * combiner tous les critères renseignés. Seuls les covoiturages disposant d'au
 * moins une place restante sont retournés.
 * </p>
 */
@Service
public class RechercheCovoiturage {

    private final CovoiturageRepository covoiturageRepository;

    /**
     * Constructeur avec injection du repository des covoiturages.
     *
     * @param covoiturageRepository repository utilisé pour accéder aux données des covoiturages
     */
    public RechercheCovoiturage(CovoiturageRepository covoiturageRepository) {
        this.covoiturageRepository = covoiturageRepository;
    }

    /**
     * Recherche des covoiturages correspondant aux critères fournis.
     *
     * @param depart  texte recherché dans l'adresse de départ (peut être {@code null})
     * @param arrivee texte recherché dans l'adresse d'arrivée (peut être {@code null})
     * @param date    date et heure de départ recherchées (peut être {@code null})
     * @param statut  statut du covoiturage recherché (peut être {@code null})
     * @return la liste des covoiturages correspondant aux critères et encore disponibles
     */
    public List<Covoiturage> rechercher(String depart, String arrivee, LocalDateTime date, Statut statut) {

        List<Covoiturage> resultats;

        // Requête principale en base : on choisit la méthode findBy la plus adaptée
        if (statut != null && date != null) {
            // Filtre combiné statut + date de départ
            resultats = covoiturageRepository.findByStatutAndDateDepart(statut, date);
        } else if (depart != null && !depart.isBlank()) {
            // Filtre sur l'adresse de départ (recherche partielle, insensible à la casse)
            resultats = covoiturageRepository.findByAdresseDepartContainingIgnoreCase(depart);
        } else if (arrivee != null && !arrivee.isBlank()) {
            // Filtre sur l'adresse d'arrivée (recherche partielle, insensible à la casse)
            resultats = covoiturageRepository.findByAdresseArriveContainingIgnoreCase(arrivee);
        } else if (statut != null) {
            // Filtre sur le statut du covoiturage
            resultats = covoiturageRepository.findByStatut(statut);
        } else if (date != null) {
            // Filtre sur la date de départ exacte
            resultats = covoiturageRepository.findByDateDepart(date);
        } else {
            // Aucun critère : récupération de tous les covoiturages
            resultats = covoiturageRepository.findAll();
        }

        // Filtre complémentaire : adresse de départ (pour combiner plusieurs critères)
        if (depart != null && !depart.isBlank()) {
            resultats = resultats.stream()
                    .filter(c -> c.getAdresseDepart().toLowerCase().contains(depart.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filtre complémentaire : adresse d'arrivée (pour combiner plusieurs critères)
        if (arrivee != null && !arrivee.isBlank()) {
            resultats = resultats.stream()
                    .filter(c -> c.getAdresseArrive().toLowerCase().contains(arrivee.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filtre complémentaire : statut (pour combiner plusieurs critères)
        if (statut != null) {
            resultats = resultats.stream()
                    .filter(c -> c.getStatut() == statut)
                    .collect(Collectors.toList());
        }

        // Filtre complémentaire : date de départ exacte (pour combiner plusieurs critères)
        if (date != null) {
            resultats = resultats.stream()
                    .filter(c -> c.getDateDepart().equals(date))
                    .collect(Collectors.toList());
        }

        // Filtre final : ne garder que les covoiturages avec au moins une place disponible
        return resultats.stream()
                .filter(c -> c.getNbPlacesRestantes() > 0)
                .collect(Collectors.toList());
    }
}
