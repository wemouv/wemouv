package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant un covoiturage proposé au sein de l'entreprise.
 * <p>
 * Cette classe est mappée directement sur la table {@code covoiturage} en base de données.
 * Elle contient l'intégralité des informations d'un trajet (adresses, dates, places...)
 * ainsi que ses relations vers le véhicule utilisé, les collaborateurs (organisateur/conducteur)
 * et les passagers inscrits (participations).
 * </p>
 */
@Entity
@Table(name = "covoiturage")
public class Covoiturage {

    /** Identifiant unique du covoiturage généré automatiquement par la BDD. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Adresse postale complète du point de départ. */
    @Column(nullable = false, length = 255)
    private String adresseDepart;

    /** Adresse postale complète du point d'arrivée. */
    @Column(nullable = false, length = 255)
    private String adresseArrive;

    /** Date et heure de départ effectif prévues pour le trajet. */
    @Column(nullable = false)
    private LocalDateTime dateDepart;

    /** Date et heure à laquelle l'annonce a été publiée dans le système. */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Durée estimée du trajet (en heures décimales). */
    private Double dureeTrajet;

    /** Distance totale estimée du trajet (en kilomètres). */
    private Double distanceKm;

    /** Nombre de places initialement proposées par le conducteur. */
    @Column(nullable = false)
    private int nbPlacesInitial;

    /** Nombre de places encore disponibles pour de nouveaux passagers. */
    @Column(nullable = false)
    private int nbPlacesRestantes;

    /** État d'avancement du trajet (EN_ATTENTE, CONFIRME, TERMINE, ANNULE). */
    @Enumerated(EnumType.STRING)
    private Statut statut;

    /** Le véhicule (personnel ou de service) affecté à ce trajet. */
    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    /** Le collaborateur qui a créé et publié cette annonce. */
    @ManyToOne
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Utilisateur organisateur;

    /** Le collaborateur qui prendra le volant (souvent l'organisateur lui-même). */
    @ManyToOne
    @JoinColumn(name = "conducteur_id", nullable = false)
    private Utilisateur conducteur;

    /** Liste des inscriptions (passagers) liées à ce trajet. */
    @OneToMany(
            mappedBy = "covoiturage",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<ParticipationCovoiturage> participations;

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

    /** @return la date de création de l'annonce */
    public LocalDateTime getDateCreation() { return dateCreation; }

    /** @param dateCreation date de création de l'annonce */
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

    /** @return le conducteur affecté au trajet */
    public Utilisateur getConducteur() { return conducteur; }

    /** @param conducteur le conducteur affecté au trajet */
    public void setConducteur(Utilisateur conducteur) { this.conducteur = conducteur; }

    /** @return la liste des participations (passagers inscrits) */
    public List<ParticipationCovoiturage> getParticipations() { return participations; }

    /** @param participations la liste des participations */
    public void setParticipations(List<ParticipationCovoiturage> participations) { this.participations = participations; }

    // -------------------------
    // Helper Methods (Utilitaires)
    // -------------------------

    /**
     * Ajoute un passager au covoiturage de manière sécurisée en mémoire.
     * * @param participation l'entité représentant l'inscription du passager
     */
    public void addParticipation(ParticipationCovoiturage participation) {
        if (this.participations == null) {
            this.participations = new ArrayList<>();
        }
        this.participations.add(participation);
    }

    /**
     * Retire un passager du covoiturage en mémoire.
     * * @param participation l'entité représentant l'inscription à retirer
     */
    public void removeParticipation(ParticipationCovoiturage participation) {
        if (this.participations != null) {
            this.participations.remove(participation);
        }
    }
}