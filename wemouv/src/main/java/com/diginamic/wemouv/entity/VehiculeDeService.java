package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Disponibilite;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Entité JPA représentant un véhicule appartenant à la flotte de service de l'entreprise.
 * <p>
 * Cette entité hérite de {@link Vehicule} grâce à la stratégie de jointure (JOINED).
 * Elle ajoute des informations spécifiques aux véhicules gérés directement par
 * l'organisation (les voitures de fonction/service), notamment :
 * <ul>
 * <li>la localisation habituelle du véhicule (parking, agence, etc.)</li>
 * <li>un statut indiquant sa disponibilité technique ou logistique</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "vehicule_service")
public class VehiculeDeService extends Vehicule {

    /** Localisation actuelle ou parking d'attache du véhicule. */
    @Column(length = 255)
    private String localisation;

    /** Statut actuel du véhicule (ex : DISPONIBLE, EN_REPARATION, HORS_SERVICE). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Disponibilite disponibilite;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return la localisation actuelle du véhicule */
    public String getLocalisation() {
        return localisation;
    }

    /** @param localisation la localisation actuelle du véhicule */
    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    /** @return le statut de disponibilité du véhicule */
    public Disponibilite getDisponibilite() {
        return disponibilite;
    }

    /** @param disponibilite le nouveau statut de disponibilité du véhicule */
    public void setDisponibilite(Disponibilite disponibilite) {
        this.disponibilite = disponibilite;
    }
}