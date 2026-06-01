package com.diginamic.wemouv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicule_perso")
public class VehiculePerso extends Vehicule {

    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private Utilisateur proprietaire;
}
