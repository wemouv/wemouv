package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Vehicule;
import com.diginamic.wemouv.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
    }

    public Vehicule save(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }
}
