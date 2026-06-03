package com.diginamic.wemouv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Participation_Covoiturage")
public class ParticipationCovoiturage {

    @EmbeddedId
    private ParticipationCovoiturageId id;

    @ManyToOne
    @MapsId("utilisateurId")
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @MapsId("covoiturageId")
    @JoinColumn(name = "covoiturage_id")
    @JsonIgnore
    private Covoiturage covoiturage;


    public ParticipationCovoiturageId getId() {
        return id;
    }

    public void setId(ParticipationCovoiturageId id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Covoiturage getCovoiturage() {
        return covoiturage;
    }

    public void setCovoiturage(Covoiturage covoiturage) {
        this.covoiturage = covoiturage;
    }
}



