package com.diginamic.wemouv.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Objet de Transfert de Données (DTO) pour la création d'une réservation de véhicule de service.
 * <p>
 * Ce DTO permet au Front-end d'envoyer uniquement les informations strictement nécessaires
 * pour bloquer un véhicule sur une période donnée. L'identité de l'emprunteur n'a pas
 * besoin d'être incluse car le Backend la déduit automatiquement du Token de sécurité.
 * </p>
 */
public class ReservationRequest {

    /** L'identifiant unique du véhicule de service ciblé. */
    private Long vehiculeId;

    /** * La date et l'heure de début de l'emprunt.
     * Le format attendu depuis le JSON est strict : "yyyy-MM-dd'T'HH:mm:ss".
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDebut;

    /** * La date et l'heure de fin (restitution du véhicule).
     * Le format attendu depuis le JSON est strict : "yyyy-MM-dd'T'HH:mm:ss".
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFin;

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

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
}