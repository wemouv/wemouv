package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Représente une réservation d'un véhicule par un utilisateur.
 * <p>
 * Une réservation correspond à une période définie (date de début et date de fin)
 * durant laquelle un {@link Utilisateur} utilise un {@link Vehicule}.
 * </p>
 *
 * <p>
 * L'entité contient également un statut permettant de suivre l'état de la réservation
 * (ex : EN_COURS, VALIDEE, ANNULEE).
 * </p>
 *
 * <p>
 * Contraintes principales :
 * <ul>
 *     <li>Un véhicule ne peut être réservé que s'il est disponible sur la période.</li>
 *     <li>Les dates de début et de fin sont obligatoires.</li>
 *     <li>Une réservation appartient à un utilisateur unique.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    /** Identifiant unique de la réservation. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Date et heure de début de la réservation. */
    @Column(nullable = false)
    private LocalDateTime dateDebut;

    /** Date et heure de fin de la réservation. */
    @Column(nullable = false)
    private LocalDateTime dateFin;

    /** Véhicule réservé par l'utilisateur. */
    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    /** Utilisateur ayant effectué la réservation. */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /** Statut actuel de la réservation (ex : EN_COURS, TERMINEE, ANNULEE). */
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

    /** @param vehicule véhicule réservé */
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    /** @return l'utilisateur ayant effectué la réservation */
    public Utilisateur getUtilisateur() { return utilisateur; }

    /** @param utilisateur utilisateur ayant effectué la réservation */
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    /** @return le statut de la réservation */
    public Statut getStatut() { return statut; }

    /** @param statut statut de la réservation */
    public void setStatut(Statut statut) { this.statut = statut; }
}
