package com.diginamic.wemouv.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO pour la modification partielle d'une réservation de véhicule.
 * <p>
 * Seuls les champs renseignés dans la requête sont mis à jour.
 * Les champs {@code null} sont ignorés.
 * </p>
 */
public class ReservationModificationRequest {

    /** Nouvelle date et heure de prise du véhicule (optionnel). */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDebut;

    /** Nouvelle date et heure de restitution du véhicule (optionnel). */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFin;

    /** Identifiant du nouveau véhicule choisi (optionnel). */
    private Long vehiculeId;

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }
}
