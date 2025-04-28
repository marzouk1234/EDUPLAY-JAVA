package com.example.demo4.services;

import com.example.demo4.entities.evaluation;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author asus
 */
public interface IevaluationService {

    // Ajouter une évaluation sans spécifier l'étudiant
    void ajouterevaluation(evaluation e) throws SQLException;

    // Ajouter une évaluation en spécifiant l'ID de l'étudiant
    void ajouterevaluation(evaluation e, int idEtudiant) throws SQLException;

    // Modifier une évaluation avec l'ID de l'étudiant (nouveau nom de la méthode)
    void modifierevaluation(evaluation e, int idEtudiant) throws SQLException;

    // Supprimer une évaluation
    void supprimerevaluation(evaluation e) throws SQLException;

    // Récupérer toutes les évaluations
    List<evaluation> recupererevaluation() throws SQLException;
}
