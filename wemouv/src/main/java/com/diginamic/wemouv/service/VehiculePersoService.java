package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.VehiculePerso;
import com.diginamic.wemouv.repository.VehiculePersoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculePersoService {

    private final VehiculePersoRepository vehiculePersoRepository;

    public VehiculePersoService(VehiculePersoRepository vehiculePersoRepository) {
        this.vehiculePersoRepository = vehiculePersoRepository;
    }

    public List<VehiculePerso> findAll() {
        return vehiculePersoRepository.findAll();
    }

    public VehiculePerso findById(Long id) {
        return vehiculePersoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule perso introuvable"));
    }

    public VehiculePerso create(VehiculePerso vehiculePerso) {
        return vehiculePersoRepository.save(vehiculePerso);
    }

    public VehiculePerso update(Long id, VehiculePerso vehiculePerso) {
        VehiculePerso existing = findById(id);

        existing.setImmatriculation(vehiculePerso.getImmatriculation());
        existing.setMarque(vehiculePerso.getMarque());
        existing.setMotorisation(vehiculePerso.getMotorisation());
        existing.setNbPlace(vehiculePerso.getNbPlace());
        existing.setPhotoUrl(vehiculePerso.getPhotoUrl());
        existing.setCo2Km(vehiculePerso.getCo2Km());
        existing.setCategorie(vehiculePerso.getCategorie());
        existing.setProprietaire(vehiculePerso.getProprietaire());

        return vehiculePersoRepository.save(existing);
    }

    public void delete(Long id) {
        vehiculePersoRepository.deleteById(id);
    }

    public List<VehiculePerso> findByProprietaire(Long proprietaireId) {
        return vehiculePersoRepository.findByProprietaireId(proprietaireId);
    }
}
