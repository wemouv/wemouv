package com.diginamic.wemouv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entité JPA représentant un véhicule personnel appartenant à un collaborateur.
 * <p>
 * Cette entité hérite de {@link Vehicule} via la stratégie de jointure (JOINED).
 * Elle ajoute une information métier spécifique par rapport aux véhicules de la flotte :
 * le lien direct vers le collaborateur qui en est le propriétaire.
 * </p>
 * <p>
 * Les véhicules personnels sont principalement utilisés dans le cadre de la publication
 * de trajets en covoiturage.
 * </p>
 * <p>
 * <b>Contraintes métier :</b>
 * <ul>
 * <li>Chaque véhicule personnel doit obligatoirement être rattaché à un propriétaire (non null).</li>
 * <li>Un collaborateur (utilisateur) peut déclarer et posséder plusieurs véhicules personnels.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "vehicule_perso")
public class VehiculePerso extends Vehicule {

    /** Le collaborateur propriétaire de ce véhicule personnel. */
    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private Utilisateur proprietaire;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return le collaborateur propriétaire du véhicule */
    public Utilisateur getProprietaire() {
        return proprietaire;
    }

    /** @param proprietaire le collaborateur propriétaire du véhicule */
    public void setProprietaire(Utilisateur proprietaire) {
        this.proprietaire = proprietaire;
    }
}