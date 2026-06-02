package com.diginamic.wemouv.service;

import com.diginamic.wemouv.entity.Covoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.enums.Statut;
import com.diginamic.wemouv.repository.CovoiturageRepository;
import com.diginamic.wemouv.repository.ParticipationCovoiturageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CovoiturageService {

    private final CovoiturageRepository covoiturageRepository;

    private final ParticipationCovoiturageRepository participationCovoiturageRepository;

    public CovoiturageService(CovoiturageRepository covoiturageRepository,
                              ParticipationCovoiturageRepository participationCovoiturageRepository) {
        this.covoiturageRepository = covoiturageRepository;
        this.participationCovoiturageRepository = participationCovoiturageRepository;
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

    public Map<String,List<Covoiturage>> getReservationsPassager(Long utilisateurId){
        LocalDateTime maintenant = LocalDateTime.now();

        // On récupère les participations via le repository
        List<ParticipationCovoiturage> futures = participationCovoiturageRepository
                .findByUtilisateurIdAndCovoiturageDateDepartAfter(utilisateurId, maintenant);

        List<ParticipationCovoiturage> passees = participationCovoiturageRepository
                .findByUtilisateurIdAndCovoiturageDateDepartBefore(utilisateurId, maintenant);

        List<Covoiturage> enCours = futures.stream()
                .map(ParticipationCovoiturage::getCovoiturage)
                .collect(Collectors.toList());

        List<Covoiturage> historique = passees.stream()
                .map(ParticipationCovoiturage::getCovoiturage)
                .collect(Collectors.toList());

        Map<String, List<Covoiturage>> resultat = new HashMap<>();
        resultat.put("enCours", enCours);
        resultat.put("historique", historique);

        return resultat;
    }
}


