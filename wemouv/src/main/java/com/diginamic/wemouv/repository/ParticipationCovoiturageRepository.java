package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParticipationCovoiturageRepository extends JpaRepository<ParticipationCovoiturage, ParticipationCovoiturageId> {

    // 1. Récupère les participations d'un utilisateur pour les trajets FUTURS (En cours)
    List<ParticipationCovoiturage> findByUtilisateurIdAndCovoiturageDateDepartAfter(Long utilisateurId, LocalDateTime date);

    // 2. Récupère les participations d'un utilisateur pour les trajets PASSÉS (Historique)
    List<ParticipationCovoiturage> findByUtilisateurIdAndCovoiturageDateDepartBefore(Long utilisateurId, LocalDateTime date);
}