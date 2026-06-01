package com.diginamic.wemouv.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ParticipationCovoiturageId implements Serializable {
    private Long utilisateurId;
    private Long covoiturageId;


    // Getters & Setters

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Long getCovoiturageId() {
        return covoiturageId;
    }

    public void setCovoiturageId(Long covoiturageId) {
        this.covoiturageId = covoiturageId;
    }
}
