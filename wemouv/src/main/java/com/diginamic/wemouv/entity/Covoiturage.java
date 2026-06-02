package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Représente un covoiturage proposé par un utilisateur.
 * <p>
 * Cette entité contient toutes les informations nécessaires pour décrire un trajet :
 * adresses, dates, véhicule utilisé, organisateur, nombre de places, distance,
 * durée estimée et statut du covoiturage.
 * </p>
 *
 * <p>
 * Un covoiturage est lié :
 * <ul>
 *     <li>à un {@link Vehicule} utilisé pour le trajet</li>
 *     <li>à un {@link Utilisateur} organisateur</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "covoiturage")
public class Covoiturage {

    /** Identifiant unique du covoiturage. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Adresse de départ du trajet. */
    @Column(nullable = false, length = 255)
    private String adresseDepart;

    /** Adresse d'arrivée du trajet. */
    @Column(nullable = false, length = 255)
    private String adresseArrive;

    /** Date et heure prévues du départ. */
    @Column(nullable = false)
    private LocalDateTime dateDepart;

    /** Date et heure de création du covoiturage. */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Durée estimée du trajet en heures. */
    private Double dureeTrajet;

    /** Distance estimée du trajet en kilomètres. */
    private Double distanceKm;

    /** Nombre total de places disponibles au départ. */
    @Column(nullable = false)
    private int nbPlacesInitial;

    /** Nombre de places restantes encore disponibles. */
    @Column(nullable = false)
    private int nbPlacesRestantes;

    /** Statut actuel du covoiturage (ex : OUVERT, COMPLET, ANNULE). */
    @Enumerated(EnumType.STRING)
    private Statut statut;

    /** Véhicule utilisé pour effectuer le covoiturage. */
    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    /** Utilisateur organisateur du covoiturage. */
    @ManyToOne
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Utilisateur organisateur;

    @ManyToOne
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Utilisateur conducteur;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return l'identifiant du covoiturage */
    public Long getId() { return id; }

    /** @param id identifiant du covoiturage */
    public void setId(Long id) { this.id = id; }

    /** @return l'adresse de départ */
    public String getAdresseDepart() { return adresseDepart; }

    /** @param adresseDepart adresse de départ */
    public void setAdresseDepart(String adresseDepart) { this.adresseDepart = adresseDepart; }

    /** @return l'adresse d'arrivée */
    public String getAdresseArrive() { return adresseArrive; }

    /** @param adresseArrive adresse d'arrivée */
    public void setAdresseArrive(String adresseArrive) { this.adresseArrive = adresseArrive; }

    /** @return la date et heure de départ */
    public LocalDateTime getDateDepart() { return dateDepart; }

    /** @param dateDepart date et heure de départ */
    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }

    /** @return la date de création */
    public LocalDateTime getDateCreation() { return dateCreation; }

    /** @param dateCreation date de création */
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    /** @return la durée estimée du trajet */
    public Double getDureeTrajet() { return dureeTrajet; }

    /** @param dureeTrajet durée estimée du trajet */
    public void setDureeTrajet(Double dureeTrajet) { this.dureeTrajet = dureeTrajet; }

    /** @return la distance estimée en kilomètres */
    public Double getDistanceKm() { return distanceKm; }

    /** @param distanceKm distance estimée en kilomètres */
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    /** @return le nombre initial de places */
    public int getNbPlacesInitial() { return nbPlacesInitial; }

    /** @param nbPlacesInitial nombre initial de places */
    public void setNbPlacesInitial(int nbPlacesInitial) { this.nbPlacesInitial = nbPlacesInitial; }

    /** @return le nombre de places restantes */
    public int getNbPlacesRestantes() { return nbPlacesRestantes; }

    /** @param nbPlacesRestantes nombre de places restantes */
    public void setNbPlacesRestantes(int nbPlacesRestantes) { this.nbPlacesRestantes = nbPlacesRestantes; }

    /** @return le statut du covoiturage */
    public Statut getStatut() { return statut; }

    /** @param statut statut du covoiturage */
    public void setStatut(Statut statut) { this.statut = statut; }

    /** @return le véhicule utilisé */
    public Vehicule getVehicule() { return vehicule; }

    /** @param vehicule véhicule utilisé */
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    /** @return l'organisateur du covoiturage */
    public Utilisateur getOrganisateur() { return organisateur; }

    /** @param organisateur organisateur du covoiturage */
    public void setOrganisateur(Utilisateur organisateur) { this.organisateur = organisateur; }
}
