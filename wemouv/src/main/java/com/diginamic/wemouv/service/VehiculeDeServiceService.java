package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.VehiculeDeService;
import com.diginamic.wemouv.repository.VehiculeDeServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeDeServiceService {

    private final VehiculeDeServiceRepository vehiculeDeServiceRepository;

    public VehiculeDeServiceService(VehiculeDeServiceRepository vehiculeDeServiceRepository) {
        this.vehiculeDeServiceRepository = vehiculeDeServiceRepository;
    }

    public List<VehiculeDeService> findAll() {
        return vehiculeDeServiceRepository.findAll();
    }

    public VehiculeDeService findById(Long id) {
        return vehiculeDeServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule de service introuvable"));
    }

    public VehiculeDeService create(VehiculeDeService vehiculeDeService) {
        return vehiculeDeServiceRepository.save(vehiculeDeService);
    }

    public VehiculeDeService update(Long id,VehiculeDeService vehiculeDeService) {
        return vehiculeDeServiceRepository.save(vehiculeDeService);
    }


    public void delete(Long id) {
        vehiculeDeServiceRepository.deleteById(id);
    }
}
