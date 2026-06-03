package com.diginamic.wemouv.entity;



import jakarta.persistence.Embeddable;

import java.io.Serializable;

import java.util.Objects;



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

 *     <li>accompagnée de {@code equals()} et {@code hashCode()} pour les opérations JPA</li>

 * </ul>

 * </p>

 */

@Embeddable

public class ParticipationCovoiturageId implements Serializable {



    /** Identifiant de l'utilisateur participant au covoiturage. */

    private Long utilisateurId;



    /** Identifiant du covoiturage auquel l'utilisateur participe. */

    private Long covoiturageId;



    /**

     * Constructeur par défaut requis par JPA.

     */

    public ParticipationCovoiturageId() {

    }



    /**

     * Constructeur pratique pour créer une clé composite à partir des deux identifiants.

     *

     * @param utilisateurId identifiant de l'utilisateur participant

     * @param covoiturageId identifiant du covoiturage

     */

    public ParticipationCovoiturageId(Long utilisateurId, Long covoiturageId) {

        this.utilisateurId = utilisateurId;

        this.covoiturageId = covoiturageId;

    }



    /** @return l'identifiant de l'utilisateur participant */

    public Long getUtilisateurId() {

        return utilisateurId;

    }



    /** @param utilisateurId identifiant de l'utilisateur participant */

    public void setUtilisateurId(Long utilisateurId) {

        this.utilisateurId = utilisateurId;

    }



    /** @return l'identifiant du covoiturage */

    public Long getCovoiturageId() {

        return covoiturageId;

    }



    /** @param covoiturageId identifiant du covoiturage */

    public void setCovoiturageId(Long covoiturageId) {

        this.covoiturageId = covoiturageId;

    }



    @Override

    public boolean equals(Object o) {

        if (this == o) {

            return true;

        }

        if (o == null || getClass() != o.getClass()) {

            return false;

        }

        ParticipationCovoiturageId that = (ParticipationCovoiturageId) o;

        return Objects.equals(utilisateurId, that.utilisateurId)

                && Objects.equals(covoiturageId, that.covoiturageId);

    }



    @Override

    public int hashCode() {

        return Objects.hash(utilisateurId, covoiturageId);

    }

}


