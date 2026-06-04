package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier générique gérant la logique des véhicules de l'application.
 * <p>
 * Ce service assure les opérations CRUD (Création, Lecture, Mise à jour, Suppression)
 * de base pour la table globale des véhicules (personnels et de service).
 * </p>
 */
@Service
public class VehiculeService {

    /** Dépôt d'accès aux données génériques des véhicules. */
    private final VehiculeRepository vehiculeRepository;

    /**
     * Constructeur avec injection du dépôt des véhicules.
     *
     * @param vehiculeRepository le dépôt utilisé pour interagir avec la base de données
     */
    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    /**
     * Récupère la liste complète de tous les véhicules.
     *
     * @return la liste globale des véhicules
     */
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    /**
     * Recherche un véhicule par son identifiant unique.
     *
     * @param id l'identifiant recherché
     * @return le véhicule correspondant
     * @throws RuntimeException si le véhicule n'existe pas en base
     */
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
    }

    /**
     * Enregistre un nouveau véhicule en base de données.
     *
     * @param vehicule l'entité véhicule à insérer
     * @return le véhicule sauvegardé
     */
    public Vehicule create(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    /**
     * Met à jour les caractéristiques d'un véhicule existant.
     *
     * @param id l'identifiant du véhicule à modifier
     * @param vehicule les nouvelles données à appliquer
     * @return le véhicule mis à jour
     * @throws RuntimeException si le véhicule est introuvable
     */
    public Vehicule update(Long id, Vehicule vehicule) {
        if (!vehiculeRepository.existsById(id)) {
            throw new RuntimeException("Véhicule introuvable pour mise à jour");
        }
        // Force l'identifiant pour garantir la mise à jour de la bonne ligne
        vehicule.setId(id);
        return vehiculeRepository.save(vehicule);
    }

    /**
     * Supprime définitivement un véhicule de la base de données.
     *
     * @param id l'identifiant du véhicule à supprimer
     * @throws RuntimeException si le véhicule est introuvable
     */
    public void delete(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new RuntimeException("Véhicule introuvable pour suppression");
        }
        vehiculeRepository.deleteById(id);
    }
}