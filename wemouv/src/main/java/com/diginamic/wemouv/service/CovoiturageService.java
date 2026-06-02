package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CovoiturageService {

    private final CovoiturageRepository covoiturageRepository;

    public CovoiturageService(CovoiturageRepository covoiturageRepository) {
        this.covoiturageRepository = covoiturageRepository;
    }

    public List<Covoiturage> findAll() {
        return covoiturageRepository.findAll();
    }

    public Covoiturage findById(Long id) {
        return covoiturageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Covoiturage introuvable"));
    }

    public List<Covoiturage> findByOrganisateur(Long organisateurId) {
        return covoiturageRepository.findByOrganisateurId(organisateurId);
    }

    public List<Covoiturage> findByVehicule(Long vehiculeId) {
        return covoiturageRepository.findByVehiculeId(vehiculeId);
    }

    public List<Covoiturage> findByStatut(Statut statut) {
        return covoiturageRepository.findByStatut(statut);
    }

    public Covoiturage create(Covoiturage covoiturage) {
        return covoiturageRepository.save(covoiturage);
    }

    public Covoiturage update(Long id,Covoiturage covoiturage) {
        return covoiturageRepository.save(covoiturage);
    }

    public void delete(Long id) {
        covoiturageRepository.deleteById(id);
    }

    public ParticipationCovoiturage participer(Long covoiturageId, Long utilisateurId) {
    return null;
    }


    public void annulerParticipation(Long covoiturageId, Long utilisateurId) {

    }
}


