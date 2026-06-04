package com.diginamic.wemouv.dto;

import com.diginamic.wemouv.enums.Statut;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Objet de Transfert de Données (DTO) pour la requête de création ou modification d'un covoiturage.
 * <p>
 * Ce DTO est utilisé par les contrôleurs pour réceptionner les données envoyées par le Front-end.
 * Il permet d'alléger la requête réseau en utilisant de simples identifiants (ID) pour lier
 * le véhicule, l'organisateur et le conducteur, au lieu d'exiger les objets complets.
 * </p>
 */
public class CovoiturageRequest {

    /** L'adresse postale complète du point de départ. */
    private String adresseDepart;

    /** L'adresse postale complète du point d'arrivée. */
    private String adresseArrive;

    /** * La date et l'heure prévues pour le départ.
     * Le format attendu en JSON est strict : "yyyy-MM-dd'T'HH:mm:ss".
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDepart;

    /** * La date et l'heure de publication de l'annonce.
     * Le format attendu en JSON est strict : "yyyy-MM-dd'T'HH:mm:ss".
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;

    /** La durée estimée du trajet (en heures décimales). */
    private Double dureeTrajet;

    /** La distance totale du trajet en kilomètres. */
    private Double distanceKm;

    /** Le nombre total de places proposées dans le véhicule au moment de la création. */
    private int nbPlacesInitial;

    /** Le nombre de places encore disponibles pour les passagers. */
    private int nbPlacesRestantes;

    /** L'état actuel du trajet (Ex: CONFIRME, ANNULE, EN_ATTENTE). */
    private Statut statut;

    /** L'identifiant du véhicule (personnel ou de service) utilisé pour ce trajet. */
    private Long vehiculeId;

    /** L'identifiant du collaborateur qui organise et publie le trajet. */
    private Long organisateurId;

    /** L'identifiant du collaborateur qui conduira le véhicule. */
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