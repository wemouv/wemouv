-- =============================================================================
-- Fichier : V2__ajout_avis_et_statut.sql
-- Description : Ajout du statut des covoiturages et création de la table des avis
-- =============================================================================

-- 1. Modification d'une table existante (ALTER TABLE)
-- On ajoute une colonne 'statut' (avec une valeur par défaut pour les anciens trajets)
-- et une colonne 'description' facultative.
ALTER TABLE covoiturage
ADD COLUMN statut VARCHAR(50) DEFAULT 'EN_ATTENTE' NOT NULL,
ADD COLUMN description VARCHAR(500);

-- 2. Création d'une nouvelle table (CREATE TABLE)
-- Table pour stocker les avis laissés par les utilisateurs sur un trajet
CREATE TABLE avis (
    id BIGINT AUTO_INCREMENT,
    note INT NOT NULL,
    commentaire TEXT,
    date_creation DATETIME NOT NULL,
    covoiturage_id BIGINT NOT NULL,
    auteur_id BIGINT NOT NULL,
    PRIMARY KEY (id),

    -- Contrainte pour s'assurer que la note est entre 1 et 5
    CONSTRAINT chk_note_valide CHECK (note >= 1 AND note <= 5),

    -- Clés étrangères : on lie l'avis au covoiturage et à l'utilisateur qui l'a écrit
    CONSTRAINT fk_avis_covoiturage FOREIGN KEY (covoiturage_id) REFERENCES covoiturage(id) ON DELETE CASCADE,
    CONSTRAINT fk_avis_auteur FOREIGN KEY (auteur_id) REFERENCES utilisateur(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;