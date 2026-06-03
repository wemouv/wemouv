package com.diginamic.wemouv.repository;

import com.diginamic.wemouv.entity.ParticipationCovoiturage;
import com.diginamic.wemouv.entity.ParticipationCovoiturageId;
import com.diginamic.wemouv.entity.Reservation;
import com.diginamic.wemouv.enums.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParticipationCovoiturageIdRepository
        extends JpaRepository<ParticipationCovoiturage, ParticipationCovoiturageId> {

    boolean existsByUtilisateurIdAndCovoiturageId(Long utilisateurId, Long covoiturageId);
}
