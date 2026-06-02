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

    public VehiculePerso save(VehiculePerso vehiculePerso) {
        return vehiculePersoRepository.save(vehiculePerso);
    }

    public void delete(Long id) {
        vehiculePersoRepository.deleteById(id);
    }
}
