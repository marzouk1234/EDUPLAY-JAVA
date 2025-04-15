-- Script de création/vérification de la table form_p

-- Vérifier si la base de données existe, sinon la créer
CREATE DATABASE IF NOT EXISTS ps;

-- Utiliser la base de données
USE ps;

-- Vérifier si la table existe, sinon la créer
CREATE TABLE IF NOT EXISTS form_p (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contenu TEXT NOT NULL,
    date_pub DATE NOT NULL,
    sujet VARCHAR(255) NOT NULL,
    auteur VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Afficher la structure de la table
DESCRIBE form_p;

-- Vérifier les données existantes
SELECT COUNT(*) AS 'Nombre de formulaires' FROM form_p; 