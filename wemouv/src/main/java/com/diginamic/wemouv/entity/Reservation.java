package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité JPA représentant une réservation de véhicule de service par un collaborateur.
 * <p>
 * Une réservation correspond à une période définie (date de début et date de fin)
 * durant laquelle un {@link Utilisateur} emprunte un {@link Vehicule} professionnel.
 * </p>
 * <p>
 * L'entité contient également un statut permettant de suivre le cycle de vie
 * de la réservation (ex : CONFIRME, ANNULE).
 * </p>
 * <p>
 * <b>Contraintes métier principales :</b>
 * <ul>
 * <li>Un véhicule ne peut être réservé que s'il est disponible sur la période.</li>
 * <li>Les dates de début et de fin sont obligatoires (non nulles).</li>
 * <li>Une réservation appartient à un seul utilisateur unique.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    /** Identifiant unique de la réservation (généré automatiquement). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Date et heure prévues pour la récupération du véhicule. */
    @Column(nullable = false)
    private LocalDateTime dateDebut;

    /** Date et heure prévues pour la restitution du véhicule. */
    @Column(nullable = false)
    private LocalDateTime dateFin;

    /** Le véhicule (de service) réservé par le collaborateur. */
    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    /** Le collaborateur ayant effectué la demande de réservation. */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /** Statut actuel de la réservation (ex: CONFIRME, ANNULE). */
    @Enumerated(EnumType.STRING)
    private Statut statut;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return l'identifiant de la réservation */
    public Long getId() { return id; }

    /** @param id identifiant de la réservation */
    public void setId(Long id) { this.id = id; }

    /** @return la date de début de la réservation */
    public LocalDateTime getDateDebut() { return dateDebut; }

    /** @param dateDebut date de début de la réservation */
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    /** @return la date de fin de la réservation */
    public LocalDateTime getDateFin() { return dateFin; }

    /** @param dateFin date de fin de la réservation */
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    /** @return le véhicule réservé */
    public Vehicule getVehicule() { return vehicule; }

    /** @param vehicule le véhicule réservé */
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    /** @return l'utilisateur ayant effectué la réservation */
    public Utilisateur getUtilisateur() { return utilisateur; }

    /** @param utilisateur l'utilisateur ayant effectué la réservation */
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    /** @return le statut de la réservation */
    public Statut getStatut() { return statut; }

    /** @param statut le statut de la réservation */
    public void setStatut(Statut statut) { this.statut = statut; }
}