package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "covoiturage")
public class Covoiturage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String adresseDepart;

    @Column(nullable = false, length = 255)
    private String adresseArrive;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private Double dureeTrajet;
    private Double distanceKm;

    @Column(nullable = false)
    private int nbPlacesInitial;

    @Column(nullable = false)
    private int nbPlacesRestantes;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Utilisateur organisateur;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdresseDepart() { return adresseDepart; }
    public void setAdresseDepart(String adresseDepart) { this.adresseDepart = adresseDepart; }

    public String getAdresseArrive() { return adresseArrive; }
    public void setAdresseArrive(String adresseArrive) { this.adresseArrive = adresseArrive; }

    public LocalDateTime getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Double getDureeTrajet() { return dureeTrajet; }
    public void setDureeTrajet(Double dureeTrajet) { this.dureeTrajet = dureeTrajet; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public int getNbPlacesInitial() { return nbPlacesInitial; }
    public void setNbPlacesInitial(int nbPlacesInitial) { this.nbPlacesInitial = nbPlacesInitial; }

    public int getNbPlacesRestantes() { return nbPlacesRestantes; }
    public void setNbPlacesRestantes(int nbPlacesRestantes) { this.nbPlacesRestantes = nbPlacesRestantes; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    public Utilisateur getOrganisateur() { return organisateur; }
    public void setOrganisateur(Utilisateur organisateur) { this.organisateur = organisateur; }
}