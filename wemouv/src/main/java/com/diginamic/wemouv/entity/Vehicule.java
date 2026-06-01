package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "vehicule")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String immatriculation;

    @Enumerated(EnumType.STRING)
    private Marque marque;

    @Enumerated(EnumType.STRING)
    private Motorisation motorisation;

    @Column(nullable = false)
    private int nbPlace;

    private String photoUrl;

    private Double co2Km;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public Marque getMarque() { return marque; }
    public void setMarque(Marque marque) { this.marque = marque; }

    public Motorisation getMotorisation() { return motorisation; }
    public void setMotorisation(Motorisation motorisation) { this.motorisation = motorisation; }

    public int getNbPlace() { return nbPlace; }
    public void setNbPlace(int nbPlace) { this.nbPlace = nbPlace; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Double getCo2Km() { return co2Km; }
    public void setCo2Km(Double co2Km) { this.co2Km = co2Km; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
}