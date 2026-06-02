package com.diginamic.wemouv.dto;

import com.diginamic.wemouv.enums.Statut;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CovoiturageRequest {

    private String adresseDepart;
    private String adresseArrive;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDepart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;
    private Double dureeTrajet;
    private Double distanceKm;
    private int nbPlacesInitial;
    private int nbPlacesRestantes;
    private Statut statut;

    private Long vehiculeId;
    private Long organisateurId;
    private Long conducteurId;

    public String getAdresseDepart() {
        return adresseDepart;
    }

    public void setAdresseDepart(String adresseDepart) {
        this.adresseDepart = adresseDepart;
    }

    public String getAdresseArrive() {
        return adresseArrive;
    }

    public void setAdresseArrive(String adresseArrive) {
        this.adresseArrive = adresseArrive;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Double getDureeTrajet() {
        return dureeTrajet;
    }

    public void setDureeTrajet(Double dureeTrajet) {
        this.dureeTrajet = dureeTrajet;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getNbPlacesInitial() {
        return nbPlacesInitial;
    }

    public void setNbPlacesInitial(int nbPlacesInitial) {
        this.nbPlacesInitial = nbPlacesInitial;
    }

    public int getNbPlacesRestantes() {
        return nbPlacesRestantes;
    }

    public void setNbPlacesRestantes(int nbPlacesRestantes) {
        this.nbPlacesRestantes = nbPlacesRestantes;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public Long getOrganisateurId() {
        return organisateurId;
    }

    public void setOrganisateurId(Long organisateurId) {
        this.organisateurId = organisateurId;
    }

    public Long getConducteurId() {
        return conducteurId;
    }

    public void setConducteurId(Long conducteurId) {
        this.conducteurId = conducteurId;
    }
}
