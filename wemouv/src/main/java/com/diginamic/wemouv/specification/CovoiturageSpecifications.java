package com.diginamic.wemouv.specification;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CovoiturageSpecifications {

    public static Specification<Covoiturage> hasDepart(String depart) {
        return (root, query, cb) ->
                depart == null ? null : cb.like(cb.lower(root.get("adresseDepart")), "%" + depart.toLowerCase() + "%");
    }

    public static Specification<Covoiturage> hasArrivee(String arrivee) {
        return (root, query, cb) ->
                arrivee == null ? null : cb.like(cb.lower(root.get("adresseArrive")), "%" + arrivee.toLowerCase() + "%");
    }

    public static Specification<Covoiturage> hasCategorieVehicule(String categorie) {
        return (root, query, cb) -> {
            if (categorie == null || categorie.isEmpty()) return null;
            return cb.equal(root.get("vehicule").get("categorie"), categorie);
        };
    }

    public static Specification<Covoiturage> hasStatut(Statut statut) {
        return (root, query, cb) ->
                statut == null ? null : cb.equal(root.get("statut"), statut);
    }
}
