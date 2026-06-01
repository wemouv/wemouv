package com.diginamic.wemouv.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 * Représente la clé primaire composite de l'entité ParticipationCovoiturage.
 * <p>
 * Cette clé est composée de :
 * <ul>
 *     <li>l'identifiant d'un utilisateur participant</li>
 *     <li>l'identifiant d'un covoiturage</li>
 * </ul>
 * </p>
 *
 * <p>
 * Elle est utilisée dans une relation ManyToMany matérialisée par une table
 * d'association contenant des informations supplémentaires (ex : statut, date d'inscription).
 * </p>
 *
 * <p>
 * La classe doit être :
 * <ul>
 *     <li>{@code @Embeddable} pour être intégrée dans une entité</li>
 *     <li>{@code Serializable} pour respecter les exigences JPA</li>
 *     <li>munie d'un constructeur vide (implicite ici)</li>
 *     <li>dotée de getters/setters</li>
 *     <li>idéalement accompagnée de equals() et hashCode() (à ajouter si nécessaire)</li>
 * </ul>
 * </p>
 */
@Embeddable
public class ParticipationCovoiturage implements Serializable {

    /** Identifiant de l'utilisateur participant au covoiturage. */
    private Long utilisateurId;

    /** Identifiant du covoiturage auquel l'utilisateur participe. */
    private Long covoiturageId;

    // --------------------
    // Getters & Setters
    // --------------------

    /**
     * @return l'identifiant de l'utilisateur
     */
    public Long getUtilisateurId() {
        return utilisateurId;
    }

    /**
     * @param utilisateurId identifiant de l'utilisateur participant
     */
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    /**
     * @return l'identifiant du covoiturage
     */
    public Long getCovoiturageId() {
        return covoiturageId;
    }

    /**
     * @param covoiturageId identifiant du covoiturage concerné
     */
    public void setCovoiturageId(Long covoiturageId) {
        this.covoiturageId = covoiturageId;
    }
}
