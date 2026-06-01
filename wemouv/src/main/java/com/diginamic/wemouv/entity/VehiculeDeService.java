package com.diginamic.wemouv.entity;

import com.diginamic.wemouv.enums.Statut;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicule_service")
public class VehiculeDeService extends Vehicule {

    private String localisation;

    @Enumerated(EnumType.STRING)
    private Statut statut;
}
