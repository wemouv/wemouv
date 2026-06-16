-- =============================================================================
-- Fichier : V1__initial_schema.sql
-- Description : Création initiale de la structure de la base de données (MariaDB)
-- =============================================================================

-- 1. Table Utilisateur
CREATE TABLE `utilisateur` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `adresse` varchar(255) DEFAULT NULL,
    `compte_actif` bit(1) DEFAULT NULL,
    `email` varchar(100) NOT NULL,
    `mot_de_passe` varchar(255) NOT NULL,
    `nom` varchar(100) NOT NULL,
    `prenom` varchar(100) NOT NULL,
    `role` enum('ADMIN','USER') NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_utilisateur_email` UNIQUE (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. Table Véhicule (Classe mère)
CREATE TABLE `vehicule` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `categorie`       enum('BERLINE','CITADINE','MINIBUS','SUV','UTILITAIRE') DEFAULT NULL,
    `co2_km` double DEFAULT NULL,
    `co2km` double DEFAULT NULL,
    `immatriculation` varchar(255) DEFAULT NULL,
    `marque`          enum('CITROEN','PEUGEOT','RENAULT','TOYOTA','VOLKSWAGEN') DEFAULT NULL,
    `motorisation`    enum('DIESEL','ELECTRIQUE','ESSENCE','HYBRIDE') DEFAULT NULL,
    `nb_place`        int(11) NOT NULL,
    `photo_url`       varchar(255) DEFAULT NULL,
    `modele`          varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 3. Table Véhicule Personnel (Héritage)
CREATE TABLE `vehicule_perso` (
    `id`              bigint(20) NOT NULL,
    `proprietaire_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_vp_vehicule` FOREIGN KEY (`id`) REFERENCES `vehicule` (`id`),
    CONSTRAINT `fk_vp_proprietaire` FOREIGN KEY (`proprietaire_id`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 4. Table Véhicule de Service (Héritage)
CREATE TABLE `vehicule_service` (
    `id` bigint(20) NOT NULL,
    `localisation`  varchar(255) DEFAULT NULL,
    `disponibilite` enum('DISPONIBLE','EN_REPARATION','HORS_SERVICE') NOT NULL,
    `statut`        enum('DISPONIBLE','EN_REPARATION','HORS_SERVICE') NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_vs_vehicule` FOREIGN KEY (`id`) REFERENCES `vehicule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 5. Table Covoiturage
CREATE TABLE `covoiturage` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `adresse_arrive`      varchar(255) NOT NULL,
    `adresse_depart`      varchar(255) NOT NULL,
    `date_creation`       datetime(6) NOT NULL,
    `date_depart`         datetime(6) NOT NULL,
    `distance_km` double DEFAULT NULL,
    `duree_trajet` double DEFAULT NULL,
    `nb_places_initial`   int(11) NOT NULL,
    `nb_places_restantes` int(11) NOT NULL,
    `statut`              enum('ANNULE','CONFIRME','EN_ATTENTE','OUVERT','TERMINE') NOT NULL,
    `organisateur_id`     bigint(20) NOT NULL,
    `vehicule_id`         bigint(20) NOT NULL,
    `conducteur_id`       bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_covoiturage_organisateur` FOREIGN KEY (`organisateur_id`) REFERENCES `utilisateur` (`id`),
    CONSTRAINT `fk_covoiturage_conducteur` FOREIGN KEY (`conducteur_id`) REFERENCES `utilisateur` (`id`),
    CONSTRAINT `fk_covoiturage_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 6. Table de jointure : Participations aux Covoiturages
CREATE TABLE `participation_covoiturage` (
    `covoiturage_id` bigint(20) NOT NULL,
    `utilisateur_id` bigint(20) NOT NULL,
    PRIMARY KEY (`covoiturage_id`, `utilisateur_id`),
    CONSTRAINT `fk_part_covoiturage` FOREIGN KEY (`covoiturage_id`) REFERENCES `covoiturage` (`id`),
    CONSTRAINT `fk_part_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 7. Table Réservation (Véhicules de Service)
CREATE TABLE `reservation` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `date_debut`     datetime(6) NOT NULL,
    `date_fin`       datetime(6) NOT NULL,
    `statut`         enum('ANNULE','CONFIRME','EN_ATTENTE','TERMINE') DEFAULT NULL,
    `utilisateur_id` bigint(20) NOT NULL,
    `vehicule_id`    bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_reservation_utilisateur` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`),
    CONSTRAINT `fk_reservation_vehicule` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;