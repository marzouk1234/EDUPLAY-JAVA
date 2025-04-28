package com.example.demo4.services;

import com.example.demo4.entities.evaluation;
import com.example.demo4.utils.MyDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class evaluationService implements IevaluationService {

    private final Connection cnx;

    public evaluationService() {
        cnx = MyDB.getInstance().getCnx();
    }

    @Override
    public void ajouterevaluation(evaluation e) throws SQLException {
        String requete = "INSERT INTO evaluation (titre, type, date, image, etudiant_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, e.getTitre());
            pst.setString(2, e.getType());
            pst.setTimestamp(3, Timestamp.valueOf(e.getDate()));
            pst.setString(4, e.getImage());
            pst.setInt(5, e.getEtudiantId());
            pst.executeUpdate();
            System.out.println("Évaluation ajoutée avec succès");
        }
    }

    @Override
    public void ajouterevaluation(evaluation e, int idEtudiant) throws SQLException {
        e.setEtudiantId(idEtudiant); // Set the student ID before adding
        ajouterevaluation(e); // Reuse the existing add method
    }

    @Override
    public void modifierevaluation(evaluation e, int idEtudiant) throws SQLException {
        String req = "UPDATE evaluation SET titre = ?, type = ?, date = ?, image = ?, etudiant_id = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, e.getTitre());
            pst.setString(2, e.getType());
            pst.setTimestamp(3, Timestamp.valueOf(e.getDate()));
            pst.setString(4, e.getImage());
            pst.setInt(5, idEtudiant);
            pst.setInt(6, e.getId());
            pst.executeUpdate();
            System.out.println("Évaluation modifiée avec succès");
        }
    }

    @Override
    public void supprimerevaluation(evaluation e) throws SQLException {
        String req = "DELETE FROM evaluation WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, e.getId());
            pst.executeUpdate();
            System.out.println("Évaluation supprimée avec succès");
        }
    }

    @Override
    public List<evaluation> recupererevaluation() throws SQLException {
        List<evaluation> liste = new ArrayList<>();
        String s = "SELECT * FROM evaluation";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(s)) {
            while (rs.next()) {
                liste.add(new evaluation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("type"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("image"),
                        rs.getInt("etudiant_id")
                ));
            }
        }
        return liste;
    }

    // Additional methods (not part of the interface)
    public ObservableList<evaluation> chercherev(String chaine) {
        ObservableList<evaluation> myList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM evaluation WHERE titre LIKE ? ORDER BY titre";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "%" + chaine + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    myList.add(new evaluation(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("type"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getString("image"),
                            rs.getInt("etudiant_id")
                    ));
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return myList;
    }

    public List<evaluation> trierev() throws SQLException {
        List<evaluation> liste = new ArrayList<>();
        String s = "SELECT * FROM evaluation ORDER BY titre";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(s)) {
            while (rs.next()) {
                liste.add(new evaluation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("type"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("image"),
                        rs.getInt("etudiant_id")
                ));
            }
        }
        return liste;
    }

    public ObservableList<evaluation> getEvaluationsByStudent(int etudiantId) throws SQLException {
        ObservableList<evaluation> evaluationsList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM evaluation WHERE etudiant_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, etudiantId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    evaluationsList.add(new evaluation(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("type"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getString("image"),
                            rs.getInt("etudiant_id")
                    ));
                }
            }
        }
        return evaluationsList;
    }
}