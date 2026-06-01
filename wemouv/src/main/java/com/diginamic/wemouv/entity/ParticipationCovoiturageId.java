package com.diginamic.wemouv.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ParticipationCovoiturageId implements Serializable {
    private Long utilisateurId;
    private Long covoiturageId;


    // Getters & Setters

}
