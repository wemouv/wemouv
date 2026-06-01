package com.diginamic.wemouv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Représente un véhicule personnel appartenant à un utilisateur.
 * <p>
 * Cette entité hérite de {@link Vehicule} et ajoute une information
 * spécifique : le propriétaire du véhicule.
 * </p>
 *
 * <p>
 * Les véhicules personnels peuvent être utilisés dans différents contextes :
 * <ul>
 *     <li>participation à des covoiturages</li>
 *     <li>déclarations de trajets personnels</li>
 *     <li>gestion des déplacements internes</li>
 * </ul>
 * </p>
 *
 * <p>
 * Contraintes :
 * <ul>
 *     <li>Chaque véhicule personnel doit obligatoirement avoir un propriétaire.</li>
 *     <li>Un utilisateur peut posséder plusieurs véhicules personnels.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "vehicule_perso")
public class VehiculePerso extends Vehicule {

    /** Utilisateur propriétaire du véhicule personnel. */
    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private Utilisateur proprietaire;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return le propriétaire du véhicule */
    public Utilisateur getProprietaire() {
        return proprietaire;
    }

    /** @param proprietaire utilisateur propriétaire du véhicule */
    public void setProprietaire(Utilisateur proprietaire) {
        this.proprietaire = proprietaire;
    }
}
