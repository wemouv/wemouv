package com.diginamic.wemouv.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entité JPA représentant la table de jointure pour l'inscription à un covoiturage.
 * <p>
 * Cette classe fait le lien (Many-To-Many) entre un {@link Utilisateur} (le passager)
 * et un {@link Covoiturage} (le trajet). Elle utilise une clé composite intégrée
 * pour garantir qu'un utilisateur ne peut pas s'inscrire deux fois au même trajet.
 * </p>
 */
@Entity
@Table(name = "Participation_Covoiturage")
public class ParticipationCovoiturage {

    /** * La clé primaire composite (utilisateur_id + covoiturage_id).
     * Injectée via la classe embarquée {@link ParticipationCovoiturageId}.
     */
    @EmbeddedId
    private ParticipationCovoiturageId id;

    /** * Le passager inscrit au trajet.
     * L'annotation {@code @MapsId} lie cette relation à la partie "utilisateurId" de la clé composite.
     */
    @ManyToOne
    @MapsId("utilisateurId")
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    /** * Le trajet de covoiturage concerné.
     * L'annotation {@code @MapsId} lie cette relation à la partie "covoiturageId" de la clé composite.
     * L'annotation {@code @JsonIgnore} est cruciale ici : elle empêche la sérialisation infinie en JSON.
     */
    @ManyToOne
    @MapsId("covoiturageId")
    @JoinColumn(name = "covoiturage_id")
    @JsonBackReference
    private Covoiturage covoiturage;


    // --------------------
    // Getters & Setters
    // --------------------

    /** @return la clé composite de la participation */
    public ParticipationCovoiturageId getId() {
        return id;
    }

    /** @param id la nouvelle clé composite */
    public void setId(ParticipationCovoiturageId id) {
        this.id = id;
    }

    /** @return l'utilisateur (passager) inscrit */
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /** @param utilisateur l'utilisateur (passager) à inscrire */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /** @return le covoiturage concerné */
    public Covoiturage getCovoiturage() {
        return covoiturage;
    }

    /** @param covoiturage le covoiturage ciblé */
    public void setCovoiturage(Covoiturage covoiturage) {
        this.covoiturage = covoiturage;
    }
}