package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Categorie;
import com.diginamic.wemouv.enums.Marque;
import com.diginamic.wemouv.enums.Motorisation;
import jakarta.persistence.*;

/**
 * Entité JPA racine représentant un véhicule au sens large dans l'application WeMouv.
 * <p>
 * Cette classe constitue la base de la hiérarchie des véhicules (véhicules personnels
 * et véhicules de service), en s'appuyant sur l'héritage JPA avec la stratégie
 * {@link InheritanceType#JOINED}.
 * En base de données, cela crée une table mère {@code vehicule} contenant toutes
 * les propriétés partagées.
 * </p>
 * <p>
 * Un véhicule possède des caractéristiques essentielles telles que :
 * <ul>
 * <li>une immatriculation</li>
 * <li>une marque ({@link Marque}), un modèle et une motorisation ({@link Motorisation})</li>
 * <li>un nombre de places et une catégorie ({@link Categorie})</li>
 * <li>un taux d'émission de CO₂ par kilomètre</li>
 * </ul>
 * </p>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "vehicule")
public class Vehicule {

    /** Identifiant unique du véhicule (généré automatiquement). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro d'immatriculation officiel du véhicule. */
    @Column(nullable = false, unique = true)
    private String immatriculation;

    /** Marque du véhicule (ex : RENAULT, PEUGEOT, TESLA). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Marque marque;

    /** Modèle commercial du véhicule (ex : Clio, 308, Yaris). */
    @Column(length = 100)
    private String modele;

    /** Type de motorisation (ex : ESSENCE, DIESEL, ELECTRIQUE). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Motorisation motorisation;

    /** Nombre total de places disponibles dans le véhicule (conducteur inclus). */
    @Column(nullable = false)
    private int nbPlace;

    /** URL ou chemin d'accès vers la photographie du véhicule (optionnelle). */
    private String photoUrl;

    /** Émission de CO₂ estimée par kilomètre (en grammes). */
    private Double co2Km;

    /** Catégorie du véhicule selon sa taille ou son usage (ex : BERLINE, UTILITAIRE, SUV). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categorie categorie;

    // --------------------
    // Getters & Setters
    // --------------------

    /** @return l'identifiant du véhicule */
    public Long getId() { return id; }

    /** @param id identifiant du véhicule */
    public void setId(Long id) { this.id = id; }

    /** @return l'immatriculation du véhicule */
    public String getImmatriculation() { return immatriculation; }

    /** @param immatriculation numéro d'immatriculation */
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    /** @return la marque du véhicule */
    public Marque getMarque() { return marque; }

    /** @param marque marque du véhicule */
    public void setMarque(Marque marque) { this.marque = marque; }

    /** @return le modèle du véhicule */
    public String getModele() { return modele; }

    /** @param modele modèle du véhicule */
    public void setModele(String modele) { this.modele = modele; }

    /** @return la motorisation du véhicule */
    public Motorisation getMotorisation() { return motorisation; }

    /** @param motorisation type de motorisation */
    public void setMotorisation(Motorisation motorisation) { this.motorisation = motorisation; }

    /** @return le nombre de places */
    public int getNbPlace() { return nbPlace; }

    /** @param nbPlace nombre de places */
    public void setNbPlace(int nbPlace) { this.nbPlace = nbPlace; }

    /** @return l'URL de la photo du véhicule */
    public String getPhotoUrl() { return photoUrl; }

    /** @param photoUrl URL de la photo */
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    /** @return l'émission de CO₂ par kilomètre */
    public Double getCo2Km() { return co2Km; }

    /** @param co2Km émission de CO₂ par kilomètre */
    public void setCo2Km(Double co2Km) { this.co2Km = co2Km; }

    /** @return la catégorie du véhicule */
    public Categorie getCategorie() { return categorie; }

    /** @param categorie catégorie du véhicule */
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }
}