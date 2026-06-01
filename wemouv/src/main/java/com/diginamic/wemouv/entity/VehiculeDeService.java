package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Représente un véhicule appartenant à la flotte de service de l'entreprise.
 * <p>
 * Cette entité hérite de {@link Vehicule} et ajoute des informations
 * spécifiques aux véhicules gérés directement par l'organisation, notamment :
 * <ul>
 *     <li>la localisation actuelle du véhicule</li>
 *     <li>un statut indiquant sa disponibilité ou son état</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les véhicules de service peuvent être utilisés dans plusieurs contextes :
 * <ul>
 *     <li>réservations internes</li>
 *     <li>missions professionnelles</li>
 *     <li>gestion de flotte</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "vehicule_service")
public class VehiculeDeService extends Vehicule {

    /** Localisation actuelle du véhicule (ex : agence, parking, ville). */
    private String localisation;

    /** Statut du véhicule (ex : DISPONIBLE, EN_MISSION, HORS_SERVICE). */
    @Enumerated(EnumType.STRING)
    private Statut statut;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return la localisation actuelle du véhicule */
    public String getLocalisation() {
        return localisation;
    }

    /** @param localisation localisation actuelle du véhicule */
    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    /** @return le statut du véhicule */
    public Statut getStatut() {
        return statut;
    }

    /** @param statut statut du véhicule */
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
}
